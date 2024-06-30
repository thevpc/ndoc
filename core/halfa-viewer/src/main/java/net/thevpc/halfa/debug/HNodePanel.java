package net.thevpc.halfa.debug;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.tson.TsonElement;

import java.util.function.Supplier;

public class HNodePanel extends TsonPanel {
    private Supplier<HNode> model;
    private HEngine engine;

    public HNodePanel(HEngine engine, Supplier<HNode> model) {
        this.model = model;
        this.engine = engine;
    }

    @Override
    public TsonElement createTson() {
        HNode m = null;
        m = model.get();
        if (m != null) {
            return engine.toTson(m);
        }
        return null;
    }

}
