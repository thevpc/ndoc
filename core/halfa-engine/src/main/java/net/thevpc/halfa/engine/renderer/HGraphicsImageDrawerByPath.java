package net.thevpc.halfa.engine.renderer;

import net.thevpc.halfa.api.model.elem2d.HImageOptions;
import net.thevpc.halfa.spi.base.renderer.HImageUtils;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HGraphicsImageDrawer;
import net.thevpc.nuts.io.NPath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class HGraphicsImageDrawerByPath implements HGraphicsImageDrawer {
    private final NPath ic;

    public HGraphicsImageDrawerByPath(NPath ic) {
        this.ic = ic;
    }


    private BufferedImage loadBufferedImage(NPath p) {
        URL url = p.toURL().orNull();
        if(url!=null) {
            try {
                BufferedImage i = ImageIO.read(url);
                return i;
            } catch (IOException e) {
                //
            }
        }
        try {
            return ImageIO.read(new ByteArrayInputStream(p.readBytes()));
        } catch (IOException e) {
            //
        }
        return null;
    }

    @Override
    public void drawImage(double x, double y, HImageOptions options, HGraphics g) {
        BufferedImage image = loadBufferedImage(ic);
        if (image == null) {
            return;
        }
        image = HImageUtils.resize(image, options.getSize());
        g.drawImage(image, x, y, options.getImageObserver());
    }
}
