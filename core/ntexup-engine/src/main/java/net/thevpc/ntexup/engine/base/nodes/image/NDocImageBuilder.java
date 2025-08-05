/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.image;

import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.elem2d.NDocImageOptions;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.eval.NDocValueByType;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NDocTextOptions;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;


/**
 * @author vpc
 */
public class NDocImageBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.IMAGE)
                .parseParam().named(NDocPropName.TRANSPARENT_COLOR).then()
                .parseParam().named(NDocPropName.VALUE, NDocPropName.FILE, "content", "src").store(NDocPropName.VALUE).then()
                .parseParam().matchesStringOrName().store(NDocPropName.VALUE).ignoreDuplicates(true).then()
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
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

        NElement eimg = p.getPropertyValue(NDocPropName.VALUE).orNull();
        NElement eimg2 = rendererContext.engine().evalExpression(eimg, p, rendererContext.varProvider());
        NPath img = rendererContext.engine().resolvePath(eimg2, p);

        NDocGraphics g = rendererContext.graphics();

        double x = b.getX();
        double y = b.getY();

        if (!rendererContext.isDry()) {
            if (rendererContext.applyBackgroundColor(p)) {
                g.fillRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
            }

            rendererContext.applyForeground(p, false);
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
                            eimg).asSevere(), src);
                }
            }
        }
        rendererContext.paintDebugBox(p, b);
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
