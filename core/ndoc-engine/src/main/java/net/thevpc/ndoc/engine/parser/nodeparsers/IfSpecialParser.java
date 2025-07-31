package net.thevpc.ndoc.engine.parser.nodeparsers;

import net.thevpc.ndoc.engine.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.parser.ctrlnodes.CtrlNDocNodeIf;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NObjectElement;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.util.NMsg;

import java.util.ArrayList;
import java.util.List;

public class IfSpecialParser extends NDocNodeParserBase {
    public IfSpecialParser() {
        super(true, NDocNodeType.CTRL_IF);
    }

    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement tsonElement = context.element();
        switch (tsonElement.type()) {
            case NAMED_PARAMETRIZED_OBJECT: {
                NObjectElement obj = tsonElement.asObject().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.ofValid(()->{
                        NElement __cond = null;
                        List<NDocNode> __trueBloc = new ArrayList<>();
                        List<NDocNode> __falseBloc = new ArrayList<>();
                        for (NElement child : obj.children()) {
                            __trueBloc.add((NDocNode) context.engine().newNode(child, context).get());
                        }
                        for (NElement e : obj.params().get()) {
                            if (e.isNamedPair("$condition")) {
                                if (__cond != null) {
                                    _logError(NMsg.ofC("unexpected %s", NDocUtils.snippet(tsonElement)),context);
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
                                    _logError(NMsg.ofC("unexpected %s", NDocUtils.snippet(tsonElement)),context);
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
                    return NCallableSupport.ofValid( () -> {
                        _logError(NMsg.ofC("missing if condition from %s", NDocUtils.snippet(tsonElement)), context);
                        return new NDocItemList();
                    });
                }
                break;
            }
            case NAMED_UPLET: {
                NUpletElement obj = tsonElement.asUplet().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.ofValid( () -> {
                        _logError(NMsg.ofC("missing if body from %s", NDocUtils.snippet(tsonElement)), context);
                        return new NDocItemList();
                    });
                }
            }
        }
        return NCallableSupport.ofInvalid(NMsg.ofC("missing if construct from %s", NDocUtils.snippet(tsonElement)).asError());
    }

}

