package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.elem.NElement;

public class NTxSphereBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NTxNodeType.SPHERE)
                .renderComponent(this::renderMain)
        ;
        defaultStyles.set(NTxPropName.PRESERVE_ASPECT_RATIO, NElement.ofTrue());
    }

    public void renderMain(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxBounds2 b = ctx.selfBounds(p, null, null);
        double x = b.getX();
        double y = b.getY();
        NTxGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            if (someBG = ctx.applyBackgroundColor((NTxNode) p)) {
                g.fillSphere((int) x, (int) y, NTxUtils.intOf(b.getWidth()), NTxUtils.intOf(b.getHeight()), 45, 50f);
            }
            if (ctx.applyForeground(p, !someBG)) {
                ctx.withStroke(p,()->{
                    g.drawOval((int) x, (int) y, NTxUtils.intOf(b.getWidth()), NTxUtils.intOf(b.getHeight()));
                });
            }
        }
        ctx.paintDebugBox(p, b, false);
    }

}
