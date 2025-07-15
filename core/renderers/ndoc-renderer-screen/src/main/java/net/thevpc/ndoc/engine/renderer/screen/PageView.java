package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.NDocNodeRenderer;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererManager;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1000, 1000);
    private HNode page;
    private int index;
    private String uuid;
    private NDocNodeRendererManager rendererManager;
    private NDocEngine engine;
    private HLogger messages;

    public PageView(HNode page, int index,
                    NDocEngine engine,
                    NDocNodeRendererManager rendererManager,
                    HLogger messages
    ) {
        this.page = page;
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

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        NDocNodeRendererContext ctx = new ScreenNDocPartRendererContext(this,
                engine.createGraphics(g2d)
                , size, messages);
        render(page, ctx);
    }


    public void render(HNode p, NDocNodeRendererContext ctx) {
        for (HNode nn : engine.compileNodeBeforeRendering(p, ctx.log())) {
            NDocNodeRenderer r = rendererManager.getRenderer(nn.type()).get();
            r.render(nn, ctx);
        }
    }

    public NDocNodeRendererManager rendererManager() {
        return rendererManager;
    }

    public HNode getPage() {
        return page;
    }
}
