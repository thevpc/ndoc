package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.spi.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.nuts.elem.NElement;

public class NDocSphereRenderer extends NDocNodeRendererBase {
    NDocProperties sphereDefaultStyles = new NDocProperties();
    NDocProperties ellipsoidDefaultStyles = new NDocProperties();

    public NDocSphereRenderer() {
        super(NDocNodeType.SPHERE, NDocNodeType.ELLIPSOID);
        sphereDefaultStyles.set(NDocPropName.PRESERVE_ASPECT_RATIO, NElement.ofTrue());
        ellipsoidDefaultStyles.set(NDocPropName.PRESERVE_ASPECT_RATIO, NElement.ofTrue());
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        NDocProperties defaultStyles = sphereDefaultStyles;
        switch (p.type()) {
            case NDocNodeType.SPHERE: {
                defaultStyles = sphereDefaultStyles;
                break;
            }
            case NDocNodeType.ELLIPSOID: {
                defaultStyles = ellipsoidDefaultStyles;
                break;
            }
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        NDocGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            if (someBG = NDocNodeRendererUtils.applyBackgroundColor((NDocNode) p, g, ctx)) {
                g.fillSphere((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), 45, 50f);
            }
            if (NDocNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                NDocNodeRendererUtils.withStroke(p, g, ctx,()->{
                    g.drawOval((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
                });
            }
        }
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b, false);
    }

}
