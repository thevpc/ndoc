package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.resources.NDocResourceMonitor;
import net.thevpc.ndoc.engine.renderer.elem2d.text.util.TextUtils;
import net.thevpc.ndoc.engine.renderer.screen.components.*;


import net.thevpc.ndoc.engine.renderer.screen.utils.JPopupMenuHelper;
import net.thevpc.ndoc.spi.base.renderer.HImageUtils;
import net.thevpc.ndoc.spi.renderer.*;
import net.thevpc.ndoc.spi.util.PagesHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Timer;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NLiteral;

public class DocumentView {

    private NDocument document;
    private NDocDocumentRendererSupplier documentSupplier;
    private NDocEngine engine;
    private int currentPageIndex;
    private List<PageView> pageViews = new ArrayList<>();
    private JFrame frame;
    private ContentPanel contentPane;
    private PageView currentShowingPage;
    private Map<String, PageView> pagesMapById = new HashMap<>();
    private Map<Integer, PageView> pagesMapByIndex = new HashMap<>();
    private NDocNodeRendererManager rendererManager;
    private Timer timer;
    private boolean inCheckResourcesChanged;
    private boolean inLoadDocument;
    private Throwable currentThrowable;
    private NDocLogger messages;
    private NDocDocumentRendererListener listener;
    private NDocDocumentRendererContext rendererContext = new NDocDocumentRendererContextImpl();
    private boolean isShown;

    public DocumentView(NDocDocumentRendererSupplier documentSupplier,
                        NDocEngine engine, NDocDocumentRendererListener listener,
                        NDocLogger messages) {
        this.documentSupplier = documentSupplier;
        this.listener = listener;
        this.engine = engine;
        this.messages = messages;
        this.rendererManager = engine.renderManager();

        frame = new JFrame();
        frame.setTitle("NDoc Viewer");
        frame.setIconImage(
                HImageUtils.resizeImage(
                        new ImageIcon(getClass().getResource("/net/thevpc/ndoc/ndoc.png")).getImage(),
                        16, 16)
        );
        contentPane = new ContentPanel();
//        contentPane.setFocusTraversalKeysEnabled(false);
        frame.setContentPane(contentPane);
        reloadDocumentAsync();
        frame.setSize(PageView.REF_SIZE);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        prepareContentPane();
        timer = new Timer("DocumentViewResourcesMonitor", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                animate();
            }
        }, 10, 10);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkResourcesChanged();
            }
        }, 3000, 1000);
    }

    private void animate() {
        this.contentPane.invalidate();
        this.contentPane.repaint();
    }

    private void checkResourcesChanged() {
        if (inLoadDocument) {
            return;
        }
        if (inCheckResourcesChanged) {
            return;
        }
        this.inCheckResourcesChanged = true;
        try {
            if (inLoadDocument) {
                return;
            }
            if (document != null) {
                NDocResourceMonitor r = document.resources();
                if (r.changed()) {
                    reloadDocumentAsync();
                }
            }
        } finally {
            this.inCheckResourcesChanged = false;
        }
    }

    public boolean isLoading() {
        return inLoadDocument;
    }

    public NDocNodeRendererManager rendererManager() {
        return rendererManager;
    }

    public String getPageSourceName() {
        Object s = getPageSource();
        if (s != null) {
            if (s instanceof NDocResource) {
                NPath path = ((NDocResource) s).path().orNull();
                if (path != null) {
                    return path.getName();
                }
            }
        }
        return null;
    }

    public int getPageUserIndex() {
        int index = currentShowingPage == null ? 0 : currentShowingPage.index();
        int currentIndex = index + 1;
        return currentIndex;
    }

    public int getPageIndex() {
        int index = currentShowingPage == null ? 0 : currentShowingPage.index();
        return index;
    }

    public Object getPageSource() {
        NDocNode p = currentShowingPage == null ? null : currentShowingPage.getPage();
        Object s = null;
        while (p != null && s == null) {
            s = p.source();
            p = p.parent();
        }
        return s;
    }

    public NDocLogger messages() {
        return messages;
    }

    public class ContentPanel extends JPanel {

        CardLayout cardLayout;
        List<NDocDocumentLayer> layers = new ArrayList<>();

        public ContentPanel() {
            this.cardLayout = new CardLayout();
            setLayout(cardLayout);
            layers.add(new PizzaProgressLayer());
            layers.add(new PageIndexSimpleLayer());
            layers.add(new SourceNameSimpleLayer());
            layers.add(new RuntimeProgressLayer());
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2d = (Graphics2D) g;
            NDocGraphics hg = engine.createGraphics(g2d);
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = getSize();
            for (NDocDocumentLayer filter : layers) {
                filter.draw(DocumentView.this, size, hg);
            }
            if (currentThrowable != null) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                TextUtils.drawThrowable(currentThrowable, new Rectangle2D.Double(20, 20, 1000, 1000), g2d);
            }
        }

        public void doShow(String id) {
            cardLayout.show(contentPane, id);
        }
    }

    public void prepareContentPane() {
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    nextPage();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }
        });

        contentPane.setFocusTraversalKeysEnabled(false);
        contentPane.setFocusable(true);
        contentPane.requestFocus();
