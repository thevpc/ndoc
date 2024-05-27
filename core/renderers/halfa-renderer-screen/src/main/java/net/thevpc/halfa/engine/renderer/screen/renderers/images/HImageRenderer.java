package net.thevpc.halfa.engine.renderer.screen.renderers.images;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.io.NPath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HImageRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();
    Map<Object,BufferedImage> imageCache = new HashMap<>();

    public HImageRenderer() {
        super(HNodeType.IMAGE);
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private BufferedImage loadImage(Object img,HNode p,HNodeRendererContext ctx){
        if(img instanceof BufferedImage){
            return (BufferedImage) img;
        }
        if(img instanceof String){
            NPath nPath = ctx.resolvePath((String) img, p);
            Object[] keys=new Object[]{
                    nPath,img,nPath.toString()
            };
            for (Object key : keys) {
                boolean c = imageCache.containsKey(key);
                if(c){
                    return imageCache.get(key);
                }
            }
            try(InputStream is=nPath.getInputStream()){
                BufferedImage i = ImageIO.read(is);
                for (Object key : keys) {
                    imageCache.put(key,i);
                }
                return i;
            }catch (IOException e){
                throw new IllegalArgumentException(e);
            }
        }
        if(img instanceof NPath){
            NPath nPath = ctx.resolvePath((NPath) img, p);
            Object[] keys=new Object[]{
                    nPath,img,nPath.toString()
            };
            for (Object key : keys) {
                boolean c = imageCache.containsKey(key);
                if(c){
                    return imageCache.get(key);
                }
            }
            try(InputStream is=nPath.getInputStream()){
                BufferedImage i = ImageIO.read(is);
                for (Object key : keys) {
                    imageCache.put(key,i);
                }
                return i;
            }catch (IOException e){
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        BufferedImage image = loadImage(p.getPropertyValue(HPropName.VALUE).orNull(), p, ctx);

        HGraphics g = ctx.graphics();

        Bounds2 b = selfBounds(p,  ctx);
        double x = b.getX();
        double y = b.getY();

        if (!ctx.isDry()) {
            if (applyBackgroundColor(p, g, ctx)) {
                g.fillRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }

            applyForeground(p, g, ctx);
            if (image != null) {
                // would resize?
                BufferedImage resized = resize(image, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
                g.drawImage(resized, (int) x, (int) y, null);
            }
        }
        paintDebugBox(p, ctx, g, b);
    }


}
