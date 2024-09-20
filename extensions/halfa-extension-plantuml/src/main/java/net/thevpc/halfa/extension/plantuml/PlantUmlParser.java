package net.thevpc.halfa.extension.plantuml;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.parser.HStyleParser;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class PlantUmlParser extends HNodeParserBase {
    public PlantUmlParser() {
        super(false, "plantuml","diagram");
    }
    @Override
    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case STRING: {
                return NOptional.of(
                        context.documentFactory().ofText(tsonElement.toStr().raw())
                );
            }
        }
        return super.parseItem(id, tsonElement, context);
    }

    @Override
    protected String acceptTypeName(TsonElement e) {
        switch (e.type()) {
            case STRING: {
                return id();
            }
        }
        return super.acceptTypeName(e);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case STRING: {
                node.setProperty(HPropName.VALUE, currentArg);
                return true;
            }
            case NAME: {
                if (!HStyleParser.isCommonStyleProperty(currentArg.toName().value())) {
                    node.setProperty(HPropName.MODE, currentArg);
                    return true;
                }
                break;
            }
            case PAIR: {
                if(currentArg.isSimplePair()) {
                    TsonPair p = currentArg.toPair();
                    String sid = HUtils.uid(p.key().stringValue());
                    switch (sid) {
                        case HPropName.VALUE:
                        case "content": {
                            node.setProperty(HPropName.VALUE, p.value());
                            return true;
                        }
                        case HPropName.FILE: {
                            String path = p.value().stringValue().trim();
                            NPath nPath = context.resolvePath(path);
                            context.document().resources().add(nPath);
                            try {
                                node.setProperty(HPropName.VALUE, Tson.of(nPath.readString().trim()));
                            } catch (Exception ex) {
                                context.messages().addError(NMsg.ofC("unable to load source file %s as %s", path, nPath));
                            }
                            return true;
                        }
                        case HPropName.MODE: {
                            node.setProperty(HPropName.MODE, p.value());
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
