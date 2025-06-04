package net.thevpc.ndoc.extension.plantuml;

import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
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
    public NOptional<HItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
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
                        context.documentFactory().ofText(tsonElement.asStringValue().get())
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
    public void onStartParsingItem(String id, HNode node, NElement tsonElement, NDocNodeFactoryParseContext context) {
        super.onStartParsingItem(id, node, tsonElement, context);
        node.setProperty(HPropName.MODE, NElement.ofString(id));
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
                info.node.setProperty(HPropName.VALUE, info.currentArg);
                return true;
            }
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    NPairElement p = info.currentArg.asPair().get();
                    String sid = HUtils.uid(p.key().asStringValue().get());
                    switch (sid) {
                        case HPropName.VALUE:
                        case "content": {
                            info.node.setProperty(HPropName.VALUE, p.value());
                            return true;
                        }
                        case HPropName.FILE: {
                            String path = p.value().asStringValue().get().trim();
                            NPath nPath = info.context.resolvePath(path);
                            info.context.document().resources().add(nPath);
                            try {
                                info.node.setProperty(HPropName.VALUE, NElement.ofString(nPath.readString().trim()));
                            } catch (Exception ex) {
                                info.context.messages().log(
                                        HMsg.of(NMsg.ofC("unable to load source file %s as %s", path, nPath).asSevere())
                                );
                            }
                            return true;
                        }
                        case HPropName.MODE: {
                            info.node.setProperty(HPropName.MODE, p.value());
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
    public NElement toElem(HNode item) {
        return ToElementHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
