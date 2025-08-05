package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.elem2d.NTxImageOptions;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocGraphicsImageDrawer;
import net.thevpc.ntexup.engine.util.NTxUtilsImages;
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
    public void drawImage(double x, double y, NTxImageOptions options, NDocGraphics g) {
        BufferedImage image = loadBufferedImage(ic);
        if (image == null) {
            return;
        }
        image = NTxUtilsImages.resizeImage(image, options.getSize());
        g.drawImage(image, x, y, options.getImageObserver());
    }
}
