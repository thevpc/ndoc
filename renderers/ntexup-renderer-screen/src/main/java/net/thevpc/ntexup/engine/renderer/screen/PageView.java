package net.thevpc.ntexup.engine.renderer.screen;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxNodeRenderer;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererManager;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.time.NChronometer;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1024, 768);
    private NTxNode page0;
    private NTxNode page;
    private long pageStartTime;
    private int index;
    private String uuid;
    private NTxNodeRendererManager rendererManager;
    private NTxEngine engine;
    private NTxDocument document;

    public PageView(
            NTxDocument document,
            NTxNode page, int index,
            NTxEngine engine,
            NTxNodeRendererManager rendererManager
    ) {
        this.document = document;
        this.page0 = page;
        this.index = index;
        this.uuid = UUID.randomUUID().toString();
        this.rendererManager = rendererManager;
        this.engine = engine;

    }

    public NTxEngine engine() {
        return engine;
    }

    public JComponent component() {
        return this;
    }

    public String id() {
        return uuid;
    }

    public int index() {
        return index;
    }

    void onHide() {

    }

    synchronized void onShow() {
        if (page == null) {
            NChronometer c = NChronometer.startNow();
            List<NTxNode> all = engine().compilePageNode(this.page0, document);
            this.page = NOptional.ofSingleton(all).get();
            c.stop();
            engine.log().log(NMsg.ofC("page %s compiled in %s", (index + 1), c), NTxUtils.sourceOf(this.page0));
        }
        this.pageStartTime = System.currentTimeMillis();
    }

    public boolean isLoading() {
        return page == null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        NChronometer c = NChronometer.startNow();
        super.paintComponent(g);
        Dimension size = getSize();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        NTxNodeRendererContext ctx = new ScreenNTxPartRendererContext(this,
                engine.createGraphics(g2d)
                , size, pageStartTime);
        if (page != null) {
            render(page, ctx);
        }
        c.stop();
        //System.out.println("NChronometer::paintComponent "+c);
    }


    public void render(NTxNode p, NTxNodeRendererContext ctx) {
        NTxNodeRenderer r = rendererManager.getRenderer(p.type()).get();
        r.render(p, ctx);
    }

    public NTxNodeRendererManager rendererManager() {
        return rendererManager;
    }

    public NTxNode getPage() {
        return page;
    }
}
