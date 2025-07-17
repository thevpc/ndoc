package net.thevpc.ndoc.elem.base.control;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElementType;
import net.thevpc.nuts.elem.NOperatorElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

public class NDocCtrlAssignParser extends NDocNodeParserBase {

    public NDocCtrlAssignParser() {
        super(false, NDocNodeType.ASSIGN);
    }

    @Override
    public NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = engine.documentFactory();
        switch (c.type().typeGroup()){
            case OPERATOR:{
                NOperatorElement binOp = c.asOperator().get();
                if (binOp.type()== NElementType.OP_EQ) {
                    NElement k = binOp.first().orNull();
                    NElement v = binOp.second().orNull();
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
        switch (c.type()) {
            case PAIR: {
                NPairElement p = c.asPair().get();
                NElement k = p.key();
                NElement v = p.value();
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
        }
        return NCallableSupport.invalid(NMsg.ofC("[%s] unable to resolve node : %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), c));
    }

    @Override
    public NElement toElem(NDocNode item) {
        Object varName = "var";
        Object varValue = null;

        NOptional<NElement> s = item.getPropertyValue(NDocPropName.NAME);

        if (!s.isEmpty()) {
            varName = s.get();
        }

        s = item.getPropertyValue(NDocPropName.VALUE);
        if (!s.isEmpty()) {
            varValue = s.get();
        }

        return NElement.ofPair("$" + varName, HUtils.toElement(varValue));
    }

}
