package net.thevpc.ntexup.engine.parser;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeDef;
import net.thevpc.ntexup.api.document.node.NTxNodeDefParam;
import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.ntexup.engine.document.DefaultNTxNode;

public class NTxNodeDefImpl implements NTxNodeDef {
    private final String templateName;
    private final NTxNodeDefParam[] params;
    private final NTxNode[]  body;
    private final NTxSource source;
    private final NTxNode parent;

    public NTxNodeDefImpl(NTxNode parent, String templateName, NTxNodeDefParam[] params, NTxNode[] body, NTxSource source) {
        this.templateName = templateName;
        this.params = params;
        this.body = body;
        for (NTxNode n : body) {
            ((DefaultNTxNode)n).setParent(this);
        }
        this.source = source;
        this.parent = parent;
    }

    public NTxNode parent() {
        return parent;
    }

    public NTxSource source() {
        return source;
    }

    @Override
    public String name() {
        return templateName;
    }

    @Override
    public NTxNodeDefParam[] params() {
        return params;
    }

    @Override
    public NTxNode[] body() {
        return body;
    }
}
