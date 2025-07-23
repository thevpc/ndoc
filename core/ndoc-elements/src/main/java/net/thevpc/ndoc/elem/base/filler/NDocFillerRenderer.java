package net.thevpc.ndoc.elem.base.filler;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocFillerRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocFillerRenderer() {
        super(NDocNodeType.FILLER);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        NDocGraphics g = ctx.graphics();
        NDocBounds2 bounds = ctx.getBounds();
        NDocBounds2 b = new NDocBounds2(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight());
        if (!ctx.isDry()) {
            NDocNodeRendererUtils.paintBackground(p, ctx, g, bounds);
            NDocNodeRendererUtils.paintBorderLine(p, ctx, g, b);
        }
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }


}
