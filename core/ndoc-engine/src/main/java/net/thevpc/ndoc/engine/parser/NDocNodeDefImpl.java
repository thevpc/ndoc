package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeDef;
import net.thevpc.ndoc.api.document.node.NDocNodeDefParam;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.engine.document.DefaultNDocNode;

public class NDocNodeDefImpl implements NDocNodeDef {
    private final String templateName;
    private final NDocNodeDefParam[] params;
    private final NDocNode[]  body;
    private final NDocResource source;
    private final NDocNode parent;

    public NDocNodeDefImpl(NDocNode parent,String templateName, NDocNodeDefParam[] params, NDocNode[] body, NDocResource source) {
        this.templateName = templateName;
        this.params = params;
        this.body = body;
        for (NDocNode nDocNode : body) {
            ((DefaultNDocNode)nDocNode).setParent(this);
        }
        this.source = source;
        this.parent = parent;
    }

    public NDocNode parent() {
        return parent;
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
