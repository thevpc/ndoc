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
import net.thevpc.nuts.elem.NObjectElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.Collections;
import java.util.List;

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
                NPairElement p = c.asPair().get();
                NElement k = p.key();
                NElement v = p.value();
                if (v.isNamedObject() || v.isNamedUplet()) {
                    NObjectElement object = v.toObject().get();
                    String name = object.name().orNull();
                    if (!NBlankable.isBlank(name)) {
                        return NCallableSupport.of(10, () -> {
                            List<NElement> definitionArguments = object.params().orElse(Collections.emptyList());
                            List<NElement> definitionBody = object.children();
                            HNode node = new DefaultHNode(HNodeType.DEFINE);
                            node.setProperty(HPropName.NAME, NElement.ofString(name));
                            node.setProperty(HPropName.ARGS, definitionArguments == null ? null : NElement.ofArray(definitionArguments.toArray(new NElement[0])));
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

        return NElement.ofPair("$" + varName, HUtils.toElement(varValue));
    }

}
