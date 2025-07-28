package net.thevpc.ndoc.api.base.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.HashMap;
import java.util.Map;

public class ParseArgumentInfo {
    public String id;
    public String uid;
    public NElement tsonElement;
    public NDocNode node;
    public NElement currentArg;
    public NElement[] arguments;
    public int currentArgIndex;
    public NDocDocumentFactory f;
    public NDocNodeFactoryParseContext context;
    public Map<String, Object> props = new HashMap<>();

    public NDocResource source() {
        return context.source();
    }
}
