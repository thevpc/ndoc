package net.thevpc.halfa.extension.plantuml;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.parser.HStyleParser;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

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
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                node.setProperty(HPropName.VALUE, e);
                return true;
            }
            case NAME: {
                if (!HStyleParser.isCommonStyleProperty(e.toName().value())) {
                    node.setProperty(HPropName.MODE,e.toName().value());
                    return true;
                }
                break;
            }
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case HPropName.VALUE:
                        case "content": {
                            node.setProperty(HPropName.VALUE, v.raw());
                            return true;
                        }
                        case HPropName.FILE: {
                            NPath nPath = context.resolvePath(v.asStringOrName().get().trim());
                            context.document().resources().add(nPath);
                            try {
                                node.setProperty(HPropName.VALUE, nPath.readString().trim());
                            } catch (Exception ex) {
                                context.messages().addError(NMsg.ofC("unable to load source file %s as %s", v.asStringOrName().get().trim(), nPath));
                            }
                            return true;
                        }
                        case HPropName.MODE: {
                            node.setProperty(HPropName.MODE, v.raw());
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArg(id, node, e, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
