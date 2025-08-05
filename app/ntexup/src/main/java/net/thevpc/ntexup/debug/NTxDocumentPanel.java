package net.thevpc.ntexup.debug;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.log.NTxLogger;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class NTxDocumentPanel extends NTxTsonPanel {
    private Supplier<NTxDocument> model;
    private NTxEngine engine;
    private NTxLogger logger;

    public NTxDocumentPanel(NTxEngine engine, Supplier<NTxDocument> model, NTxLogger logger) {
        this.model = model;
        this.engine = engine;
        this.logger = logger;
    }

    @Override
    public NElement createTson() {
        NTxDocument m = null;
        m = model.get();
        if (m != null) {
            return engine.toElement(m);
        }
        return null;
    }

}
