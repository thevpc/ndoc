package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.renderer.screen.common.HNodeRendererUtils;
import net.thevpc.halfa.spi.nodes.HPropValueByNameParser;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.ObjEx;

import java.awt.*;

public class HLineRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HLineRenderer() {
        super(HNodeType.LINE);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        HPoint2D from = HPoint.ofParent(ObjEx.ofProp(p, HPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds());
        HPoint2D to = HPoint.ofParent(ObjEx.ofProp(p, HPropName.TO).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds());
        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint fc = HPropValueByNameParser.resolveLineColor(p, ctx, true);
            g.draw2D(HElement2DFactory.line(from,to)
                    .setLineStroke(HNodeRendererUtils.resolveStroke(p, g, ctx))
                    .setLinePaint(fc));
        }
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        Bounds2 b2 = new Bounds2(minx, miny, maxX, maxY);
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b2);
    }

}
