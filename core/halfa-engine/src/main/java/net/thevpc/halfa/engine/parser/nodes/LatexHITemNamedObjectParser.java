package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HLatex;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.tson.TsonElement;

public class LatexHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
    public LatexHITemNamedObjectParser() {
        super("latex", "tex");
    }

    @Override
    protected HNode node(HDocumentFactory f) {
        return f.ofLatex();
    }

    @Override
    protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                ((HLatex) p).setValue(e.toStr().raw());
                return true;
            }
        }
        return false;
    }
}
