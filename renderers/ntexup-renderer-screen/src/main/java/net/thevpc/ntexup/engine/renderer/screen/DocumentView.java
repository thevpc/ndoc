package net.thevpc.ntexup.engine.renderer.screen;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.ntexup.api.source.NTxSourceMonitor;
import net.thevpc.ntexup.engine.parser.resources.NTxSourceNew;
import net.thevpc.ntexup.api.renderer.*;


import net.thevpc.ntexup.engine.util.NTxUtilsImages;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class DocumentView {

    NTxDocument document;
    private NTxDocumentRendererSupplier documentSupplier;
    NTxEngine engine;
    private int currentPageIndex;
    private List<PageView> pageViews = new ArrayList<>();
    JFrame frame;
    DocumentViewContentPanel contentPane;
    PageView currentShowingPage;
    private Map<String, PageView> pagesMapById = new HashMap<>();
    private Map<Integer, PageView> pagesMapByIndex = new HashMap<>();
    private NTxNodeRendererManager rendererManager;
    private Timer timer;
    private boolean inCheckResourcesChanged;
    private boolean inLoadDocument;
    Throwable currentThrowable;
    NTxDocumentRendererListener listener;
    private NTxDocumentRendererContext rendererContext = new NTxDocumentRendererContextImpl();
    private boolean isShown;

    public DocumentView(NTxDocumentRendererSupplier documentSupplier,
                        NTxEngine engine, NTxDocumentRendererListener listener) {
        this.documentSupplier = documentSupplier;
        this.listener = listener;
        this.engine = engine;
        this.rendererManager = engine.renderManager();

        frame = new JFrame();
        frame.setTitle("NTexup Viewer");
        frame.setIconImage(
                NTxUtilsImages.resizeImage(
                        new ImageIcon(getClass().getResource("/net/thevpc/ntexup/ntexup-logo.png")).getImage(),
                        16, 16)
        );
        contentPane = new DocumentViewContentPanel(this);
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
                NTxSourceMonitor r = document.resources();
                if (r.changed()) {
                    reloadDocumentAsync();
                }
            }
        } finally {
            this.inCheckResourcesChanged = false;
        }
    }

    public boolean isPageLoading() {
        PageView cp = currentShowingPage;
        return isLoading() || (cp != null && cp.isLoading());
    }

    public boolean isLoading() {
        return inLoadDocument;
    }

    public NTxNodeRendererManager rendererManager() {
        return rendererManager;
    }

    public String getPageSourceName() {
        Object s = getPageSource();
        if (s != null) {
            if (s instanceof NTxSource) {
                NPath path = ((NTxSource) s).path().orNull();
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
        NTxItem p = currentShowingPage == null ? null : currentShowingPage.getPage();
        Object s = null;
        while (p != null && s == null) {
            s = p.source();
            p = p.parent();
        }
        return s;
    }

    public void prepareContentPane() {
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    new Thread(() -> nextPage()).start();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    new DocumentPopupMenu(DocumentView.this).showPopupMenu(e);
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
                NTxDocument rawDocument = documentSupplier.get(rendererContext);
                NTxSource source = rawDocument.root().source();
                SwingUtilities.invokeLater(() -> {
                    if (source == null) {
                        frame.setTitle("New Document");
                    } else {
                        frame.setTitle(String.valueOf(source));
                    }
                });
                listener.onChangedRawDocument(rawDocument);
                NTxDocument compiledDocument = engine.compileDocument(rawDocument.copy()).get();
                listener.onChangedCompiledDocument(compiledDocument);
                document = compiledDocument;
            } catch (Exception ex) {
                engine.log().log(NMsg.ofC("compile document failed %s", ex));
                this.currentThrowable = ex;
            }
            if (document == null) {
                document = engine.documentFactory().ofDocument(null);
            }
            document.resources().save();
            List<NTxNode> pages = engine.tools().resolvePages(document);
            pageViews.clear();
            contentPane.removeAll();
            pagesMapById.clear();
            pagesMapByIndex.clear();
            for (int i = 0; i < pages.size(); i++) {
                NTxNode page = pages.get(i);
                pageViews.add(createPageView(page, i));
            }
            if (pageViews.isEmpty()) {
                NTxNode node = engine.documentFactory().ofPage();
                node.setSource(NOptional.of(document).then(NTxDocument::source).orElseGet(NTxSourceNew::new));
                pageViews.add(createPageView(
                        node,
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

    void lastPage() {
        showPage(getPagesCount() - 1);
    }

    void firstPage() {
        showPage(0);
    }

    public PageView createPageView(NTxNode node, int index) {
        return new PageView(
                document,
                node, index,
                engine(),
                rendererManager()
        );
    }

    public void showPage(PageView pv) {
        synchronized (this) {
            if (currentShowingPage != null) {
                currentShowingPage.onHide();
            }
            this.currentShowingPage = pv;
            if (pv != null) {
                listener.onChangedPage(pv.getPage());
                pv.onShow();
                SwingUtilities.invokeLater(() -> contentPane.doShow(pv.id()));
            } else {
                listener.onChangedPage(null);
            }
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

    public NTxEngine engine() {
        return engine;
    }

    public synchronized void nextPage() {
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
