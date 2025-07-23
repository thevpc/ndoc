package net.thevpc.ndoc.engine.parser.special;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeFor;
import net.thevpc.ndoc.engine.parser.nodes.AbstractNDocItemNamedObjectParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NObjectElement;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.ArrayList;
import java.util.List;

public class ForSpecialParser extends AbstractNDocItemNamedObjectParser {
    public ForSpecialParser() {
        super("for");
    }

    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement tsonElement = context.element();
        switch (tsonElement.type()) {
            case NAMED_PARAMETRIZED_OBJECT: {
                NObjectElement obj = tsonElement.asObject().get();
                if(obj.isNamed(id())) {
                    return NCallableSupport.valid(()-> {
                        NElement __varName = null;
                        NElement __varDomain = null;
                        List<NElement> block = new ArrayList<>();
                        for (NElement child : obj.children()) {
                            block.add(child);
                        }
                        for (NElement e : obj.params().get()) {
                            if (e.isPair()) {
                                __varName = e.asPair().get().key();
                                __varDomain = e.asPair().get().value();
                            } else {
                                _logError(NMsg.ofC("unexpected %s", tsonElement),context);
                            }
                        }
                        CtrlNDocNodeFor cc = new CtrlNDocNodeFor(context.source(), __varName, __varDomain, block);
                        cc.setParent(context.node());
                        return cc;
                    });
                }
                break;
            }
            case NAMED_OBJECT: {
                NObjectElement obj = tsonElement.asObject().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.valid( () -> {
                        _logError(NMsg.ofC("missing for condition from %s", tsonElement), context);
                        return new NDocItemList();
                    });
                }
                break;
            }
            case NAMED_UPLET: {
                NUpletElement obj = tsonElement.asUplet().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.valid( () -> {
                        _logError(NMsg.ofC("missing for body from %s", tsonElement), context);
                        return new NDocItemList();
                    });
                }
                break;
            }
        }
        return _invalidSupport(NMsg.ofC("missing for construct from %s", tsonElement), context);
    }

//    @Override
//    public NOptional<NDocItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
//        switch (tsonElement.type()) {
//            case NAMED_PARAMETRIZED_OBJECT: {
//                NObjectElement obj = tsonElement.asObject().get();
//                NElement __varName = null;
//                NElement __varDomain = null;
//                List<NElement> block = new ArrayList<>();
//                for (NElement child : obj.children()) {
//                    block.add(child);
//                }
//                for (NElement e : obj.params().get()) {
//                    if (e.isPair()) {
//                        __varName = e.asPair().get().key();
//                        __varDomain = e.asPair().get().value();
//                    } else {
//                        throw new NIllegalArgumentException(NMsg.ofC("unexpected %s", tsonElement));
//                    }
//                }
//                CtrlNDocNodeFor cc = new CtrlNDocNodeFor(context.source(), __varName, __varDomain, block);
//                cc.setParent(context.node());
//                return NOptional.of(cc);
//            }
//            case NAMED_OBJECT: {
//                context.messages().log(NDocMsg.of(NMsg.ofC("missing for condition from %s", tsonElement).asSevere(), context.source()));
//                return NOptional.ofNamedEmpty("for condition");
//            }
//            case NAMED_UPLET: {
//                context.messages().log(NDocMsg.of(NMsg.ofC("missing for body from %s", tsonElement).asSevere(), context.source()));
//                return NOptional.ofNamedEmpty("for body");
//            }
//        }
//        context.messages().log(NDocMsg.of(NMsg.ofC("missing for construct from %s", tsonElement).asSevere(), context.source()));
//        return NOptional.ofNamedEmpty("for construct");
//    }

    @Override
    public NElement toElem(NDocNode item) {
        throw new IllegalStateException("not implemented toElem "+id());
    }

    @Override
    public NDocNode newNode() {
        throw new IllegalStateException("not implemented newNode "+id());
    }

}

