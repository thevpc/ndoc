package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

public class TestPdf {
    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine e = new HEngineImpl(session);
        NPath file = NPath.of("documentation/halfa-doc", session).toAbsolute().normalize();
        HDocument doc = e.loadDocument(file, new HMessageListImpl(session, null)).get();
        HDocumentStreamRenderer renderer = e.newStreamRenderer("pdf");
        renderer.setOutput(file.resolve("output.pdf"));
        renderer.render(doc);
    }
}
