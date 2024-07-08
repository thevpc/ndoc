package net.thevpc.halfa.engine.renderer.screen;
//import net.thevpc.halfa.main.ServiceHelper;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResourceMonitor;
import net.thevpc.halfa.api.util.NPathHResource;
import net.thevpc.halfa.engine.renderer.elem2d.text.util.TextUtils;
import net.thevpc.halfa.engine.renderer.screen.components.PizzaProgressLayer;
import net.thevpc.halfa.engine.renderer.screen.components.HDocumentLayer;
import net.thevpc.halfa.engine.renderer.screen.components.PageIndexSimpleLayer;
import net.thevpc.halfa.engine.renderer.screen.components.SourceNameSimpleLayer;


import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.halfa.spi.util.PagesHelper;
import net.thevpc.nuts.NSession;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.Timer;

import net.thevpc.halfa.engine.renderer.common.elem2d.ImageUtils;

public class DocumentView {

    private HDocument document;
    private HDocumentRendererSupplier documentSupplier;
    private HEngine engine;
    private int currentPageIndex;
    private List<PageView> pageViews = new ArrayList<>();
    private NSession session;
    private JFrame frame;
    private ContentPanel contentPane;
    private PageView currentShowingPage;
    private Map<String, PageView> pagesMapById = new HashMap<>();
    private Map<Integer, PageView> pagesMapByIndex = new HashMap<>();
    private HNodeRendererManager rendererManager;
    private Timer timer;
    private boolean inCheckResourcesChanged;
    private boolean inLoadDocument;
    private Throwable currentThrowable;
    private HMessageList messages;
    private HDocumentRendererListener listener;
    private HDocumentRendererContext rendererContext = new HDocumentRendererContextImpl();

