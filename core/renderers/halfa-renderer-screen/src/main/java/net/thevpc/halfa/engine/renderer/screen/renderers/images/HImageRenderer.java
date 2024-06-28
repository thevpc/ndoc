package net.thevpc.halfa.engine.renderer.screen.renderers.images;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engin.spibase.renderer.ImageUtils;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererUtils;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engin.spibase.renderer.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HImageRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();
    Map<Object, HImageDrawer> imageCache = new HashMap<>();

    public HImageRenderer() {
        super(HNodeType.IMAGE);
    }

    private HImageDrawer putCache(Object[] keys,HImageDrawer value){
        for (Object key : keys) {
            imageCache.put(key, value);
        }
        return value;
    }

    private HImageDrawer loadImageFromPath(NPath img, HNode p, HNodeRendererContext ctx) {
        NPath nPath = ctx.resolvePath((NPath) img, p);
        Object[] keys = new Object[]{
                nPath, img, nPath.toString()
        };
        for (Object key : keys) {
            boolean c = imageCache.containsKey(key);
            if (c) {
                return imageCache.get(key);
            }
        }
        URL url=nPath.toURL().orNull();
        if(url!=null){
            ImageIcon ic;
            byte[] b = nPath.readBytes();
            if(url.toString().toLowerCase().endsWith(".gif")){
                return putCache(keys, new GifHImageDrawer(b));
            }else{
                return putCache(keys, new ImageHImageDrawer(b));
            }
        }
        try (InputStream is = nPath.getInputStream()) {
            BufferedImage i = ImageIO.read(is);
            return putCache(keys, new BufferedImageDrawer(i));
        } catch (Exception e) {
            HResource src = ctx.engine().computeSource(p);
            ctx.messages().addError(NMsg.ofC("[%s] [ERROR] image not found : %s (%s)",
                    src == null ? null : src.shortName(),
                    img, e), src);
            for (Object key : keys) {
                imageCache.put(key, null);
            }
            return null;
        }
    }

    private HImageDrawer loadImage(Object img, HNode p, HNodeRendererContext ctx) {
        if (img instanceof BufferedImage) {
            return new BufferedImageDrawer((BufferedImage) img);
        }
        if (img instanceof NPath) {
            return loadImageFromPath((NPath) img, p, ctx);
        }
        NOptional<String> imgStr = ObjEx.of(img).asStringOrName();
        if (imgStr.isPresent()) {
            NPath nPath = ctx.resolvePath(imgStr.get(), p);
            return loadImageFromPath(nPath, p, ctx);
        }
        return null;
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        HImageDrawer image = loadImage(p.getPropertyValue(HPropName.VALUE).orNull(), p, ctx);

        HGraphics g = ctx.graphics();

        Bounds2 b = selfBounds(p, ctx);
        double x = b.getX();
        double y = b.getY();

        if (!ctx.isDry()) {
            if (HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }

            HNodeRendererUtils.applyForeground(p, g, ctx, false);
            if (image != null) {
                // would resize?
                int w = HUtils.intOf(b.getWidth());
                int h = HUtils.intOf(b.getHeight());
                if (w > 0 && h > 0) {
                    image.drawImage(g, x, y, w, h, ctx.imageObserver());
                }
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

    public interface HImageDrawer{
        void drawImage(HGraphics g,double x, double y, double w, double h, ImageObserver o);
    }


    private static class BufferedImageDrawer implements HImageDrawer {
        private final BufferedImage img;

        public BufferedImageDrawer(BufferedImage img) {
            this.img = img;
        }

        @Override
        public void drawImage(HGraphics g, double x, double y, double w, double h, ImageObserver o) {
            BufferedImage resized = ImageUtils.resize(img, (int)w, (int)h);
            g.drawImage(resized, (int) x, (int) y, null);
        }
    }

    private static class ImageHImageDrawer implements HImageDrawer {
        private final byte[] ic;

        public ImageHImageDrawer(byte[] ic) {
            this.ic = ic;
        }

        @Override
        public void drawImage(HGraphics g, double x, double y, double w, double h, ImageObserver o) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(new ByteArrayInputStream(ic));
            } catch (IOException e) {
                return;
            }
            if(image==null){
                return;
            }
            image=ImageUtils.resize(image,(int)w,(int)h);
            // should resize
            g.drawImage(image, x,y, o);
        }
    }
    private static class GifHImageDrawer implements HImageDrawer {
        private final byte[] ic;

        public GifHImageDrawer(byte[] ic) {
            this.ic = ic;
        }

        @Override
        public void drawImage(HGraphics g, double x, double y, double w, double h, ImageObserver o) {
            byte[] t = GifResizer.transform(ic,i->ImageUtils.resize(i,(int)w,(int)h));
            ImageIcon ii=new ImageIcon(t);
            // should resize
            g.drawImage(ii.getImage(), x,y, o);
        }
    }
}
