package net.thevpc.halfa.engine.renderer.screen.renderers.fillers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.XYConstraints;
import net.thevpc.halfa.api.node.HFiller;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HVoid;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;

import java.awt.*;

public class HVoidRenderer extends AbstractHPartRenderer {
    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        return ctx.getBounds();
    }


}
