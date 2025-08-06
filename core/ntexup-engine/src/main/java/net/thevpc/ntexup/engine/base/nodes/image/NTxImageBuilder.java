/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.image;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxImageOptions;
import net.thevpc.ntexup.api.document.elem2d.NTxSize;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.eval.NTxValueByType;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.util.NTxSizeRef;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;


/**
 * @author vpc
 */
public class NTxImageBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NTxNodeType.IMAGE)
                .parseParam().named(NTxPropName.TRANSPARENT_COLOR).then()
                .parseParam().named(NTxPropName.VALUE, NTxPropName.FILE, "content", "src").store(NTxPropName.VALUE).then()
                .parseParam().matchesStringOrName().store(NTxPropName.VALUE).ignoreDuplicates(true).then()
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NTxNode p, NTxNodeRendererContext rendererContext, NTxNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NTxBounds2 b = rendererContext.selfBounds(p);
        int w = NTxUtils.intOf(b.getWidth());
        int h = NTxUtils.intOf(b.getHeight());
        if (w <= 0 || h <= 0) {
            return;
        }

        Color transparentColor = NTxValueByType.getColor(p, rendererContext, NTxPropName.TRANSPARENT_COLOR).orNull();
        NTxImageOptions options = new NTxImageOptions();
        options.setTransparentColor(transparentColor);
        options.setDisableAnimation(!rendererContext.isAnimate());
        NTxNodeRendererContext finalCtx = rendererContext;
        options.setAsyncLoad(() -> finalCtx.repaint());
        options.setImageObserver(rendererContext.imageObserver());
        options.setSize(new Dimension(b.getWidth().intValue(), b.getHeight().intValue()));

        NElement eimg = p.getPropertyValue(NTxPropName.VALUE).orNull();
        NElement eimg2 = rendererContext.engine().evalExpression(eimg, p, rendererContext.varProvider());
        NPath img = rendererContext.engine().resolvePath(eimg2, p);

        NTxGraphics g = rendererContext.graphics();

        double x = b.getX();
        double y = b.getY();

        if (!rendererContext.isDry()) {
            if (rendererContext.applyBackgroundColor(p)) {
                g.fillRect((int) x, (int) y, NTxUtils.intOf(b.getWidth()), NTxUtils.intOf(b.getHeight()));
            }

            rendererContext.applyForeground(p, false);
            if (img instanceof NPath) {
                NPath imgPath = (NPath) img;
                NPath vp = resolveImagePath(imgPath);
                if (vp != null) {
                    try {
                        g.drawImage(vp, x, y, options);
                    } catch (Exception ex) {
                        NTxSource src = NTxUtils.sourceOf(p);
                        rendererContext.log().log(NMsg.ofC("[%s] [ERROR] error loading image : %s (%s)",
                                src == null ? null : src.shortName(),
                                vp, ex).asSevere(), src);

                    }
                } else {
                    int descent = g.getFontMetrics().getAscent();
                    NTxTextOptions nTxTextOptions = new NTxTextOptions();
                    nTxTextOptions.sr=rendererContext.sizeRef();
                    g.drawString("Image not found "+imgPath, x, y+descent, nTxTextOptions.setForegroundColor(Color.YELLOW).setBackgroundColor(Color.RED).setFontSize(NTxSize.ofPx(8.0f)));
                    NTxSource src = NTxUtils.sourceOf(p);
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
