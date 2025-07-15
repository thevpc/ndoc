package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeDef;
import net.thevpc.ndoc.api.model.node.HNodeDefParam;
import net.thevpc.ndoc.api.resources.HResource;

import java.util.List;

public class HNodeDefImpl implements HNodeDef {
    private final String templateName;
    private final HNodeDefParam[] params;
    private final HNode[]  body;
    private final HResource source;

    public HNodeDefImpl(String templateName, HNodeDefParam[] params, HNode[] body,HResource source) {
        this.templateName = templateName;
        this.params = params;
        this.body = body;
        this.source = source;
    }

    public HResource source() {
        return source;
    }

    @Override
    public String name() {
        return templateName;
    }

    @Override
    public HNodeDefParam[] params() {
        return params;
    }

    @Override
    public HNode[] body() {
        return body;
    }
}
