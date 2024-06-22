package net.thevpc.halfa.debug;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.tson.TsonElement;

import java.util.function.Supplier;

public class HDocumentPanel extends TsonPanel {
    private Supplier<HDocument> model;
    private HEngine engine;

    public HDocumentPanel(HEngine engine, Supplier<HDocument> model) {
        this.model = model;
        this.engine = engine;
    }

    @Override
    public TsonElement createTson() {
        HDocument m = null;
        m = model.get();
        if (m != null) {
            return engine.toTson(m);
        }
        return null;
    }

}
