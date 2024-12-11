package net.thevpc.halfa.elem.base.control;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.model.DefaultHNode;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.*;

public class HCtrlDefineParser extends HNodeParserBase {

    public HCtrlDefineParser() {
        super(false, HNodeType.DEFINE);
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
                if (v.isNamedObject() || v.isFunction()) {
                    TsonObject object = v.toObject();
                    String name = object.name();
                    if (!NBlankable.isBlank(name)) {
                        return NCallableSupport.of(10, () -> {
                            TsonElementList definitionArguments = object.args();
                            TsonElementList definitionBody = object.body();
                            HNode node = new DefaultHNode(HNodeType.DEFINE);
                            node.setProperty(HPropName.NAME, Tson.of(name));
                            node.setProperty(HPropName.ARGS, definitionArguments == null ? null : Tson.ofArray(definitionArguments.toList().toArray(new TsonElement[0])).build());
                            for (TsonElement element : definitionBody) {
                                NOptional<HItem> o = context.engine().newNode(element, context);
                                if (!o.isPresent()) {
                                    NMsg nMsg = NMsg.ofC("[%s] unable to resolve node : %s", net.thevpc.halfa.api.util.HUtils.shortName(context.source()), c);
                                    context.messages().addError(nMsg);
                                    throw new NIllegalArgumentException(nMsg);
                                }
                                HItem hItem = o.get();
                                if (hItem instanceof HItemList) {
                                    for (HItem hItem0 : ((HItemList) hItem).getItems()) {
                                        node.children().add((HNode) hItem0);
                                    }
                                } else {
                                    node.children().add((HNode) hItem);
                                }
                            }
                            return node;
                        });
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
