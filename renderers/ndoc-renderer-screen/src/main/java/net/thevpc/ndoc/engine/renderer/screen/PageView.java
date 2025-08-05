package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRenderer;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererManager;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.time.NChronometer;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1024, 768);
    private NDocNode page0;
    private NDocNode page;
    private long pageStartTime;
    private int index;
    private String uuid;
    private NDocNodeRendererManager rendererManager;
    private NDocEngine engine;
    private NDocument document;

    public PageView(
            NDocument document,
            NDocNode page, int index,
            NDocEngine engine,
            NDocNodeRendererManager rendererManager
    ) {
        this.document = document;
        this.page0 = page;
        this.index = index;
        this.uuid = UUID.randomUUID().toString();
        this.rendererManager = rendererManager;
        this.engine = engine;

    }

    public NDocEngine engine() {
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
            List<NDocNode> all = engine().compilePageNode(this.page0, document);
            this.page = NOptional.ofSingleton(all).get();
            c.stop();
            engine.log().log(NMsg.ofC("page %s compiled in %s", (index + 1), c), NDocUtils.sourceOf(this.page0));
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
        NDocNodeRendererContext ctx = new ScreenNDocPartRendererContext(this,
                engine.createGraphics(g2d)
                , size, pageStartTime);
        if (page != null) {
            render(page, ctx);
        }
        c.stop();
        //System.out.println("NChronometer::paintComponent "+c);
    }


    public void render(NDocNode p, NDocNodeRendererContext ctx) {
        NDocNodeRenderer r = rendererManager.getRenderer(p.type()).get();
        r.render(p, ctx);
    }

    public NDocNodeRendererManager rendererManager() {
        return rendererManager;
    }

    public NDocNode getPage() {
        return page;
    }
}
