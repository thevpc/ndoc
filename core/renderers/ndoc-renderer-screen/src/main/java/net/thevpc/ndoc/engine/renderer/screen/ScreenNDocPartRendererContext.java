package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.base.renderer.NDocNodeRendererContextBase;
import net.thevpc.ndoc.api.renderer.NDocGraphics;

import java.awt.*;
import java.awt.image.ImageObserver;

class ScreenNDocPartRendererContext extends NDocNodeRendererContextBase {
    private final PageView pageView;

    public ScreenNDocPartRendererContext(PageView pageView, NDocGraphics g, Dimension size) {
        super(pageView.engine(), g, size, new NDocBounds2(0, 0, size.getWidth(), size.getHeight()));
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
