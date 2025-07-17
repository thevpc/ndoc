//package net.thevpc.ndoc.engine.parser.nodes;
//
//import net.thevpc.ndoc.api.document.NDocMsg;
//import net.thevpc.ndoc.api.model.node.NDocItemList;
//import net.thevpc.ndoc.api.model.node.NDocItem;
//import net.thevpc.ndoc.api.model.node.NDocNode;
//import net.thevpc.ndoc.api.model.node.NDocNodeType;
//import net.thevpc.ndoc.spi.base.model.DefaultNDocNode;
//import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
//import net.thevpc.nuts.elem.NObjectElement;
//import net.thevpc.nuts.util.NBlankable;
//import net.thevpc.nuts.util.NMsg;
//import net.thevpc.nuts.util.NOptional;
//import net.thevpc.nuts.elem.NElement;
//
//
//import java.util.*;
//
//public class DefineHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
//    public DefineHITemNamedObjectParser() {
//        super(NDocNodeType.DEFINE);
//    }
//
//    @Override
//    public boolean accept(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
//        return true;
//    }
//
//    @Override
//    public NOptional<NDocItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
//        switch (tsonElement.type()) {
//            case NAMED_PARAMETRIZED_OBJECT: {
//                NObjectElement object = tsonElement.toObject().get();
//                String name = object.name().orNull();
//                if(NBlankable.isBlank(name)) {
//                    context.messages().log(HMsg.of(NMsg.ofC("missing definition name : %s", tsonElement).asSevere(), context.source()));
//                    return NOptional.ofError(() -> NMsg.ofC("missing definition name : %s", tsonElement));
//                }
//                List<NElement> definitionArguments = object.params().get();
//                List<NElement> definitionBody = object.children();
//                NDocNode node=new DefaultNDocNode(NDocNodeType.DEFINE);
//                node.setProperty("name", NElement.ofNameOrString(name));
//                node.setProperty("args", definitionArguments==null?null: NElement.ofArray(definitionArguments.toArray(new NElement[0])));
//                for (NElement element : definitionBody) {
//                    NOptional<NDocItem> o = context.engine().newNode(element, context);
//                    if(!o.isPresent()) {
//                        return o;
//                    }
//                    NDocItem NDocItem = o.get();
//                    if(NDocItem instanceof NDocItemList) {
//                        for (NDocItem NDocItem0 : ((NDocItemList) NDocItem).getItems()) {
//                            node.children().add((NDocNode) NDocItem0);
//                        }
//                    }else{
//                        node.children().add((NDocNode) NDocItem);
//                    }
//                }
//                return NOptional.of(node);
//            }
//        }
//        context.messages().log(NDocMsg.of(NMsg.ofC("missing declare elements from %s", tsonElement).asSevere(), context.source()));
//        return NOptional.ofNamedEmpty("declare elements");
//    }
//
//
//}
//
