package net.thevpc.ndoc.engine.parser.special;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeIf;
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

public class IfSpecialParser extends AbstractNDocItemNamedObjectParser {
    public IfSpecialParser() {
        super("if");
    }

    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement tsonElement = context.element();
        switch (tsonElement.type()) {
            case NAMED_PARAMETRIZED_OBJECT: {
                NObjectElement obj = tsonElement.asObject().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.valid(()->{
                        NElement __cond = null;
                        List<NDocNode> __trueBloc = new ArrayList<>();
                        List<NDocNode> __falseBloc = new ArrayList<>();
                        for (NElement child : obj.children()) {
                            __trueBloc.add((NDocNode) context.engine().newNode(child, context).get());
                        }
                        for (NElement e : obj.params().get()) {
                            if (e.isNamedPair("$condition")) {
                                if (__cond != null) {
                                    _logError(NMsg.ofC("unexpected %s", tsonElement),context);
                                    return new NDocItemList();
                                }
                                __cond = e.asPair().get().value();
                            } else if (e.isNamedPair("$else")) {
                                NElement ee = e.asPair().get().value();
                                if (ee.isAnyObject()) {
                                    for (NElement child : ee.asObject().get().children()) {
                                        __falseBloc.add((NDocNode) context.engine().newNode(child, context).get());
                                    }
                                }
                            } else {
                                if (__cond != null) {
                                    _logError(NMsg.ofC("unexpected %s", tsonElement),context);
                                }else {
                                    __cond = e;
                                }
                            }
                        }
                        CtrlNDocNodeIf cc = new CtrlNDocNodeIf(context.source(), __cond, __trueBloc, __falseBloc);
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
                        _logError(NMsg.ofC("missing if condition from %s", tsonElement), context);
                        return new NDocItemList();
                    });
                }
                break;
            }
            case NAMED_UPLET: {
                NUpletElement obj = tsonElement.asUplet().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.valid( () -> {
                        _logError(NMsg.ofC("missing if body from %s", tsonElement), context);
                        return new NDocItemList();
                    });
                }
            }
        }
        return _invalidSupport(NMsg.ofC("missing if construct from %s", tsonElement), context);
    }


//    @Override
//    public NOptional<NDocItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
//        switch (tsonElement.type()) {
//            case NAMED_PARAMETRIZED_OBJECT: {
//                NObjectElement obj = tsonElement.asObject().get();
//                if (obj.isNamed(id())) {
//                    NElement __cond = null;
//                    List<NDocNode> __trueBloc = new ArrayList<>();
//                    List<NDocNode> __falseBloc = new ArrayList<>();
//                    for (NElement child : obj.children()) {
//                        __trueBloc.add((NDocNode) context.engine().newNode(child, context).get());
//                    }
//                    for (NElement e : obj.params().get()) {
//                        if (e.isNamedPair("$condition")) {
//                            if (__cond != null) {
//                                throw new NIllegalArgumentException(NMsg.ofC("unexpected %s", tsonElement));
//                            }
//                            __cond = e.asPair().get().value();
//                        } else if (e.isNamedPair("$else")) {
//                            NElement ee = e.asPair().get().value();
//                            if (ee.isAnyObject()) {
//                                for (NElement child : ee.asObject().get().children()) {
//                                    __falseBloc.add((NDocNode) context.engine().newNode(child, context).get());
//                                }
//                            }
//                        } else {
//                            if (__cond != null) {
//                                throw new NIllegalArgumentException(NMsg.ofC("unexpected %s", tsonElement));
//                            }
//                            __cond = e;
//                        }
//                    }
//                    CtrlNDocNodeIf cc = new CtrlNDocNodeIf(context.source(), __cond, __trueBloc, __falseBloc);
//                    cc.setParent(context.node());
//                    return NOptional.of(cc);
//                }
//            }
//            case NAMED_OBJECT: {
//                context.messages().log(NDocMsg.of(NMsg.ofC("missing if condition from %s", tsonElement).asSevere(), context.source()));
//                return NOptional.ofNamedEmpty("if condition");
//            }
//            case NAMED_UPLET: {
//                context.messages().log(NDocMsg.of(NMsg.ofC("missing if body from %s", tsonElement).asSevere(), context.source()));
//                return NOptional.ofNamedEmpty("if body");
//            }
//        }
//        context.messages().log(NDocMsg.of(NMsg.ofC("missing if construct from %s", tsonElement).asSevere(), context.source()));
//        return NOptional.ofNamedEmpty("if construct");
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

