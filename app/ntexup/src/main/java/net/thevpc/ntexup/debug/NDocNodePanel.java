package net.thevpc.ntexup.debug;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class NDocNodePanel extends TsonPanel {
    private Supplier<NTxNode> model;
    private NTxEngine engine;

    public NDocNodePanel(NTxEngine engine, Supplier<NTxNode> model) {
        this.model = model;
        this.engine = engine;
    }

    @Override
    public NElement createTson() {
        NTxNode m = null;
        m = model.get();
        if (m != null) {
            return NTxUtils.removeCompilerDeclarationPathAnnotations(engine.toElement(m));
        }
        return null;
    }

}
