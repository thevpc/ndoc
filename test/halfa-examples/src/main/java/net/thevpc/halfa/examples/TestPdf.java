package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.DefaultHLogger;
import net.thevpc.halfa.engine.DefaultHEngine;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

public class TestPdf {
    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        HEngine e = new DefaultHEngine();
        NPath file = NPath.of("documentation/halfa-doc").toAbsolute().normalize();
        HDocument doc = e.loadDocument(file, new DefaultHLogger(null)).get();
        HDocumentStreamRenderer renderer = e.newPdfRenderer().get();
        renderer.setOutput(file.resolve("output.pdf"));
        renderer.render(doc);
    }
}
