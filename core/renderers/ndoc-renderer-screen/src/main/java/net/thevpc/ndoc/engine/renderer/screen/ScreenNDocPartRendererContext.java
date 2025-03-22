package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.spi.base.renderer.NDocNodeRendererContextBase;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;

import java.awt.*;
import java.awt.image.ImageObserver;

class ScreenNDocPartRendererContext extends NDocNodeRendererContextBase {
    private final PageView pageView;

    public ScreenNDocPartRendererContext(PageView pageView, NDocGraphics g, Dimension size, HLogger messages) {
        super(pageView.engine(), g, size, new Bounds2(0, 0, size.getWidth(), size.getHeight()), messages);
        this.pageView = pageView;
        setCapability("animated",true);
    }

    @Override
    public ImageObserver imageObserver() {
        return pageView;
    }

    @Override
    public void repaint() {
        pageView.repaint();
    }
}
