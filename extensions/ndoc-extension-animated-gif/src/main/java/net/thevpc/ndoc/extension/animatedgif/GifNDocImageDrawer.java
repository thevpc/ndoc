package net.thevpc.ndoc.extension.animatedgif;

import net.thevpc.ndoc.api.document.elem2d.NDocImageOptions;
import net.thevpc.ndoc.api.base.renderer.HImageUtils;
import net.thevpc.ndoc.api.renderer.NDocGraphicsImageDrawer;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
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

class GifNDocImageDrawer implements NDocGraphicsImageDrawer {
    private final byte[] ic;
    private final Map<String, FutureTask<NPath>> pendingCache;

    public GifNDocImageDrawer(byte[] ic, Map<String, FutureTask<NPath>> pendingCache) {
        this.ic = ic;
        this.pendingCache = pendingCache;
    }

    @Override
    public void drawImage(double x, double y, NDocImageOptions options, NDocGraphics g) {
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
                        }, pendingCache)
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
