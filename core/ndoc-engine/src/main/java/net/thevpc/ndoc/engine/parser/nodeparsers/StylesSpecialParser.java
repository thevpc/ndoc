package net.thevpc.ndoc.engine.parser.nodeparsers;

import net.thevpc.ndoc.engine.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.parser.HStyleParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NArrayElement;
import net.thevpc.nuts.elem.NObjectElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class StylesSpecialParser extends NDocNodeParserBase {
    public StylesSpecialParser() {
        super(true,"styles");
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
                    return NCallableSupport.valid(() -> {
                        for (NElement yy : obj.children()) {
                            NOptional<NDocStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                            if (!u.isPresent()) {
                                HStyleParser.parseStyleRule(yy, f, context).get();
                                _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(yy), u.getMessage().get()), context);
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
                    return NCallableSupport.valid(() -> {
                        for (NElement yy : obj.children()) {
                            NOptional<NDocStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                            if (!u.isPresent()) {
                                _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(yy), u.getMessage().get()), context);
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
            case PAIR: {
                NPairElement obj = tsonElement.toNamedPair().get();
                if (obj.isNamedPair() && NNameFormat.equalsIgnoreFormat(obj.key().asStringValue().get(), id())) {
                    return NCallableSupport.valid(() -> {
                        if (obj.value().isAnyObject()) {
                            for (NElement yy : obj.value().asObject().get().children()) {
                                NOptional<NDocStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                                if (!u.isPresent()) {
                                    _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(yy), u.getMessage().get()), context);
                                } else {
                                    for (NDocStyleRule r : u.get()) {
                                        styles.add(r);
                                    }
                                }
                            }
                            return new NDocItemList().addAll(styles);
                        }
                        _logError(NMsg.ofC("[%s] invalid style rules, expected styles: {...} , got %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(tsonElement)), context);
                        return new NDocItemList();
                    });
                }
                break;
            }
        }
        return NCallableSupport.invalid(NMsg.ofC("missing style construct from %s", NDocUtils.snippet(tsonElement)).asError());
    }

}
