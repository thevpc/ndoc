package net.thevpc.ntexup.engine.renderer.special;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.engine.renderer.NTxNodeRendererBase;

public class NTxImportRenderer extends NTxNodeRendererBase {
    public NTxImportRenderer() {
        super(NTxNodeType.CTRL_IMPORT);
    }

    @Override
    public void renderMain(NTxNode p, NTxNodeRendererContext ctx) {
    }
}
