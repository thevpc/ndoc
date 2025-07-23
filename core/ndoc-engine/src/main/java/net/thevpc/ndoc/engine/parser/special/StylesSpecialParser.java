package net.thevpc.ndoc.engine.parser.special;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.base.parser.HStyleParser;
import net.thevpc.ndoc.engine.parser.nodes.AbstractNDocItemNamedObjectParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NArrayElement;
import net.thevpc.nuts.elem.NObjectElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class StylesSpecialParser extends AbstractNDocItemNamedObjectParser {
    public StylesSpecialParser() {
        super("styles");
    }

    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        List<NDocItem> styles = new ArrayList<>();
        NElement tsonElement = context.element();
        NDocDocumentFactory f = context.documentFactory();
        switch (tsonElement.type()) {
            case NAMED_PARAMETRIZED_OBJECT:
            case NAMED_OBJECT: {
                NObjectElement obj = tsonElement.toObject().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.valid( () -> {
                        for (NElement yy : obj.children()) {
                            NOptional<NDocStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                            if (!u.isPresent()) {
                                HStyleParser.parseStyleRule(yy, f, context).get();
                                _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), NDocUtils.shortString(yy), u.getMessage().get()), context);
                            } else {
                                for (NDocStyleRule r : u.get()) {
                                    styles.add(r);
                                }
                            }
                        }
                        return new NDocItemList().addAll(styles);
                    });
                }
                break;
            }
            case NAMED_PARAMETRIZED_ARRAY:
            case NAMED_ARRAY: {
                NArrayElement obj = tsonElement.toArray().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.valid( () -> {
                        for (NElement yy : obj.children()) {
                            NOptional<NDocStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                            if (!u.isPresent()) {
                                _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), yy, u.getMessage().get()), context);
                            } else {
                                for (NDocStyleRule r : u.get()) {
                                    styles.add(r);
                                }
                            }
                        }
                        return new NDocItemList().addAll(styles);
                    });
                }
                break;
            }
        }
        return _invalidSupport(NMsg.ofC("[%s] invalid style rule  %s ", NDocUtils.shortName(context.source()), tsonElement), context);
    }

//    @Override
//    public NOptional<NDocItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
//        List<NDocItem> styles = new ArrayList<>();
//        NDocDocumentFactory f = context.documentFactory();
//        switch (tsonElement.type()) {
//            case OBJECT:
//            case NAMED_PARAMETRIZED_OBJECT:
//            case PARAMETRIZED_OBJECT:
//            case NAMED_OBJECT: {
//                for (NElement yy : tsonElement.toObject().get().children()) {
//                    NOptional<NDocStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
//                    if (!u.isPresent()) {
//                        HStyleParser.parseStyleRule(yy, f, context).get();
//                        context.messages().log(NDocMsg.of(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), yy, u.getMessage().get()).asSevere(), context.source()));
//                        return NOptional.ofEmpty(() -> NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), yy, u.getMessage().get()));
//                    }
//                    for (NDocStyleRule r : u.get()) {
//                        styles.add(r);
//                    }
//                }
//                break;
//            }
//            case ARRAY:
//            case NAMED_PARAMETRIZED_ARRAY:
//            case PARAMETRIZED_ARRAY:
//            case NAMED_ARRAY: {
//                for (NElement yy : tsonElement.toArray().get().children()) {
//                    NOptional<NDocStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
//                    if (!u.isPresent()) {
//                        context.messages().log(NDocMsg.of(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), yy, u.getMessage().get().asSevere(), context.source())));
//                        NOptional.ofEmpty(() -> NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), yy, u.getMessage().get()));
//                    }
//                    for (NDocStyleRule r : u.get()) {
//                        styles.add(r);
//                    }
//                }
//                break;
//            }
//            default: {
//                return NOptional.ofEmpty(() -> NMsg.ofC("[%s] invalid style rule  %s ", NDocUtils.shortName(context.source()), tsonElement));
//            }
//        }
//        return NOptional.of(new NDocItemList().addAll(styles));
//    }

    @Override
    public NElement toElem(NDocNode item) {
        throw new IllegalStateException("not implemented toElem " + id());
    }

    @Override
    public NDocNode newNode() {
        throw new IllegalStateException("not implemented newNode " + id());
    }
}
