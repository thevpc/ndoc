package net.thevpc.halfa.engine.renderer;

import net.thevpc.halfa.api.model.elem2d.HImageOptions;
import net.thevpc.halfa.engin.spibase.renderer.ImageUtils;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HGraphicsImageDrawer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class HGraphicsImageDrawerByBytes implements HGraphicsImageDrawer {
    private final byte[] ic;

    public HGraphicsImageDrawerByBytes(byte[] ic) {
        this.ic = ic;
    }

    @Override
    public void drawImage(double x, double y, HImageOptions options, HGraphics g) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(ic));
        } catch (IOException e) {
            return;
        }
        if (image == null) {
            return;
        }
        image = ImageUtils.resize(image, options.getSize());
        g.drawImage(image, x, y, options.getImageObserver());
    }
}
