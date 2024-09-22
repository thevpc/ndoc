package net.thevpc.halfa.elem.base.text.source;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonPair;

public class HSourceParser extends HNodeParserBase {

    public HSourceParser() {
        super(false, HNodeType.SOURCE);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case STRING: {
                info.node.setProperty(HPropName.VALUE, info.currentArg);
                return true;
            }
            case NAME: {
                info.node.setProperty(HPropName.LANG, info.currentArg);
                return true;
            }
            case PAIR: {
                if(info.currentArg.isSimplePair()){
                    TsonPair p = info.currentArg.toPair();
                    switch (HUtils.uid(p.key().stringValue())){
                        case "value":
                        case "code":
                        case "content": {
                            info.node.setProperty(HPropName.VALUE, p.value());
                            return true;
                        }
                        case "file": {
                            String path = p.value().toStr().stringValue().trim();
                            NPath nPath = info.context.resolvePath(path);
                            info.context.document().resources().add(nPath);
                            try {
                                info.node.setProperty(HPropName.VALUE, Tson.of(nPath.readString().trim()));
                            } catch (Exception ex) {
                                info.context.messages().addError(NMsg.ofC("unable to load source file %s as %s", path, nPath));
                            }
                            return true;
                        }
                        case "lang": {
                            info.node.setProperty(HPropName.LANG, p.value());
                            return true;
                        }
                    }
                }
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {

                    }
                }
                break;
            }
        }
        return super.processArgument(info);
    }

}
