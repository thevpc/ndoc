package net.thevpc.ntexup.cmdline;

import net.thevpc.ntexup.api.document.NTxDocumentLoadingResult;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.engine.NTxTemplateInfo;
import net.thevpc.ntexup.api.renderer.NTxDocumentStreamRenderer;
import net.thevpc.ntexup.api.renderer.NTxDocumentStreamRendererConfig;
import net.thevpc.ntexup.config.UserConfigManager;
import net.thevpc.ntexup.engine.impl.DefaultNTxEngine;
import net.thevpc.nuts.NOut;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

public class NTxTerminalProcessor {

    public void runTerminal(Options options) {
        NTxEngine engine = new DefaultNTxEngine();
        engine.importDefaultDependencies();
        switch (options.action) {
            case NEW: {
                break;
            }
            case LIST_TEMPLATES: {
                if (NSession.of().isPlainOut()) {
                    for (NTxTemplateInfo template : engine.getTemplates()) {
                        NOut.println(NMsg.ofC("%s (%s @ %s) %s",
                                NMsg.ofStyledPath(template.url()),
                                NMsg.ofStyledPrimary1(template.name()),
                                NMsg.ofStyledPrimary2(template.repoName()),
                                template.recommended() ? NMsg.ofStyledError(" (*)") : ""
                        ));
                    }
                } else {
                    NOut.println(engine.getTemplates());
                }
                break;
            }
            case DUMP: {
                engine.dump();
                break;
            }
            case BUILD_REPO: {
                RepoBuilderTool tool = new RepoBuilderTool(engine.log());
                if (tool.buildRepository(options.buildRepoPath)) {
                    engine.log().log(NMsg.ofC("repository built successfully at %s", options.buildRepoPath));
                }
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
                    NTxDocumentLoadingResult d = engine.loadDocument(path);
                    if (d.isSuccessful()) {
                        NTxDocument doc = d.document().get();
                        UserConfigManager usersConfigManager = new UserConfigManager();
                        NTxDocumentStreamRendererConfig renderConfig = new NTxDocumentStreamRendererConfig();
                        NTxDocumentStreamRenderer renderer = engine.newPdfRenderer().get();
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
