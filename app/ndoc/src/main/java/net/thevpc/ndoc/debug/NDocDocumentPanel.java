package net.thevpc.ndoc.debug;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.nuts.elem.NElement;

import java.util.function.Supplier;

public class NDocDocumentPanel extends TsonPanel {
    private Supplier<NDocument> model;
    private NDocEngine engine;
    private NDocLogger logger;

    public NDocDocumentPanel(NDocEngine engine, Supplier<NDocument> model,NDocLogger logger) {
        this.model = model;
        this.engine = engine;
        this.logger = logger;
    }

    @Override
    public NElement createTson() {
        NDocument m = null;
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
