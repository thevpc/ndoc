package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeDef;
import net.thevpc.ndoc.api.model.node.NDocNodeDefParam;
import net.thevpc.ndoc.api.resources.NDocResource;

public class NDocNodeDefImpl implements NDocNodeDef {
    private final String templateName;
    private final NDocNodeDefParam[] params;
    private final NDocNode[]  body;
    private final NDocResource source;

    public NDocNodeDefImpl(String templateName, NDocNodeDefParam[] params, NDocNode[] body, NDocResource source) {
        this.templateName = templateName;
        this.params = params;
        this.body = body;
        this.source = source;
    }

    public NDocResource source() {
        return source;
    }

    @Override
    public String name() {
        return templateName;
    }

    @Override
    public NDocNodeDefParam[] params() {
        return params;
    }

    @Override
    public NDocNode[] body() {
        return body;
    }
}
