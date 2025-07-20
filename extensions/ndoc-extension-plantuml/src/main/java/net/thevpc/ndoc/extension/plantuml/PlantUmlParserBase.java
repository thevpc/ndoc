package net.thevpc.ndoc.extension.plantuml;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.model.node.NDocItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;


public abstract class PlantUmlParserBase extends NDocNodeParserBase {
    public PlantUmlParserBase(String id, String mode) {
        super(false, id, mode);
        setNodeId("plantuml");
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
    public void onStartParsingItem(String id, NDocNode node, NElement tsonElement, NDocNodeFactoryParseContext context) {
        super.onStartParsingItem(id, node, tsonElement, context);
        node.setProperty(NDocPropName.MODE, NElement.ofString(id));
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
                            String path = p.value().asStringValue().get().trim();
                            NPath nPath = info.context.resolvePath(path);
                            info.context.document().resources().add(nPath);
                            try {
                                info.node.setProperty(NDocPropName.VALUE, NElement.ofString(nPath.readString().trim()));
                            } catch (Exception ex) {
                                info.context.messages().log(
                                        NDocMsg.of(NMsg.ofC("unable to load source file %s as %s", path, nPath).asSevere())
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
                NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    NDocObjEx.SimplePair spp = sp.get();
                    NDocObjEx v = spp.getValue();
                    switch (spp.getNameId()) {

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
