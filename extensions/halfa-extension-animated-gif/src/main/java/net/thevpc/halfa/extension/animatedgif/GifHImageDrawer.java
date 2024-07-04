package net.thevpc.halfa.extension.animatedgif;

import net.thevpc.halfa.api.model.elem2d.HImageOptions;
import net.thevpc.halfa.spi.base.renderer.HImageUtils;
import net.thevpc.halfa.spi.renderer.HGraphicsImageDrawer;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.nuts.io.NPath;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class GifHImageDrawer implements HGraphicsImageDrawer {
    private final byte[] ic;
    private final Map<String, FutureTask<NPath>> pendingCache;

    public GifHImageDrawer(byte[] ic,Map<String, FutureTask<NPath>> pendingCache) {
        this.ic = ic;
        this.pendingCache = pendingCache;
    }

    @Override
    public void drawImage(double x, double y, HImageOptions options, HGraphics g) {
        Color transparentColor = options.getTransparentColor();
        Dimension size = options.getSize();
        FutureTask<NPath> location = GifResizer.transformWithCache(ic, null,
                        new GifResizer.GifFrameTransformer() {
                            @Override
                            public BufferedImage frame(BufferedImage image, int index) {
                                return null;
                            }

                            @Override
                            public Color transparentColor() {
                                return transparentColor;
                            }

                            @Override
                            public Dimension size() {
                                return size;
                            }
                        }, g.session(),pendingCache)
                ;
        if(location.isDone() || options.isDisableAnimation()) {
            ImageIcon ii = null;
            try {
                ii = new ImageIcon(location.get().toURL().get());
                g.drawImage(ii.getImage(), x, y, options.getImageObserver());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }else{
            BufferedImage image = null;
            try {
                image = ImageIO.read(new ByteArrayInputStream(ic));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(image!=null){
                image= HImageUtils.resize(image, options.getSize());
                g.drawImage(image, x, y, options.getImageObserver());
            }
            new Thread(){
                @Override
                public void run() {
                    if(options.getAsyncLoad()!=null){
                        options.getAsyncLoad().run();
                    }
                }
            }.start();
        }
    }
}
