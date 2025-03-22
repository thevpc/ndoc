package net.thevpc.ndoc.elem.base.control;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonOp;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class NDocCtrlAssignParser extends NDocNodeParserBase {

    public NDocCtrlAssignParser() {
        super(false, HNodeType.ASSIGN);
    }

    @Override
    public NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context) {
        TsonElement c = context.element();
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = engine.documentFactory();
        switch (c.type()) {
            case PAIR: {
                TsonPair p = c.toPair();
                TsonElement k = p.key();
                TsonElement v = p.value();
                NDocObjEx kh = NDocObjEx.of(k);
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
                    NDocObjEx kh = NDocObjEx.of(k);
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
        return NCallableSupport.invalid(NMsg.ofC("[%s] unable to resolve node : %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), c));
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
