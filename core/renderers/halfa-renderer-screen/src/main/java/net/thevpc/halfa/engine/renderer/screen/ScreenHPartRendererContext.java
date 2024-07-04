package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.engin.spibase.renderer.HPartRendererContextImpl;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.nuts.NSession;

import java.awt.*;
import java.awt.image.ImageObserver;

class ScreenHPartRendererContext extends HPartRendererContextImpl {
    private final PageView pageView;

    public ScreenHPartRendererContext(PageView pageView, HGraphics g, Dimension size, NSession session, HMessageList messages) {
        super(g, size, new Bounds2(0, 0, size.getWidth(), size.getHeight()), session, messages);
        this.pageView = pageView;
    }

    @Override
    public HNodeRendererManager manager() {
        return pageView.rendererManager();
    }

    @Override
    public void render(HNode p, HNodeRendererContext ctx) {
        pageView.render(p, ctx);
    }

    @Override
    public ImageObserver imageObserver() {
        return pageView;
    }

    @Override
    public HEngine engine() {
        return pageView.engine();
    }

    @Override
    public boolean isAnimated() {
        return true;
    }

    @Override
    public void repaint() {
        pageView.repaint();
    }
}
