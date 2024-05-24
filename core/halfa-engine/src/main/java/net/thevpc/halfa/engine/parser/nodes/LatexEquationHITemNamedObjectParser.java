package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HLatexEquation;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.tson.TsonElement;

public class LatexEquationHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
    public LatexEquationHITemNamedObjectParser() {
        super("equation", "eq");
    }

    @Override
    protected HNode node(HDocumentFactory f) {
        return f.ofEquation();
    }

    @Override
    protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                ((HLatexEquation) p).setValue(e.toStr().raw());
                return true;
            }
        }
        return false;
    }
}