    public DocumentView(HDocumentRendererSupplier documentSupplier,
                        HEngine engine, HDocumentRendererListener listener,
                        HMessageList messages,
                        NSession session) {
        this.documentSupplier = documentSupplier;
        this.listener = listener;
        this.engine = engine;
        this.messages = messages;
        this.session = session;
        this.rendererManager = engine.renderManager();

        frame = new JFrame();
        frame.setTitle("H Document Viewer");
        frame.setIconImage(
                ImageUtils.resizeImage(
                        new ImageIcon(getClass().getResource("/net/thevpc/halfa/halfa.png")).getImage(),
                        16, 16)
        );
        contentPane = new ContentPanel();
//        contentPane.setFocusTraversalKeysEnabled(false);
        frame.setContentPane(contentPane);
        reloadDocument();
        frame.setSize(PageView.REF_SIZE);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        prepareContentPane();
        timer = new Timer("DocumentViewResourcesMonitor", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkResourcesChanged();
            }
        }, 3000, 1000);
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
                HResourceMonitor r = document.resources();
                if (r.changed()) {
                    reloadDocument();
                }
            }
        } finally {
            this.inCheckResourcesChanged = false;
        }
    }

    public NSession session() {
        return session;
    }

    public HNodeRendererManager rendererManager() {
        return rendererManager;
    }

    public String getPageSourceName() {
        Object s = getPageSource();
        if (s != null) {
            if (s instanceof NPathHResource) {
                return ((NPathHResource) s).getPath().getName();
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
        HNode p = currentShowingPage == null ? null : currentShowingPage.getPage();
        Object s = null;
        while (p != null && s == null) {
            s = p.source();
            p = p.parent();
        }
        return s;
    }

    public HMessageList messages() {
        return messages;
    }

    public class ContentPanel extends JPanel {

        CardLayout cardLayout;
        List<HDocumentLayer> layers = new ArrayList<>();

        public ContentPanel() {
            this.cardLayout = new CardLayout();
            setLayout(cardLayout);
            layers.add(new PizzaProgressLayer());
            layers.add(new PageIndexSimpleLayer());
            layers.add(new SourceNameSimpleLayer());
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2d = (Graphics2D) g;
            HGraphics hg = engine.createGraphics(g2d);
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = getSize();
            for (HDocumentLayer filter : layers) {
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
                        reloadDocument();
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
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem saveAsPdfMenuItem = new JMenuItem("Save as PDF");
        saveAsPdfMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PdfConfigDialog configDialog = new PdfConfigDialog((Frame) SwingUtilities.getWindowAncestor((Component) e.getSource()));
                configDialog.setVisible(true);
                if (configDialog.isConfirmed()) {
                    HDocumentStreamRendererConfig config = configDialog.getConfig();

                    if (document != null && listener != null ) {
                        listener.onSaveDocument(document, config);
                    } else {
                        System.err.println("saveDocumentListener is null or document is null");
                    }
                }
            }
        });

        popupMenu.add(saveAsPdfMenuItem);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }





    private boolean reloadDocument() {
        if (inLoadDocument) {
            return false;
        }
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
                HDocument rawDocument = documentSupplier.get(rendererContext);
                listener.onChangedRawDocument(rawDocument);
                HDocument compiledDocument = engine.compileDocument(rawDocument.copy(), messages).get();
                listener.onChangedCompiledDocument(compiledDocument);
                document = compiledDocument;
            } catch (Exception ex) {
                this.currentThrowable = ex;
            }
            if (document == null) {
                document = engine.documentFactory().ofDocument();
            }
            document.resources().save();
            List<HNode> pages = PagesHelper.resolvePages(document);
            pageViews.clear();
            contentPane.removeAll();
            pagesMapById.clear();
            pagesMapByIndex.clear();
            for (int i = 0; i < pages.size(); i++) {
                HNode page = pages.get(i);
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
        }
        return true;
    }

    private void lastPage() {
        showPage(getPagesCount() - 1);
    }

    private void firstPage() {
        showPage(0);
    }

    public PageView createPageView(HNode node, int index) {
        return new PageView(
                node, index,
                engine(),
                rendererManager(),
                messages(),
                session()
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
            contentPane.doShow(pv.id());
        } else {
            listener.onChangedPage(null);
        }
        frame.setVisible(true);
    }

    public int getPagesCount() {
        return pageViews.size();
    }

    public void show() {
        showPage(0);
    }

    public void showPage(int index) {
        PageView pageView = pageViews.get(index);
        this.showPage(pageView);
    }

    public HEngine engine() {
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

    private class HDocumentRendererContextImpl implements HDocumentRendererContext {

        public HDocumentRendererContextImpl() {
        }

        @Override
        public HMessageList messages() {
            return messages;
        }
    }

    public class PdfConfigDialog extends JDialog {
        private JComboBox<String> orientationComboBox;
        private JComboBox<String> sizePageComboBox;
        private JComboBox<Integer> imagesPerPageComboBox;
        private JCheckBox showPageNumberCheckBox;
        private JCheckBox showFileNameCheckBox;
        private JCheckBox showDateCheckBox;
        private boolean confirmed;

        public PdfConfigDialog(Frame parent) {
            super(parent, "PDF Configuration", true);
            setLayout(new GridLayout(0, 2));

            add(new JLabel("Orientation:"));
            orientationComboBox = new JComboBox<>(new String[]{"Portrait", "Landscape"});
            add(orientationComboBox);

            add(new JLabel("Page Size:"));
            sizePageComboBox = new JComboBox<>(new String[]{"Small (300x300)", "Medium (600x600)", "Large (900x900)","Max (1200x1200)"});
            add(sizePageComboBox);

            add(new JLabel("Pages Per Sheet:"));
            imagesPerPageComboBox = new JComboBox<>(new Integer[]{1, 2, 4, 6});
            add(imagesPerPageComboBox);

            showPageNumberCheckBox = new JCheckBox("Show Page Number");
            add(showPageNumberCheckBox);

            showFileNameCheckBox = new JCheckBox("Show File Name");
            add(showFileNameCheckBox);

            showDateCheckBox = new JCheckBox("Show Date");
            add(showDateCheckBox);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    confirmed = true;
                    setVisible(false);
                }
            });
            add(okButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    confirmed = false;
                    setVisible(false);
                }
            });
            add(cancelButton);

            pack();
            setLocationRelativeTo(parent);
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public HDocumentStreamRendererConfig getConfig() {
            HDocumentStreamRendererConfig config = new HDocumentStreamRendererConfig();
            config.setOrientation(orientationComboBox.getSelectedItem().equals("Portrait") ? PageOrientation.PORTRAIT : PageOrientation.LANDSCAPE);
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
            config.setImagesPerPage((int) imagesPerPageComboBox.getSelectedItem());
            config.setShowPageNumber(showPageNumberCheckBox.isSelected());
            config.setShowFileName(showFileNameCheckBox.isSelected());
            config.setShowDate(showDateCheckBox.isSelected());
            return config;
        }
    }

}
