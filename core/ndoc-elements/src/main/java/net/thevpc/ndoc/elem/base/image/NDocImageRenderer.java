package net.thevpc.ndoc.elem.base.image;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocImageOptions;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocResource;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValueByType;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

import java.awt.*;

public class NDocImageRenderer extends NDocNodeRendererBase {

    NDocProperties defaultStyles = new NDocProperties();

    public NDocImageRenderer() {
        super(NDocNodeType.IMAGE);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = selfBounds(p, ctx);
        int w = NDocUtils.intOf(b.getWidth());
        int h = NDocUtils.intOf(b.getHeight());
        if (w <= 0 || h <= 0) {
            return;
        }

        Color transparentColor = NDocValueByType.getColor(p, ctx, NDocPropName.TRANSPARENT_COLOR).orNull();
        NDocImageOptions options = new NDocImageOptions();
        options.setTransparentColor(transparentColor);
        options.setDisableAnimation(!ctx.isAnimate());
        NDocNodeRendererContext finalCtx = ctx;
        options.setAsyncLoad(() -> finalCtx.repaint());
        options.setImageObserver(ctx.imageObserver());
        options.setSize(new Dimension(b.getWidth().intValue(), b.getHeight().intValue()));

        Object img = p.getPropertyValue(NDocPropName.VALUE).orNull();
        NOptional<NElement> imgStr = NDocObjEx.of(img).asTsonStringOrName();
        if (imgStr.isPresent()) {
            img = ctx.resolvePath(imgStr.get(), p);
        }

        NDocGraphics g = ctx.graphics();

        double x = b.getX();
        double y = b.getY();

        if (!ctx.isDry()) {
            if (NDocNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
            }

            NDocNodeRendererUtils.applyForeground(p, g, ctx, false);
            if (img instanceof NPath) {
                NPath imgPath = (NPath) img;
                NPath vp = resolveImagePath(imgPath);
                if (vp != null) {
                    try {
                        g.drawImage(vp, x, y, options);
                    } catch (Exception ex) {
                        NDocResource src = NDocUtils.sourceOf(p);
                        ctx.log().log(NMsg.ofC("[%s] [ERROR] error loading image : %s (%s)",
                                src == null ? null : src.shortName(),
                                vp, ex).asSevere(), src);

                    }
                } else {
                    int descent = g.getFontMetrics().getAscent();
                    g.drawString("Image not found "+imgPath, x, y+descent,new NDocTextOptions().setForegroundColor(Color.YELLOW).setBackgroundColor(Color.RED).setFontSize(8.0f));
                    NDocResource src = NDocUtils.sourceOf(p);
                    ctx.log().log(NMsg.ofC("[%s] [ERROR] image not found : %s",
                            src == null ? null : src.shortName(),
                            img).asSevere(), src);
                }
            }
        }
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
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
