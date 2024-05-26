package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.components.PizzaProgressLayer;
import net.thevpc.halfa.engine.renderer.screen.components.HDocumentLayer;
import net.thevpc.halfa.engine.renderer.screen.components.PageIndexSimpleLayer;
import net.thevpc.halfa.engine.renderer.screen.components.SourceNameSimpleLayer;
import net.thevpc.halfa.engine.renderer.screen.renderers.HGraphicsImpl;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.utils.PagesHelper;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class DocumentView {
    private HDocument document;
    private Supplier<HDocument> documentSupplier;
    private HEngine halfaEngine;
    private int currentPageIndex;
    private List<PageView> pageViews = new ArrayList<>();
    private NSession session;
    private JFrame frame;
    private ContentPanel contentPane;
    private PageView currentShowingPage;
    private Map<String, PageView> pagesMapById = new HashMap<>();
    private Map<Integer, PageView> pagesMapByIndex = new HashMap<>();
    private RenderFactoryManager renderFactoryManager;

    public DocumentView(Supplier<HDocument> documentSupplier, HEngine halfaEngine, NSession session) {
        this.documentSupplier = documentSupplier;
        this.halfaEngine = halfaEngine;
        this.session = session;
        renderFactoryManager=new RenderFactoryManager(halfaEngine);

        frame = new JFrame();
        contentPane = new ContentPanel();
//        contentPane.setFocusTraversalKeysEnabled(false);
        frame.setContentPane(contentPane);
        reloadDocument();
        frame.setSize(PageView.REF_SIZE);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        prepareContentPane();
    }

    public NSession session() {
        return session;
    }

    public RenderFactoryManager getRenderFactoryManager() {
        return renderFactoryManager;
    }

    public String getPageSourceName() {
        Object s = getPageSource();
        if (s != null) {
            if (s instanceof NPath) {
                return ((NPath) s).getName();
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
            HGraphics hg=new HGraphicsImpl(g2d);
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = getSize();
            for (HDocumentLayer filter : layers) {
                filter.draw(DocumentView.this, size, hg);
            }
        }

        private void drawStr(String str, HAlign a, Graphics2D g2d) {
            Dimension size = getSize();
            Rectangle2D b = g2d.getFontMetrics().getStringBounds(str, g2d);

            int x = 0;
            int y = 0;
            switch (a) {
                case LEFT: {
                    x = 10;
                    y = (int) (size.getHeight() - b.getHeight());
                    break;
                }
                case RIGHT: {
                    x = (int) (size.getWidth() - b.getWidth()) - 10;
                    y = (int) (size.getHeight() - b.getHeight());
                    break;
                }
            }
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString(str, x + 2, y + 2);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(str, x, y);
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
                    previousPage();
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

    private void reloadDocument() {
        PageView oldPage = this.currentShowingPage;
        int oldIndex = 0;
        String oldId = null;
        if (oldPage != null) {
            oldIndex = oldPage.index();
            oldId = oldPage.id();
        }
        this.currentShowingPage = null;
        document = documentSupplier.get();
        List<HNode> pages = PagesHelper.resolvePages(document);
        pageViews.clear();
        contentPane.removeAll();
        pagesMapById.clear();
        pagesMapByIndex.clear();

        for (int i = 0; i < pages.size(); i++) {
            HNode page = pages.get(i);
            pageViews.add(new PageView(
                    page,
                    UUID.randomUUID().toString(), i, this
            ));
        }
        if (pageViews.isEmpty()) {
            pageViews.add(new PageView(
                    halfaEngine.documentFactory().ofPage(),
                    UUID.randomUUID().toString(), 0, this
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
    }

    private void lastPage() {
        showPage(getPagesCount() - 1);
    }

    private void firstPage() {
        showPage(0);
    }

    public void showPage(PageView pv) {
        if (currentShowingPage != null) {
            currentShowingPage.onHide();
        }
        this.currentShowingPage = pv;
        if (pv != null) {
            this.currentShowingPage.onShow();
            contentPane.doShow(pv.id());
        } else {
            //????
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
        pageViews.get(index).showPage();
    }

    public HEngine engine() {
        return halfaEngine;
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
}
