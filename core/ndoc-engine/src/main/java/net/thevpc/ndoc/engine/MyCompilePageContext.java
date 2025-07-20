package net.thevpc.ndoc.engine;

import net.thevpc.ndoc.api.CompilePageContext;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.engine.eval.NDocNodeEvalNDoc;
import net.thevpc.nuts.elem.NElement;

public class MyCompilePageContext implements CompilePageContext {
    private final NDocLogger messages;
    private final NDocument document;

    public MyCompilePageContext(NDocLogger messages,NDocument document) {
        this.messages = messages;
        this.document = document;
    }

    public NDocument document() {
        return document;
    }

    @Override
    public NDocLogger messages() {
        return messages;
    }
}
