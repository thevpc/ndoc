package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.util.HUtils;

public class HSphereRenderer extends AbstractHNodeRenderer {
    HProperties sphereDefaultStyles = new HProperties();
    HProperties ellipsoidDefaultStyles = new HProperties();

    public HSphereRenderer() {
        super(HNodeType.SPHERE,HNodeType.ELLIPSOID);
        sphereDefaultStyles.set(HPropName.PRESERVE_ASPECT_RATIO,true);
        ellipsoidDefaultStyles.set(HPropName.PRESERVE_ASPECT_RATIO,false);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        HProperties defaultStyles=sphereDefaultStyles;
        switch (p.type()){
            case HNodeType.SPHERE:{
                defaultStyles=sphereDefaultStyles;
                break;
            }
            case HNodeType.ELLIPSOID:{
                defaultStyles=ellipsoidDefaultStyles;
                break;
            }
        }
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Bounds2 b = selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        HGraphics g = ctx.graphics();
        boolean someBG=false;
        if (!ctx.isDry()) {
            if (someBG = applyBackgroundColor((HNode) p, g, ctx)) {
                g.fillSphere((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), 45, 50f);
            }
            if (applyLineColor((HNode) p, g, ctx, !someBG)) {
                applyStroke(p, g, ctx);
                g.drawOval((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }
        }
        paintDebugBox(p, ctx, g, b);
    }

}
