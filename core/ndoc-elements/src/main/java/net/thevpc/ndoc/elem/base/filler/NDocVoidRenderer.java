package net.thevpc.ndoc.elem.base.filler;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;

public class NDocVoidRenderer extends NDocNodeRendererBase {

    public NDocVoidRenderer() {
        super(
                HNodeType.VOID,
                HNodeType.ASSIGN,
                HNodeType.DEFINE
        );
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        //Do nothing
    }


}
