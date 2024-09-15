package net.thevpc.halfa.elem.base.text.source;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HSourceParser extends HNodeParserBase {

    public HSourceParser() {
        super(false, HNodeType.SOURCE);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case STRING: {
                node.setProperty(HPropName.VALUE, currentArg);
                return true;
            }
            case NAME: {
                node.setProperty(HPropName.LANG, currentArg);
                return true;
            }
            case PAIR: {
                if(currentArg.isSimplePair()){
                    TsonPair p = currentArg.toPair();
                    switch (HUtils.uid(p.key().stringValue())){
                        case "value":
                        case "code":
                        case "content": {
                            node.setProperty(HPropName.VALUE, p.value());
                            return true;
                        }
                        case "file": {
                            String path = p.value().toStr().stringValue().trim();
                            NPath nPath = context.resolvePath(path);
                            context.document().resources().add(nPath);
                            try {
                                node.setProperty(HPropName.VALUE, Tson.of(nPath.readString().trim()));
                            } catch (Exception ex) {
                                context.messages().addError(NMsg.ofC("unable to load source file %s as %s", path, nPath));
                            }
                            return true;
                        }
                        case "lang": {
                            node.setProperty(HPropName.LANG, p.value());
                            return true;
                        }
                    }
                }
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(currentArg).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {

                    }
                }
                break;
            }
        }
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
    }

}
