package net.thevpc.ntexup.debug;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class NDocDocumentPanel extends TsonPanel {
    private Supplier<NTxDocument> model;
    private NTxEngine engine;
    private NDocLogger logger;

    public NDocDocumentPanel(NTxEngine engine, Supplier<NTxDocument> model, NDocLogger logger) {
        this.model = model;
        this.engine = engine;
        this.logger = logger;
    }

    @Override
    public NElement createTson() {
        NTxDocument m = null;
        m = model.get();
        if (m != null) {
//            NDocNode r = m.root();
//            List<NDocNode> nDocNodes = engine.compilePage(r, m, logger);
//            NArrayElement uu = NElement.ofArray(nDocNodes.stream().map(x -> engine.toElement(x)).toArray(NElement[]::new));
            return engine.toElement(m);
//            return uu;
        }
        return null;
    }

}
