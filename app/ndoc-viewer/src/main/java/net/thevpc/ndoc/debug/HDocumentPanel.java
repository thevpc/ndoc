package net.thevpc.ndoc.debug;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class HDocumentPanel extends TsonPanel {
    private Supplier<NDocument> model;
    private NDocEngine engine;

    public HDocumentPanel(NDocEngine engine, Supplier<NDocument> model) {
        this.model = model;
        this.engine = engine;
    }

    @Override
    public NElement createTson() {
        NDocument m = null;
        m = model.get();
        if (m != null) {
            return engine.toElement(m);
        }
        return null;
    }

}
