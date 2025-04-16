package net.thevpc.ndoc.debug;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class HTsonPanel extends TsonPanel {
    private Supplier<NElement> model;

    public HTsonPanel(Supplier<NElement> model) {
        this.model = model;
    }

    @Override
    public NElement createTson() {
        return model.get();
    }
    
}
