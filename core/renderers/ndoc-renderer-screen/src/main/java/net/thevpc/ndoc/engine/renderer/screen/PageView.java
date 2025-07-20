package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.NDocNodeRenderer;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererManager;
import net.thevpc.nuts.time.NChronometer;
import net.thevpc.nuts.util.NOptional;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1000, 1000);
    private NDocNode page0;
    private NDocNode page;
    private int index;
    private String uuid;
    private NDocNodeRendererManager rendererManager;
    private NDocEngine engine;
    private NDocLogger messages;
    private NDocument document;

    public PageView(
            NDocument document,
            NDocNode page, int index,
                    NDocEngine engine,
                    NDocNodeRendererManager rendererManager,
                    NDocLogger messages
    ) {
        this.document = document;
        this.page0 = page;
        this.index = index;
        this.uuid = UUID.randomUUID().toString();
        this.rendererManager = rendererManager;
        this.engine = engine;
        this.messages = messages;

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

    void onShow() {
        if (page == null) {
            List<NDocNode> all = engine().compileNode(this.page0, document, messages);
            this.page = NOptional.ofSingleton(all).get();
        }
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
                , size, messages);
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
