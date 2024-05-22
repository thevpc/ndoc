package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.DefaultHNodeSelector;
import net.thevpc.halfa.api.style.HStyleRuleSelector;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HStyleRuleSelectorParser {
    private HStyleRuleSelector parseStyleRuleSelector(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        List<TsonElement> args = e.toFunction().getAll();
        Set<String> names = new HashSet<>();
        Set<String> classes = new HashSet<>();
        Set<HNodeType> types = new HashSet<>();
        for (TsonElement arg : args) {
            switch (arg.type()) {
                case PAIR: {
                    TsonPair p = arg.toPair();
                    NOptional<String> k = new TsonElementParseHelper(p.getKey()).asStringOrName();
                    TsonElementParseHelper v = new TsonElementParseHelper(p.getValue());
                    switch (HParseHelper.uid(k.get())) {
                        case "name": {
                            names.addAll(Arrays.asList(v.asStringOrNameArray().get()));
                            break;
                        }
                        case "class": {
                            classes.addAll(Arrays.asList(v.asStringOrNameArray().get()));
                            break;
                        }
                        case "type": {
                            types.addAll(Arrays.asList(v.asStringOrNameArray().get()).stream().map(x -> HNodeTypeEnumParser.parse(
                                    Tson.string(x), f, context
                            )).collect(Collectors.toSet()));
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("unexpected : " + k.get());
                        }
                    }
                    break;
                }
                case STRING: {
                    String s = e.toStr().getString();
                    if (s.startsWith(".")) {
                        classes.add(s);
                    } else {
                        names.add(s);
                    }
                    break;
                }
                case NAME: {
                    String s = e.toStr().getString();
                    if (s.startsWith(".")) {
                        classes.add(s);
                    } else {
                        types.add(HNodeTypeEnumParser.parse(e, f, context));
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("invalid HStyleRuleSelector");
                }
            }
        }
        return new DefaultHNodeSelector(
                names.toArray(new String[0]),
                types.toArray(new HNodeType[0]),
                classes.toArray(new String[0]),
                false
        );
    }
}
