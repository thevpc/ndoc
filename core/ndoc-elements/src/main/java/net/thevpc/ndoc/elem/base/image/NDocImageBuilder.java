/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.image;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocImageOptions;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.eval.NDocValueByType;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;


/**
 * @author vpc
 */
public class NDocImageBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.IMAGE)
                .parseParam().named(NDocPropName.TRANSPARENT_COLOR).then()
                .parseParam().named(NDocPropName.VALUE, NDocPropName.FILE, "content", "src").set(NDocPropName.VALUE).asString().then()
                .parseParam().matchesStringOrName().set(NDocPropName.VALUE).ignoreDuplicates(true).asString().then()
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext rendererContext,NDocNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = rendererContext.selfBounds(p);
        int w = NDocUtils.intOf(b.getWidth());
        int h = NDocUtils.intOf(b.getHeight());
        if (w <= 0 || h <= 0) {
            return;
        }

        Color transparentColor = NDocValueByType.getColor(p, rendererContext, NDocPropName.TRANSPARENT_COLOR).orNull();
        NDocImageOptions options = new NDocImageOptions();
        options.setTransparentColor(transparentColor);
        options.setDisableAnimation(!rendererContext.isAnimate());
        NDocNodeRendererContext finalCtx = rendererContext;
        options.setAsyncLoad(() -> finalCtx.repaint());
        options.setImageObserver(rendererContext.imageObserver());
        options.setSize(new Dimension(b.getWidth().intValue(), b.getHeight().intValue()));

        Object img = p.getPropertyValue(NDocPropName.VALUE).orNull();
        NOptional<NElement> imgStr = NDocObjEx.of(img).asTsonStringOrName();
        if (imgStr.isPresent()) {
            img = rendererContext.resolvePath(imgStr.get(), p);
        }

        NDocGraphics g = rendererContext.graphics();

        double x = b.getX();
        double y = b.getY();

        if (!rendererContext.isDry()) {
            if (NDocNodeRendererUtils.applyBackgroundColor(p, g, rendererContext)) {
                g.fillRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
            }

            NDocNodeRendererUtils.applyForeground(p, g, rendererContext, false);
            if (img instanceof NPath) {
                NPath imgPath = (NPath) img;
                NPath vp = resolveImagePath(imgPath);
                if (vp != null) {
                    try {
                        g.drawImage(vp, x, y, options);
                    } catch (Exception ex) {
                        NDocResource src = NDocUtils.sourceOf(p);
                        rendererContext.log().log(NMsg.ofC("[%s] [ERROR] error loading image : %s (%s)",
                                src == null ? null : src.shortName(),
                                vp, ex).asSevere(), src);

                    }
                } else {
                    int descent = g.getFontMetrics().getAscent();
                    g.drawString("Image not found "+imgPath, x, y+descent,new NDocTextOptions().setForegroundColor(Color.YELLOW).setBackgroundColor(Color.RED).setFontSize(8.0f));
                    NDocResource src = NDocUtils.sourceOf(p);
                    rendererContext.log().log(NMsg.ofC("[%s] [ERROR] image not found : %s",
                            src == null ? null : src.shortName(),
                            img).asSevere(), src);
                }
            }
        }
        NDocNodeRendererUtils.paintDebugBox(p, rendererContext, g, b);
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
