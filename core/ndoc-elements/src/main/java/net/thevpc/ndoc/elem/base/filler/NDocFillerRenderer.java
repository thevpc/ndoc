package net.thevpc.ndoc.elem.base.filler;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public class NDocFillerRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public NDocFillerRenderer() {
        super(HNodeType.FILLER);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        NDocGraphics g = ctx.graphics();
        Bounds2 bounds = ctx.getBounds();
        Bounds2 b = new Bounds2(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight());
        if (!ctx.isDry()) {
            HNodeRendererUtils.paintBackground(p, ctx, g, bounds);
            HNodeRendererUtils.paintBorderLine(p, ctx, g, b);
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }


}
