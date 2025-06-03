package net.thevpc.ndoc.elem.base.text.source;

import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class NDocSourceParser extends NDocNodeParserBase {

    public NDocSourceParser() {
        super(false, HNodeType.SOURCE);
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
            case NAME: {
                info.node.setProperty(HPropName.LANG, info.currentArg);
                return true;
            }
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    NPairElement p = info.currentArg.asPair().get();
                    switch (HUtils.uid(p.key().asStringValue().get())) {
                        case "value":
                        case "code":
                        case "content": {
                            info.node.setProperty(HPropName.VALUE, p.value());
                            return true;
                        }
                        case "file": {
                            String path = p.value().asStringValue().get().trim();
                            NPath nPath = info.context.resolvePath(path);
                            info.context.document().resources().add(nPath);
                            try {
                                info.node.setProperty(HPropName.VALUE, NElements.ofString(nPath.readString().trim()));
                            } catch (Exception ex) {
                                info.context.messages().log(
                                        HMsg.of(NMsg.ofC("unable to load source file %s as %s", path, nPath).asSevere())
                                );
                            }
                            return true;
                        }
                        case "lang": {
                            info.node.setProperty(HPropName.LANG, p.value());
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

}
