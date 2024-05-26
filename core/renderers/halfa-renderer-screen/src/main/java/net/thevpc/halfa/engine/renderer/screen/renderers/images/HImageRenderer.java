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

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Image image = loadImage(p.getPropertyValue(HPropName.VALUE), p, ctx);

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
                g.drawImage(image, (int) x, (int) (y - b.getMinY()), null);
            }
        }
        return new HSizeRequirements(new Bounds2(x,y,b.getWidth(),b.getWidth()));
    }


}
