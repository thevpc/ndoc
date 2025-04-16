package net.thevpc.ndoc.debug;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class HNodePanel extends TsonPanel {
    private Supplier<HNode> model;
    private NDocEngine engine;

    public HNodePanel(NDocEngine engine, Supplier<HNode> model) {
        this.model = model;
        this.engine = engine;
    }

    @Override
    public NElement createTson() {
        HNode m = null;
        m = model.get();
        if (m != null) {
            return engine.toElement(m);
        }
        return null;
    }

}
