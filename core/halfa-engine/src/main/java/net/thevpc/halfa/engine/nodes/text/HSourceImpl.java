package net.thevpc.halfa.engine.nodes.text;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HSourceImpl extends AbstractHNodeTypeFactory {

    public HSourceImpl() {
        super(false, HNodeType.SOURCE);
    }

    @Override
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                node.setProperty(HPropName.VALUE, e);
                return true;
            }
            case NAME: {
                node.setProperty(HPropName.LANG, e);
                return true;
            }
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "value":
                        case "code":
                        case "content": {
                            node.setProperty(HPropName.VALUE, v.raw());
                            return true;
                        }
                        case "file": {
                            NPath nPath = context.resolvePath(v.asStringOrName().get().trim());
                            context.document().resources().add(nPath);
                            try {
                                node.setProperty(HPropName.VALUE, nPath.readString().trim());
                            } catch (Exception ex) {
                                context.messages().addError(NMsg.ofC("unable to load source file %s as %s", v.asStringOrName().get().trim(), nPath));
                            }
                            return true;
                        }
                        case "lang": {
                            node.setProperty(HPropName.LANG, v.raw());
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArg(id, node, e, f, context);
    }

}
