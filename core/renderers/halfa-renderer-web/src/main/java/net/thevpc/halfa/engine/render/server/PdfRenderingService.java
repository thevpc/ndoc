package net.thevpc.halfa.engine.render.server;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;
import org.springframework.stereotype.Service;

@Service
public class PdfRenderingService {

    public String renderPdf() {
        NSession session = Nuts.openWorkspace();

        HEngine e = new HEngineImpl(session);

        NPath file = NPath.of("/home/mohamed/Desktop/stage/halfa/documentation/tson-doc/main.hd", session)
                .toAbsolute()
                .normalize();

        HDocument doc = e.loadDocument(file, null).get();

        HDocumentStreamRenderer renderer = e.newStreamRenderer("pdf");
        renderer.setOutput(NPath.ofUserHome(session).resolve("example.pdf"));

        renderer.render(doc);

        return "PDF rendu avec succès!";
    }
}
