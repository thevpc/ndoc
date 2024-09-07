package net.thevpc.halfa.engine.parser.nodes.assign;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HCtrlAssignParser extends HNodeParserBase {

    public HCtrlAssignParser() {
        super(false, HNodeType.ASSIGN);
    }

    @Override
    public NCallableSupport<HItem> parseNode(HNodeFactoryParseContext context) {
        TsonElement c = context.element();
        HEngine engine = context.engine();
        HDocumentFactory f = engine.documentFactory();
        NSession session = context.session();
        switch (c.type()) {
            case PAIR: {
                TsonPair p = c.toPair();
                TsonElement k = p.key();
                TsonElement v = p.value();
                ObjEx kh = ObjEx.of(k);
                NOptional<String> nn = kh.asStringOrName();
                if (nn.isPresent()) {
                    String nnn = NStringUtils.trim(nn.get());
                    if (nnn.length() > 1 && nnn.startsWith("$")) {
                        return NCallableSupport.of(10, () -> f.ofAssign(
                                nnn.substring(1),
                                v
                        ));
                    }
                }
                break;
            }
        }
        throw new NIllegalArgumentException(session, NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c));
    }

    @Override
    public TsonElement toTson(HNode item) {
        HNode n = (HNode) item;
        Object varName = "var";
        Object varValue = null;

        NOptional<Object> s = n.getPropertyValue(HPropName.NAME);

        if (!s.isEmpty()) {
            varName = s.get();
        }

        s = n.getPropertyValue(HPropName.VALUE);
        if (!s.isEmpty()) {
            varValue = s.get();
        }

        return Tson.ofPair("$" + varName, HUtils.toTson(varValue));
    }

}
