package net.thevpc.halfa.elem.base.image;

import net.thevpc.halfa.api.model.elem2d.HImageOptions;
import net.thevpc.halfa.spi.base.renderer.HImageUtils;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HGraphicsImageDrawer;

import java.awt.image.BufferedImage;

class BufferedImageDrawer implements HGraphicsImageDrawer {
    private final BufferedImage img;

    public BufferedImageDrawer(BufferedImage img) {
        this.img = img;
    }

    @Override
    public void drawImage(double x, double y, HImageOptions options, HGraphics g) {
        BufferedImage image = HImageUtils.resize(img, options.getSize());
        g.drawImage(image, (int) x, (int) y, options.getImageObserver());
    }
}
