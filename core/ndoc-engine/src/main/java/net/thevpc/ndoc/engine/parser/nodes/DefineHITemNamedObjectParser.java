//package net.thevpc.halfa.engine.parser.nodes;
//
//import net.thevpc.halfa.api.model.node.HItem;
//import net.thevpc.halfa.api.model.node.HItemList;
//import net.thevpc.halfa.api.model.node.HNode;
//import net.thevpc.halfa.api.model.node.HNodeType;
//import net.thevpc.halfa.engine.HEngineUtils;
//import net.thevpc.halfa.spi.base.model.DefaultHNode;
//import net.thevpc.halfa.spi.eval.ObjEx;
//import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
//import net.thevpc.nuts.io.NPath;
//import net.thevpc.nuts.util.NBlankable;
//import net.thevpc.nuts.util.NMsg;
//import net.thevpc.nuts.util.NOptional;
//import net.thevpc.nuts.util.NRef;
//import net.thevpc.tson.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DefineHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
//    public DefineHITemNamedObjectParser() {
//        super(HNodeType.DEFINE);
//    }
//
//    @Override
//    public boolean accept(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
//        return true;
//    }
//
//    @Override
//    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
//        switch (tsonElement.type()) {
//            case OBJECT: {
//                TsonObject object = tsonElement.toObject();
//                String name = object.name();
//                if(NBlankable.isBlank(name)) {
//                    context.messages().addError(NMsg.ofC("missing definition name : %s", tsonElement), context.source());
//                    return NOptional.ofError(() -> NMsg.ofC("missing definition name : %s", tsonElement));
//                }
//                TsonElementList definitionArguments = object.args();
//                TsonElementList definitionBody = object.body();
//                HNode node=new DefaultHNode(HNodeType.DEFINE);
//                node.setProperty("name", name);
//                node.setProperty("args", definitionArguments==null?null:definitionArguments.toList().toArray(new TsonElement[0]));
//                for (TsonElement element : definitionBody) {
//                    NOptional<HItem> o = context.engine().newNode(element, context);
//                    if(!o.isPresent()) {
//                        return o;
//                    }
//                    HItem hItem = o.get();
//                    if(hItem instanceof HItemList) {
//                        for (HItem hItem0 : ((HItemList) hItem).getItems()) {
//                            node.children().add((HNode) hItem0);
//                        }
//                    }else{
//                        node.children().add((HNode) hItem);
//                    }
//                }
//                return NOptional.of(node);
//            }
//        }
//        context.messages().addError(NMsg.ofC("missing declare elements from %s", tsonElement), context.source());
//        return NOptional.ofNamedEmpty("declare elements");
//    }
//
//
//}
//
