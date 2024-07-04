package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.spi.base.renderer.HNodeRendererContextBase;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.nuts.NSession;

import java.awt.*;
import java.awt.image.ImageObserver;

class ScreenHPartRendererContext extends HNodeRendererContextBase {
    private final PageView pageView;

    public ScreenHPartRendererContext(PageView pageView, HGraphics g, Dimension size, NSession session, HMessageList messages) {
        super(pageView.engine(), g, size, new Bounds2(0, 0, size.getWidth(), size.getHeight()), session, messages);
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
