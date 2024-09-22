package net.thevpc.halfa.extension.plantuml;

import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.parser.HParserUtils;
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
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case STRING: {
                info.node.setProperty(HPropName.VALUE, info.currentArg);
                return true;
            }
            case NAME: {
                if (!HParserUtils.isCommonStyleProperty(info.currentArg.toName().value())) {
                    info.node.setProperty(HPropName.MODE, info.currentArg);
                    return true;
                }
                break;
            }
            case PAIR: {
                if(info.currentArg.isSimplePair()) {
                    TsonPair p = info.currentArg.toPair();
                    String sid = HUtils.uid(p.key().stringValue());
                    switch (sid) {
                        case HPropName.VALUE:
                        case "content": {
                            info.node.setProperty(HPropName.VALUE, p.value());
                            return true;
                        }
                        case HPropName.FILE: {
                            String path = p.value().stringValue().trim();
                            NPath nPath = info.context.resolvePath(path);
                            info.context.document().resources().add(nPath);
                            try {
                                info.node.setProperty(HPropName.VALUE, Tson.of(nPath.readString().trim()));
                            } catch (Exception ex) {
                                info.context.messages().addError(NMsg.ofC("unable to load source file %s as %s", path, nPath));
                            }
                            return true;
                        }
                        case HPropName.MODE: {
                            info.node.setProperty(HPropName.MODE, p.value());
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArgument(info);
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
