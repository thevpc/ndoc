package net.thevpc.ndoc.extension.plantuml;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.base.parser.HParserUtils;
import net.thevpc.ndoc.api.base.format.ToElementHelper;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;


public class PlantUmlParser extends NDocNodeParserBase {
    public PlantUmlParser() {
        super(false, "plantuml", "diagram");
    }

    @Override
    public NOptional<NDocItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                return NOptional.of(
                        context.documentFactory().ofText(tsonElement.asStringValue().get()).setSource(context.source())
                );
            }
        }
        return super.parseItem(id, tsonElement, context);
    }

    @Override
    protected String acceptTypeName(NElement e) {
        switch (e.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                return id();
            }
        }
        return super.acceptTypeName(e);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                info.node.setProperty(NDocPropName.VALUE, info.currentArg);
                return true;
            }
            case NAME: {
                if (!HParserUtils.isCommonStyleProperty(info.currentArg.asStringValue().get())) {
                    info.node.setProperty(NDocPropName.MODE, info.currentArg);
                    return true;
                }
                break;
            }
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    NPairElement p = info.currentArg.asPair().get();
                    String sid = NDocUtils.uid(p.key().asStringValue().get());
                    switch (sid) {
                        case NDocPropName.VALUE:
                        case "content": {
                            info.node.setProperty(NDocPropName.VALUE, p.value());
                            return true;
                        }
                        case NDocPropName.FILE: {
                            NPath nPath = engine().resolvePath(p.value().asString().get(),info.node);
                            info.context.document().resources().add(nPath);
                            try {
                                info.node.setProperty(NDocPropName.VALUE, NElement.ofString(nPath.readString().trim()));
                            } catch (Exception ex) {
                                info.context.messages().log(
                                        NMsg.ofC("unable to load source file %s as %s", NDocUtils.snippet(p.value()), nPath).asSevere()
                                );
                            }
                            return true;
                        }
                        case NDocPropName.MODE: {
                            info.node.setProperty(NDocPropName.MODE, p.value());
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
    public NElement toElem(NDocNode item) {
        return ToElementHelper
                .of(item, engine())
                .inlineStringProp(NDocPropName.VALUE)
                .build();
    }
}
