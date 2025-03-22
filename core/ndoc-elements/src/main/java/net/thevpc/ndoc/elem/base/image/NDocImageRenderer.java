package net.thevpc.ndoc.elem.base.image;

import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.HImageOptions;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByType;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.awt.*;

public class NDocImageRenderer extends NDocNodeRendererBase {

    HProperties defaultStyles = new HProperties();

    public NDocImageRenderer() {
        super(HNodeType.IMAGE);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        int w = net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth());
        int h = net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight());
        if (w <= 0 || h <= 0) {
            return;
        }

        Color transparentColor = NDocValueByType.getColor(p, ctx, HPropName.TRANSPARENT_COLOR).orNull();
        HImageOptions options = new HImageOptions();
        options.setTransparentColor(transparentColor);
        options.setDisableAnimation(!ctx.isAnimate());
        NDocNodeRendererContext finalCtx = ctx;
        options.setAsyncLoad(() -> finalCtx.repaint());
        options.setImageObserver(ctx.imageObserver());
        options.setSize(new Dimension(b.getWidth().intValue(), b.getHeight().intValue()));

        Object img = p.getPropertyValue(HPropName.VALUE).orNull();
        NOptional<TsonElement> imgStr = NDocObjEx.of(img).asTsonStringOrName();
        if (imgStr.isPresent()) {
            img = ctx.resolvePath(imgStr.get(), p);
        }

        NDocGraphics g = ctx.graphics();

        double x = b.getX();
        double y = b.getY();

        if (!ctx.isDry()) {
            if (HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillRect((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }

            HNodeRendererUtils.applyForeground(p, g, ctx, false);
            if (img instanceof NPath) {
                NPath imgPath = (NPath) img;
                NPath vp = resolveImagePath(imgPath);
                if (vp != null) {
                    try {
                        g.drawImage(vp, x, y, options);
                    } catch (Exception ex) {
                        HResource src = ctx.engine().computeSource(p);
                        ctx.log().log(HMsg.of(NMsg.ofC("[%s] [ERROR] error loading image : %s (%s)",
                                src == null ? null : src.shortName(),
                                vp, ex).asSevere(), src));

                    }
                } else {
                    int descent = g.getFontMetrics().getAscent();
                    g.drawString("Image not found "+imgPath, x, y+descent,new NDocTextOptions().setForegroundColor(Color.YELLOW).setBackgroundColor(Color.RED).setFontSize(8.0f));
                    HResource src = ctx.engine().computeSource(p);
                    ctx.log().log(HMsg.of(NMsg.ofC("[%s] [ERROR] image not found : %s",
                            src == null ? null : src.shortName(),
                            img).asSevere(), src));
                }
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

    private NPath resolveImagePath(NPath p) {
        if (p.isRegularFile()) {
            return p;
        }
        for (String ext : new String[]{"png", "jpg", "gif", "jpeg","svg"}) {
            String n = p.getName();
            String nLowered = n.toLowerCase();
            if (nLowered.endsWith(".")) {
                NPath p2 = p.resolveSibling(n + ext);
                if (p2.isRegularFile()) {
                    return p2;
                }
                p2 = p.resolveSibling(n + ext.toUpperCase());
                if (p2.isRegularFile()) {
                    return p2;
                }
            } else if (nLowered.endsWith(".*")) {
                NPath p2 = p.resolveSibling(n.substring(0, n.length() - 1) + ext);
                if (p2.isRegularFile()) {
                    return p2;
                }
                p2 = p.resolveSibling(n.substring(0, n.length() - 1) + ext.toUpperCase());
                if (p2.isRegularFile()) {
                    return p2;
                }
            } else {
                NPath p2 = p.resolveSibling(n + "." + ext);
                if (p2.isRegularFile()) {
                    return p2;
                }
                p2 = p.resolveSibling(n + "." + ext.toUpperCase());
                if (p2.isRegularFile()) {
                    return p2;
                }
            }
        }
        return null;

    }

}
