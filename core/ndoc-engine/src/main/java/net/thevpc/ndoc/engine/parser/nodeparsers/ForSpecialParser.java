package net.thevpc.ndoc.engine.parser.nodeparsers;

import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.parser.ctrlnodes.CtrlNDocNodeFor;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NObjectElement;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.util.NMsg;

import java.util.ArrayList;
import java.util.List;

public class ForSpecialParser extends NDocNodeParserBase {
    public ForSpecialParser() {
        super(true, NDocNodeType.CTRL_FOR);
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
                                _logError(NMsg.ofC("unexpected %s", NDocUtils.snippet(tsonElement)),context);
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
                        _logError(NMsg.ofC("missing for condition from %s", NDocUtils.snippet(tsonElement)), context);
                        return new NDocItemList();
                    });
                }
                break;
            }
            case NAMED_UPLET: {
                NUpletElement obj = tsonElement.asUplet().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.valid( () -> {
                        _logError(NMsg.ofC("missing for body from %s", NDocUtils.snippet(tsonElement)), context);
                        return new NDocItemList();
                    });
                }
                break;
            }
        }
        return NCallableSupport.invalid(NMsg.ofC("missing for construct from %s", NDocUtils.snippet(tsonElement)).asError());
    }




}

