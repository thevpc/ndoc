package net.thevpc.halfa.elem.base.filler;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;

public class HVoidRenderer extends HNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public HVoidRenderer() {
        super(
                HNodeType.VOID,
                HNodeType.ASSIGN
        );
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        //Do nothing
    }


}