//        this.requestFocusInWindow();
        contentPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_UP: {
                        if (e.isControlDown()) {
                            lastPage();
                        } else {
                            nextPage();
                        }

                        break;
                    }
                    case KeyEvent.VK_F5: {
                        reloadDocumentAsync();
                        break;
                    }
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_DOWN: {
                        if (e.isControlDown()) {
                            firstPage();
                        } else {
                            previousPage();
                        }
                        break;
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
    }


    private void showPopupMenu(MouseEvent e) {
        JPopupMenuHelper popupMenu = new JPopupMenuHelper();
        popupMenu.addMenuItem("Save as PDF", ev -> {
            PdfConfigDialog configDialog = new PdfConfigDialog((Frame) SwingUtilities.getWindowAncestor((Component) e.getSource()));
            configDialog.setVisible(true);
            if (configDialog.isConfirmed()) {
                NDocDocumentStreamRendererConfig config = configDialog.getConfig();

                if (document != null && listener != null) {
                    listener.onSaveDocument(document, config);
                } else {
                    System.err.println("saveDocumentListener is null or document is null");
                }
            }
        });

        NDocResource source = document.root().source();
        if (source != null) {
            NPath path = source.path().orNull();
            if (path != null) {
                File file = path.toFile().orNull();
                if (file != null) {
                    popupMenu.addMenuItem(file.isDirectory() ? "Open Project in Explorer" : "Open Project",
                            ev -> {
                                try {
                                    Desktop.getDesktop().open(file);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                }
            }
        }
        popupMenu.addSeparator().addMenuItem("First Page", ev -> {
            firstPage();
        });
        popupMenu.addMenuItem("Last Page", ev -> {
            lastPage();
        });
        popupMenu.addMenuItem("Goto Page...", ev -> {
            String s = JOptionPane.showInputDialog("Page Number?");
            Integer v = NLiteral.of(s).asInt().orElse(-1);
            if (v > 0) {
                showPage(v - 1);
            }
        });
        popupMenu.show(e);
    }


    private void reloadDocumentAsync() {
        if (inLoadDocument) {
            return;
        }
        new Thread(() -> {
            reloadDocumentSync();
            if (!isShown) {
                isShown = true;
                show();
            }
        }).start();
    }

    private boolean reloadDocumentSync() {
        if (inLoadDocument) {
            return false;
        }
        listener.onStartLoadingDocument();
        this.inLoadDocument = true;
        try {
            PageView oldPage = this.currentShowingPage;
            int oldIndex = 0;
            String oldId = null;
            if (oldPage != null) {
                oldIndex = oldPage.index();
                oldId = oldPage.id();
            }
            this.currentShowingPage = null;
            this.currentThrowable = null;
            try {
                NDocument rawDocument = documentSupplier.get(rendererContext);
                NDocResource source = rawDocument.root().source();
                SwingUtilities.invokeLater(() -> {
                    if (source == null) {
                        frame.setTitle("H Document Viewer");
                    } else {
                        frame.setTitle(String.valueOf(source));
                    }
                });
                listener.onChangedRawDocument(rawDocument);
                NDocument compiledDocument = engine.compileDocument(rawDocument.copy(), messages).get();
                listener.onChangedCompiledDocument(compiledDocument);
                document = compiledDocument;
            } catch (Exception ex) {
                this.currentThrowable = ex;
            }
            if (document == null) {
                document = engine.documentFactory().ofDocument();
            }
            document.resources().save();
            List<NDocNode> pages = PagesHelper.resolvePages(document);
            pageViews.clear();
            contentPane.removeAll();
            pagesMapById.clear();
            pagesMapByIndex.clear();
            for (int i = 0; i < pages.size(); i++) {
                NDocNode page = pages.get(i);
                pageViews.add(createPageView(page, i));
            }
            if (pageViews.isEmpty()) {
                pageViews.add(createPageView(
                        engine.documentFactory().ofPage(),
                        0
                ));
            }
            for (PageView pageView : pageViews) {
                contentPane.add(pageView.component(), pageView.id());
                pagesMapById.put(pageView.id(), pageView);
                pagesMapByIndex.put(pageView.index(), pageView);
            }
            if (oldId != null) {
                PageView oo = pagesMapById.get(oldId);
                if (oo != null) {
                    showPage(oo.index());
                } else {
                    oo = pagesMapByIndex.get(oldIndex);
                    if (oo != null) {
                        showPage(oo.index());
                    } else {
                        showPage(0);
                    }
                }
            } else {
                showPage(0);
            }
        } finally {
            this.inLoadDocument = false;
            listener.onEndLoadingDocument();
        }
        return true;
    }

    private void lastPage() {
        showPage(getPagesCount() - 1);
    }

    private void firstPage() {
        showPage(0);
    }

    public PageView createPageView(NDocNode node, int index) {
        return new PageView(
                node, index,
                engine(),
                rendererManager(),
                messages()
        );
    }

    public void showPage(PageView pv) {
        if (currentShowingPage != null) {
            currentShowingPage.onHide();
        }
        this.currentShowingPage = pv;
        if (pv != null) {
            listener.onChangedPage(pv.getPage());
            this.currentShowingPage.onShow();
            SwingUtilities.invokeLater(() -> contentPane.doShow(pv.id()));
        } else {
            listener.onChangedPage(null);
        }
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public int getPagesCount() {
        return pageViews.size();
    }

    public void show() {
        showPage(0);
    }

    public void showPage(int index) {
        int count = getPagesCount();
        if (index >= count) {
            index = count - 1;
        } else if (index < 0) {
            index = 0;
        }
        PageView pageView = pageViews.get(index);
        this.showPage(pageView);
    }

    public NDocEngine engine() {
        return engine;
    }

    public void nextPage() {
        if (currentShowingPage != null) {
            int i = currentShowingPage.index();
            if (i + 1 < pageViews.size()) {
                showPage(i + 1);
            }
        }
    }

    public void previousPage() {
        if (currentShowingPage != null) {
            int i = currentShowingPage.index();
            if (i - 1 >= 0) {
                showPage(i - 1);
            }
        }
    }

    private class NDocDocumentRendererContextImpl implements NDocDocumentRendererContext {

        public NDocDocumentRendererContextImpl() {
        }

        @Override
        public NDocLogger messages() {
            return messages;
        }
    }

    public class PdfConfigDialog extends JDialog {
        private JRadioButton portraitRadioButton;
        private JRadioButton landscapeRadioButton;
        private JTextField gridXField;
        private JTextField gridYField;
        private JComboBox<String> sizePageComboBox;
        private JCheckBox showPageNumberCheckBox;
        private JCheckBox showFileNameCheckBox;
        private JCheckBox showDateCheckBox;
        private JTextField marginTopField;
        private JTextField marginBottomField;
        private JTextField marginLeftField;
        private JTextField marginRightField;
        private boolean confirmed;

        public PdfConfigDialog(Frame parent) {
            super(parent, "PDF Configuration", true);
            setLayout(new BorderLayout());
            getContentPane().setBackground(Color.WHITE);

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(33, 150, 243));
            headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            JLabel titleLabel = new JLabel("PDF Configuration");
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            headerPanel.add(titleLabel);
            add(headerPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new GridLayout(0, 2, 10, 10));
            contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            contentPanel.setBackground(Color.WHITE);

            addLabelAndComponent(contentPanel, "Orientation:", createOrientationPanel());
            addLabelAndComponent(contentPanel, "Grid X:", gridXField = createTextField());
            addLabelAndComponent(contentPanel, "Grid Y:", gridYField = createTextField());
            addLabelAndComponent(contentPanel, "Page Size:", sizePageComboBox = createComboBox(new String[]{"Small (300x300)", "Medium (600x600)", "Large (900x900)", "Max (1200x1200)"}));
            addLabelAndComponent(contentPanel, "Show Page Number:", showPageNumberCheckBox = createCheckBox());
            addLabelAndComponent(contentPanel, "Show File Name:", showFileNameCheckBox = createCheckBox());
            addLabelAndComponent(contentPanel, "Show Date:", showDateCheckBox = createCheckBox());
            addLabelAndComponent(contentPanel, "Margin Top:", marginTopField = createTextField());
            addLabelAndComponent(contentPanel, "Margin Bottom:", marginBottomField = createTextField());
            addLabelAndComponent(contentPanel, "Margin Left:", marginLeftField = createTextField());
            addLabelAndComponent(contentPanel, "Margin Right:", marginRightField = createTextField());

            add(contentPanel, BorderLayout.CENTER);

            JPanel footerPanel = new JPanel();
            footerPanel.setBackground(Color.WHITE);
            footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            JButton okButton = createButton("OK", new Color(76, 175, 80));
            okButton.addActionListener(e -> {
                confirmed = true;
                setVisible(false);
            });

            JButton cancelButton = createButton("Cancel", new Color(244, 67, 54));
            cancelButton.addActionListener(e -> {
                confirmed = false;
                setVisible(false);
            });

            footerPanel.add(okButton);
            footerPanel.add(cancelButton);
            add(footerPanel, BorderLayout.SOUTH);

            pack();
            setLocationRelativeTo(parent);
            setResizable(false);
        }

        private void addLabelAndComponent(JPanel panel, String labelText, JComponent component) {
            JLabel label = new JLabel(labelText);
            label.setForeground(new Color(33, 150, 243));
            label.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(label);
            panel.add(component);
        }

        private JPanel createOrientationPanel() {
            JPanel orientationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            orientationPanel.setBackground(Color.WHITE);
            ButtonGroup orientationGroup = new ButtonGroup();
            portraitRadioButton = createRadioButton("Portrait", false);
            landscapeRadioButton = createRadioButton("Landscape", true);
            orientationGroup.add(portraitRadioButton);
            orientationGroup.add(landscapeRadioButton);
            orientationPanel.add(portraitRadioButton);
            orientationPanel.add(landscapeRadioButton);
            return orientationPanel;
        }

        private JRadioButton createRadioButton(String text, boolean selected) {
            JRadioButton radioButton = new JRadioButton(text, selected);
            radioButton.setBackground(Color.WHITE);
            radioButton.setForeground(new Color(33, 150, 243));
            radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
            return radioButton;
        }

        private JTextField createTextField() {
            JTextField textField = new JTextField();
            textField.setBackground(new Color(245, 245, 245));
            textField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            return textField;
        }

        private JComboBox<String> createComboBox(String[] items) {
            JComboBox<String> comboBox = new JComboBox<>(items);
            comboBox.setBackground(new Color(245, 245, 245));
            comboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            return comboBox;
        }

        private JCheckBox createCheckBox() {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(Color.WHITE);
            checkBox.setForeground(new Color(33, 150, 243));
            checkBox.setFont(new Font("Arial", Font.PLAIN, 14));
            return checkBox;
        }

        private JButton createButton(String text, Color backgroundColor) {
            JButton button = new JButton(text);
            button.setBackground(backgroundColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setPreferredSize(new Dimension(100, 40));
            return button;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public NDocDocumentStreamRendererConfig getConfig() {
            NDocDocumentStreamRendererConfig config = new NDocDocumentStreamRendererConfig();
            config.setOrientation(portraitRadioButton.isSelected() ? NDocPageOrientation.PORTRAIT : NDocPageOrientation.LANDSCAPE);
            config.setGridX(NLiteral.of(gridXField.getText()).asInt().orElse(1));
            config.setGridY(NLiteral.of(gridYField.getText()).asInt().orElse(1));
            switch ((String) sizePageComboBox.getSelectedItem()) {
                case "Small (300x300)":
                    config.setPageWidth(300);
                    config.setPageHeight(300);
                    break;
                case "Medium (600x600)":
                    config.setPageWidth(600);
                    config.setPageHeight(600);
                    break;
                case "Large (900x900)":
                    config.setPageWidth(900);
                    config.setPageHeight(900);
                    break;
                case "Max (1200x1200)":
                    config.setPageWidth(1200);
                    config.setPageHeight(1200);
                    break;
            }
            config.setShowPageNumber(showPageNumberCheckBox.isSelected());
            config.setShowFileName(showFileNameCheckBox.isSelected());
            config.setShowDate(showDateCheckBox.isSelected());
            config.setMarginTop(NLiteral.of(marginTopField.getText()).asFloat().orElse(0f));
            config.setMarginBottom(NLiteral.of(marginBottomField.getText()).asFloat().orElse(0f));
            config.setMarginLeft(NLiteral.of(marginLeftField.getText()).asFloat().orElse(0f));
            config.setMarginRight(NLiteral.of(marginRightField.getText()).asFloat().orElse(0f));
            return config;
        }
    }


}
