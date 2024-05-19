package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.halfa.api.model.HPage;
import net.thevpc.halfa.spi.utils.PagesHelper;
import net.thevpc.nuts.NSession;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentView {
    private HDocument document;
    private HalfaEngine halfaEngine;
    private int currentPageIndex;
    private List<PageView> pageViews = new ArrayList<>();
    private NSession session;
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel contentPane;
    private PageView currentShowingPage;

    public DocumentView(HDocument document, HalfaEngine halfaEngine, NSession session) {
        this.document = document;
        this.halfaEngine = halfaEngine;
        this.session = session;
        List<HPage> pages = PagesHelper.resolvePages(document);
        for (int i = 0; i < pages.size(); i++) {
            HPage page = pages.get(i);
            pageViews.add(new PageView(
                    page,
                    UUID.randomUUID().toString(), i, this
            ));
        }
        if (pageViews.isEmpty()) {
            pageViews.add(new PageView(
                    halfaEngine.factory().page(),
                    UUID.randomUUID().toString(), 0, this
            ));
        }
        frame = new JFrame();
        contentPane = new JPanel(cardLayout = new CardLayout());
        frame.setContentPane(contentPane);
        for (PageView pageView : pageViews) {
            contentPane.add(pageView.component(), pageView.id());
        }
        frame.setSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void showPage(PageView pv) {
        if (currentShowingPage != null) {
            currentShowingPage.onHide();
        }
        this.currentShowingPage = pv;
        if (pv != null) {
            this.currentShowingPage.onShow();
            cardLayout.show(contentPane, pv.id());
        } else {
            //????
        }
        frame.setVisible(true);
    }

    public void show() {
        showPage(0);
    }

    public void showPage(int index) {
        pageViews.get(index).showPage();
    }

    public HalfaEngine getHalfaEngine() {
        return halfaEngine;
    }
}
