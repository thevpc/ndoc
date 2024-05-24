package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.tson.TsonElement;

public class TextHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
    public TextHITemNamedObjectParser() {
        super("text");
    }

    @Override
    protected HNode node(HDocumentFactory f) {
        return f.ofText();
    }

    @Override
    protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                ((HText) p).setValue(e.toStr().raw());
                return true;
            }
        }
        return false;
    }
}
