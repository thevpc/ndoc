package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.document.elem2d.NDocImageOptions;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocGraphicsImageDrawer;
import net.thevpc.nuts.io.NPath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class NDocGraphicsImageDrawerByPath implements NDocGraphicsImageDrawer {
    private final NPath ic;

    public NDocGraphicsImageDrawerByPath(NPath ic) {
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
    public void drawImage(double x, double y, NDocImageOptions options, NDocGraphics g) {
        BufferedImage image = loadBufferedImage(ic);
        if (image == null) {
            return;
        }
        image = HImageUtils.resize(image, options.getSize());
        g.drawImage(image, x, y, options.getImageObserver());
    }
}
