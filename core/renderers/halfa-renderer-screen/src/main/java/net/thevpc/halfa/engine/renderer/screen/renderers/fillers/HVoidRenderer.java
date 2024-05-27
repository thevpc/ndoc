package net.thevpc.halfa.engine.renderer.screen.renderers.fillers;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;

public class HVoidRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HVoidRenderer() {
        super(
                HNodeType.VOID,
                HNodeType.ASSIGN
                );
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        //Do nothing
    }


}
