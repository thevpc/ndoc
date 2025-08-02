package net.thevpc.ndoc.cmdline;

import net.thevpc.ndoc.api.document.NDocDocumentLoadingResult;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRenderer;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererConfig;
import net.thevpc.ndoc.config.UserConfigManager;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.nuts.NOut;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

public class TerminalProcessor {

    public void runTerminal(Options options) {
        NDocEngine engine = new DefaultNDocEngine();
        switch (options.action) {
            case NEW: {
                break;
            }
            case LIST_TEMPLATES:{
                NOut.println(engine.getTemplates());
                break;
            }
            case OPEN: {
                if (options.paths.isEmpty()) {
                    engine.log().log(NMsg.ofC("missing document to open"));
                    return;
                }
                NPath toPdf = options.toPdf;
                if (toPdf == null) {
                    toPdf = NPath.of(".");
                }
                for (NPath path : options.paths) {
                    NDocDocumentLoadingResult d = engine.loadDocument(path);
                    if (d.isSuccessful()) {
                        NDocument doc = d.document().get();
                        UserConfigManager usersConfigManager = new UserConfigManager();
                        NDocDocumentStreamRendererConfig renderConfig = new NDocDocumentStreamRendererConfig();
                        NDocDocumentStreamRenderer renderer = engine.newPdfRenderer().get();
                        renderer.setStreamRendererConfig(renderConfig);
                        NPath output = null;
                        if (options.paths.size() == 1) {
                            if (toPdf.isDirectory()) {
                                output = toPdf.resolve(path.getName()).resolveSiblingWithExtension("pdf");
                            } else {
                                output = toPdf.resolveSiblingWithExtension("pdf");
                            }
                        } else {
                            output = toPdf.resolve(path.getName()).resolveSiblingWithExtension("pdf");
                        }
                        renderer.setOutput(output);
                        renderer.render(doc);
                    }
                }
                break;
            }
        }
    }

}
