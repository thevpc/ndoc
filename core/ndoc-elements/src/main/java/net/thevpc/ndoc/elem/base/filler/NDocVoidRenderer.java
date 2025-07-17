package net.thevpc.ndoc.elem.base.filler;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;

public class NDocVoidRenderer extends NDocNodeRendererBase {

    public NDocVoidRenderer() {
        super(
                NDocNodeType.VOID,
                NDocNodeType.ASSIGN,
                NDocNodeType.DEFINE
        );
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        //Do nothing
    }


}
