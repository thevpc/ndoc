package net.thevpc.ndoc.elem.base.control;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HItemList;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.model.DefaultHNode;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class NDocCtrlDefineParser extends NDocNodeParserBase {

    public NDocCtrlDefineParser() {
        super(false, HNodeType.DEFINE);
    }

    @Override
    public NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = engine.documentFactory();
        switch (c.type()) {
            case PAIR: {
                NPairElement p = c.toPair();
                NElement k = p.key();
                NElement v = p.value();
                if (v.isNamedObject() || v.isNamedUplet()) {
                    TsonObject object = v.toObject();
                    String name = object.name();
                    if (!NBlankable.isBlank(name)) {
                        return NCallableSupport.of(10, () -> {
                            List<NElement> definitionArguments = object.params();
                            List<NElement> definitionBody = object.body();
                            HNode node = new DefaultHNode(HNodeType.DEFINE);
                            node.setProperty(HPropName.NAME, NElements.of().of(name));
                            node.setProperty(HPropName.ARGS, definitionArguments == null ? null : Tson.ofArray(definitionArguments.toList().toArray(new NElement[0])).build());
                            for (NElement element : definitionBody) {
                                NOptional<HItem> o = context.engine().newNode(element, context);
                                if (!o.isPresent()) {
                                    NMsg nMsg = NMsg.ofC("[%s] unable to resolve node : %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), c)
                                            .asSevere();
                                    context.messages().log(HMsg.of(nMsg));
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
        return NCallableSupport.invalid(NMsg.ofC("[%s] unable to resolve node : %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), c));
    }

    @Override
    public NElement toElem(HNode item) {
        Object varName = "var";
        Object varValue = null;

        NOptional<NElement> s = item.getPropertyValue(HPropName.NAME);

        if (!s.isEmpty()) {
            varName = s.get();
        }

        s = item.getPropertyValue(HPropName.VALUE);
        if (!s.isEmpty()) {
            varValue = s.get();
        }

        return Tson.ofPair("$" + varName, HUtils.toElement(varValue));
    }

}
