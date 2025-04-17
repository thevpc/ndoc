package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;

public class NDocSphereRenderer extends NDocNodeRendererBase {
    HProperties sphereDefaultStyles = new HProperties();
    HProperties ellipsoidDefaultStyles = new HProperties();

    public NDocSphereRenderer() {
        super(HNodeType.SPHERE, HNodeType.ELLIPSOID);
        sphereDefaultStyles.set(HPropName.PRESERVE_ASPECT_RATIO, Tson.ofTrue());
        ellipsoidDefaultStyles.set(HPropName.PRESERVE_ASPECT_RATIO, Tson.ofTrue());
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        HProperties defaultStyles = sphereDefaultStyles;
        switch (p.type()) {
            case HNodeType.SPHERE: {
                defaultStyles = sphereDefaultStyles;
                break;
            }
            case HNodeType.ELLIPSOID: {
                defaultStyles = ellipsoidDefaultStyles;
                break;
            }
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        NDocGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            if (someBG = HNodeRendererUtils.applyBackgroundColor((HNode) p, g, ctx)) {
                g.fillSphere((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight()), 45, 50f);
            }
            if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                HNodeRendererUtils.withStroke(p, g, ctx,()->{
                    g.drawOval((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
                });
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b, false);
    }

}
