package net.thevpc.ndoc.debug;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class NDocNodePanel extends TsonPanel {
    private Supplier<NDocNode> model;
    private NDocEngine engine;

    public NDocNodePanel(NDocEngine engine, Supplier<NDocNode> model) {
        this.model = model;
        this.engine = engine;
    }

    @Override
    public NElement createTson() {
        NDocNode m = null;
        m = model.get();
        if (m != null) {
            return engine.toElement(m);
        }
        return null;
    }

}
