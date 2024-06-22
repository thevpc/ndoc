package net.thevpc.halfa.debug;

import net.thevpc.tson.TsonElement;

import java.util.function.Supplier;

public class HTsonPanel extends TsonPanel {
    private Supplier<TsonElement> model;

    public HTsonPanel(Supplier<TsonElement> model) {
        this.model = model;
    }

    @Override
    public TsonElement createTson() {
        return model.get();
    }
    
}
