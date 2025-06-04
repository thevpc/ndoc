package net.thevpc.ndoc.engine.parser.nodes;

import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.node.HItemList;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.model.DefaultHNode;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.elem.NObjectElement;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;


import java.util.*;

public class DefineHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
    public DefineHITemNamedObjectParser() {
        super(HNodeType.DEFINE);
    }

    @Override
    public boolean accept(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
        return true;
    }

    @Override
    public NOptional<HItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case NAMED_PARAMETRIZED_OBJECT: {
                NObjectElement object = tsonElement.toObject().get();
                String name = object.name().orNull();
                if(NBlankable.isBlank(name)) {
                    context.messages().log(HMsg.of(NMsg.ofC("missing definition name : %s", tsonElement).asSevere(), context.source()));
                    return NOptional.ofError(() -> NMsg.ofC("missing definition name : %s", tsonElement));
                }
                List<NElement> definitionArguments = object.params().get();
                List<NElement> definitionBody = object.children();
                HNode node=new DefaultHNode(HNodeType.DEFINE);
                node.setProperty("name", NElement.ofNameOrString(name));
                node.setProperty("args", definitionArguments==null?null: NElement.ofArray(definitionArguments.toArray(new NElement[0])));
                for (NElement element : definitionBody) {
                    NOptional<HItem> o = context.engine().newNode(element, context);
                    if(!o.isPresent()) {
                        return o;
                    }
                    HItem hItem = o.get();
                    if(hItem instanceof HItemList) {
                        for (HItem hItem0 : ((HItemList) hItem).getItems()) {
                            node.children().add((HNode) hItem0);
                        }
                    }else{
                        node.children().add((HNode) hItem);
                    }
                }
                return NOptional.of(node);
            }
        }
        context.messages().log(HMsg.of(NMsg.ofC("missing declare elements from %s", tsonElement).asSevere(), context.source()));
        return NOptional.ofNamedEmpty("declare elements");
    }


}

