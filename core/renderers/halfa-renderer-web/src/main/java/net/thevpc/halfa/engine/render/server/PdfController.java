package net.thevpc.halfa.engine.render.server;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PdfController {

    @GetMapping("/renderPdf")
    @ResponseBody
    public String renderPdf() {
        NSession session = Nuts.openWorkspace();

        HEngine e = new HEngineImpl(session);
        System.out.println(NPath.ofUserDirectory(session));
        NPath file = NPath.of("/home/vpc/xprojects/nuts/nuts-enterprise/halfa/documentation/tson-doc/main.hd", session)
                .toAbsolute()
                .normalize();

        HDocument doc = e.loadDocument(file, null).get();
        HDocumentStreamRenderer renderer = e.newStreamRenderer("hd");
        renderer.setOutput(NPath.ofUserHome(session).resolve("exemple.hd"));

        renderer.render(doc);

        return "PDF rendu avec succès!";
    }
}
