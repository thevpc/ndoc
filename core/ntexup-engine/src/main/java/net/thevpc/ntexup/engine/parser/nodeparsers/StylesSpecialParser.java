package net.thevpc.ntexup.engine.parser.nodeparsers;

import net.thevpc.ntexup.engine.parser.NDocNodeParserBase;
import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.node.NTxItemList;
import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.style.NTxStyleRule;
import net.thevpc.ntexup.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.ntexup.engine.parser.HStyleParser;
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
    public NCallableSupport<NTxItem> parseNode(NDocNodeFactoryParseContext context) {
        List<NTxItem> styles = new ArrayList<>();
        NElement tsonElement = context.element();
        NTxDocumentFactory f = context.documentFactory();
        switch (tsonElement.type()) {
            case NAMED_PARAMETRIZED_OBJECT:
            case NAMED_OBJECT: {
                NObjectElement obj = tsonElement.toObject().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.ofValid(() -> {
                        for (NElement yy : obj.children()) {
                            NOptional<NTxStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                            if (!u.isPresent()) {
                                HStyleParser.parseStyleRule(yy, f, context).get();
                                _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(yy), u.getMessage().get()), context);
                            } else {
                                for (NTxStyleRule r : u.get()) {
                                    styles.add(r);
                                }
                            }
                        }
                        return new NTxItemList().addAll(styles);
                    });
                }
                break;
            }
            case NAMED_PARAMETRIZED_ARRAY:
            case NAMED_ARRAY: {
                NArrayElement obj = tsonElement.toArray().get();
                if (obj.isNamed(id())) {
                    return NCallableSupport.ofValid(() -> {
                        for (NElement yy : obj.children()) {
                            NOptional<NTxStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                            if (!u.isPresent()) {
                                _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(yy), u.getMessage().get()), context);
                            } else {
                                for (NTxStyleRule r : u.get()) {
                                    styles.add(r);
                                }
                            }
                        }
                        return new NTxItemList().addAll(styles);
                    });
                }
                break;
            }
            case PAIR: {
                NPairElement obj = tsonElement.toNamedPair().get();
                if (obj.isNamedPair() && NNameFormat.equalsIgnoreFormat(obj.key().asStringValue().get(), id())) {
                    return NCallableSupport.ofValid(() -> {
                        if (obj.value().isAnyObject()) {
                            for (NElement yy : obj.value().asObject().get().children()) {
                                NOptional<NTxStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                                if (!u.isPresent()) {
                                    _logError(NMsg.ofC("[%s] invalid style rule  %s :: %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(yy), u.getMessage().get()), context);
                                } else {
                                    for (NTxStyleRule r : u.get()) {
                                        styles.add(r);
                                    }
                                }
                            }
                            return new NTxItemList().addAll(styles);
                        }
                        _logError(NMsg.ofC("[%s] invalid style rules, expected styles: {...} , got %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(tsonElement)), context);
                        return new NTxItemList();
                    });
                }
                break;
            }
        }
        return NCallableSupport.ofInvalid(NMsg.ofC("missing style construct from %s", NDocUtils.snippet(tsonElement)).asError());
    }

}
