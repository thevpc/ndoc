package net.thevpc.halfa.elem.base.control;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonOp;
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
            case OP:{
                TsonOp binOp = c.toOp();
                if("=".equals(binOp.opName())) {
                    TsonElement k = binOp.first();
                    TsonElement v = binOp.second();
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
                }
                break;
            }
        }
        return NCallableSupport.invalid(NMsg.ofC("[%s] unable to resolve node : %s", net.thevpc.halfa.api.util.HUtils.shortName(context.source()), c));
    }

    @Override
    public TsonElement toTson(HNode item) {
        Object varName = "var";
        Object varValue = null;

        NOptional<TsonElement> s = item.getPropertyValue(HPropName.NAME);

        if (!s.isEmpty()) {
            varName = s.get();
        }

        s = item.getPropertyValue(HPropName.VALUE);
        if (!s.isEmpty()) {
            varValue = s.get();
        }

        return Tson.ofPair("$" + varName, HUtils.toTson(varValue));
    }

}
