package net.thevpc.halfa.elem.base.image;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HImageOptions;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByType;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class HImageRenderer extends HNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public HImageRenderer() {
        super(HNodeType.IMAGE);
    }


    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        int w = HUtils.intOf(b.getWidth());
        int h = HUtils.intOf(b.getHeight());
        if (w <= 0 || h <= 0) {
            return;
        }

        Color transparentColor = HValueByType.getColor(p, ctx, HPropName.TRANSPARENT_COLOR).orNull();
        HImageOptions options = new HImageOptions();
        options.setTransparentColor(transparentColor);
        options.setDisableAnimation(!ctx.isAnimated());
        HNodeRendererContext finalCtx = ctx;
        options.setAsyncLoad(() -> finalCtx.repaint());
        options.setImageObserver(ctx.imageObserver());
        options.setSize(new Dimension(b.getWidth().intValue(), b.getHeight().intValue()));

        Object img = p.getPropertyValue(HPropName.VALUE).orNull();
        NOptional<String> imgStr = ObjEx.of(img).asStringOrName();
        if (imgStr.isPresent()) {
            img = ctx.resolvePath(imgStr.get(), p);
        }

        HGraphics g = ctx.graphics();

        double x = b.getX();
        double y = b.getY();

        if (!ctx.isDry()) {
            if (HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }

            HNodeRendererUtils.applyForeground(p, g, ctx, false);
            if (img instanceof NPath) {
                try {
                    g.drawImage((NPath) img, x, y, options);
                } catch (Exception ex) {
                    HResource src = ctx.engine().computeSource(p);
                    ctx.messages().addError(NMsg.ofC("[%s] [ERROR] error loading image : %s (%s)",
                            src == null ? null : src.shortName(),
                            img, ex), src);

                }
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }


}
