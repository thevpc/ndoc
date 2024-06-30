package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.nuts.NSession;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1000, 1000);
    private HNode page;
    private int index;
    private String uuid;
    private HNodeRendererManager rendererManager;
    private HEngine engine;
    private NSession session;
    private HMessageList messages;

    public PageView(HNode page, int index,
                    HEngine engine,
                    HNodeRendererManager rendererManager,
                    HMessageList messages,
                    NSession session
    ) {
        this.page = page;
        this.index = index;
        this.uuid = UUID.randomUUID().toString();
        this.rendererManager = rendererManager;
        this.engine = engine;
        this.messages = messages;
        this.session = session;
    }

    public HEngine engine() {
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
        HNodeRendererContext ctx = new ScreenHPartRendererContext(this,
                engine.createGraphics(g2d)
                , size, session, messages);
        render(page, ctx);
    }


    public void render(HNode p, HNodeRendererContext ctx) {
        if (p.isTemplate()) {
            return;
        }
        HNodeRenderer r = rendererManager.getRenderer(p.type()).get();
        r.render(p, ctx);
    }

    public HNodeRendererManager rendererManager() {
        return rendererManager;
    }

    public HNode getPage() {
        return page;
    }
}
