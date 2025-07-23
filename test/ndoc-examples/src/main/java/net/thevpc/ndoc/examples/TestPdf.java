package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRenderer;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

public class TestPdf {
    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e = new DefaultNDocEngine();
        NPath file = NPath.of("documentation/ndoc-doc").toAbsolute().normalize();
        NDocument doc = e.loadDocument(file).get();
        NDocDocumentStreamRenderer renderer = e.newPdfRenderer().get();
        renderer.setOutput(file.resolve("output.pdf"));
        renderer.render(doc);
    }
}
