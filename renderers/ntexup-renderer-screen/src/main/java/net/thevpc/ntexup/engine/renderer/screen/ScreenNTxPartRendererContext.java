package net.thevpc.ntexup.engine.renderer.screen;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.engine.renderer.NTxNodeRendererContextBase;
import net.thevpc.ntexup.api.renderer.NTxGraphics;

import java.awt.*;
import java.awt.image.ImageObserver;

class ScreenNTxPartRendererContext extends NTxNodeRendererContextBase {
    private final PageView pageView;

    public ScreenNTxPartRendererContext(PageView pageView, NTxGraphics g, Dimension size, long pageStartTime) {
        super(pageView.engine(), g, size, new NTxBounds2(0, 0, size.getWidth(), size.getHeight()));
        this.pageView = pageView;
        setCapability(CAPABILITY_ANIMATE,true);
        setPageStartTime(pageStartTime);
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
