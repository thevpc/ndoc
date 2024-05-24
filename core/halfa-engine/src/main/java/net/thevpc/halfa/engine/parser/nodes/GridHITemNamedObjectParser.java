package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.Int2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.engine.parser.util.TsonElementParseHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class GridHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
    public GridHITemNamedObjectParser() {
        super("grid");
    }

    @Override
    protected HNode node(HDocumentFactory f) {
        return f.ofGrid();
    }

    @Override
    protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        if (
                !p.getStyle(HStyleType.COLUMNS).isPresent()
                        && !p.getStyle(HStyleType.ROWS).isPresent()) {
            NOptional<Int2> dp = new TsonElementParseHelper(e).asInt2();
            if (dp.isPresent()) {
                p.set(HStyles.columns(dp.get().getX()));
                p.set(HStyles.rows(dp.get().getY()));
                return true;
            }
        } else if (!p.getStyle(HStyleType.COLUMNS).isPresent()) {
            NOptional<Integer> dp = new TsonElementParseHelper(e).asInt();
            p.set(HStyles.columns(dp.get()));
            return true;
        } else if (!p.getStyle(HStyleType.ROWS).isPresent()) {
            NOptional<Integer> dp = new TsonElementParseHelper(e).asInt();
            p.set(HStyles.rows(dp.get()));
            return true;
        }
        return false;
    }
}
