package net.thevpc.halfa.engine.renderer.screen.renderers.fillers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;

public class HFillerRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HFillerRenderer() {
        super(HNodeType.FILLER);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        HGraphics g = ctx.graphics();
        Bounds2 bounds = ctx.getBounds();
        Bounds2 b = new Bounds2(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight());
        if (!ctx.isDry()) {
            paintBackground(p, ctx, g, bounds);
            paintBorderLine(p, ctx, g, b);
        }
        paintDebugBox(p, ctx, g, b);
    }


}
