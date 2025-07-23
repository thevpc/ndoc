package net.thevpc.ndoc.engine.parser.special;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.engine.NDocEngineUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.engine.parser.nodes.AbstractNDocItemNamedObjectParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NRef;
import net.thevpc.nuts.elem.NElement;


import java.util.*;

public class ImportSpecialParser extends AbstractNDocItemNamedObjectParser {
    public ImportSpecialParser() {
        super("import", "include");
    }


    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement tsonElement = context.element();
        switch (tsonElement.type()) {
            case NAMED_UPLET: {
                NUpletElement uplet = tsonElement.asUplet().get();
                if (uplet.isNamed("import") || uplet.isNamed("include")) {
                    return NCallableSupport.valid( () -> {
                        List<NElement> u = uplet.params();
                        if (u == null || u.isEmpty()) {
                            context.messages().log(NDocMsg.of(NMsg.ofC("missing path argument : %s", tsonElement).asSevere(),context.source()));
                            return new NDocItemList();
                        }
                        NDocNode putInto = context.node();
                        List<NDocItem> loaded = new ArrayList<>();
                        NRef<Boolean> someLoaded = NRef.of(false);
                        for (NElement ee : u) {
                            NDocObjEx t = NDocObjEx.of(ee);
                            NOptional<String[]> p = t.asStringArrayOrString();
                            if (p.isPresent()) {
                                for (String sp : p.get()) {
                                    NDocItem a = importOne(sp, loaded, putInto, context).orNull();
                                    if (a != null) {
                                        return a;
                                    } else {
                                        someLoaded.set(true);
                                    }
                                }
                            }
                        }
                        NDocItem ret;
                        if (someLoaded.get()) {
                            ret = new NDocItemList().addAll(loaded);
                        } else {
                            context.messages().log(NDocMsg.of(NMsg.ofC("missing include elements from %s", tsonElement).asSevere(), context.source()));
                            ret = new NDocItemList();
                        }
                        return ret;
                    });
                }
                break;
            }
        }
        return _invalidSupport(NMsg.ofC("include elements"), context);
    }

//    @Override
//    public NOptional<NDocItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
//        switch (tsonElement.type()) {
//            case UPLET:
//            case NAMED_UPLET: {
//                NUpletElement uplet = tsonElement.asUplet().get();
//                if (uplet.isNamed()) {
//                    List<NElement> u = uplet.params();
//                    if (u == null || u.isEmpty()) {
//                        context.messages().log(NDocMsg.of(NMsg.ofC("missing path argument : %s", tsonElement).asSevere(), context.source()));
//                        return NOptional.ofError(() -> NMsg.ofC("missing path argument : %s", tsonElement));
//                    }
//                    NDocNode putInto = context.node();
//                    List<NDocItem> loaded = new ArrayList<>();
//                    NRef<Boolean> someLoaded = NRef.of(false);
//                    for (NElement ee : u) {
//                        NDocObjEx t = NDocObjEx.of(ee);
//                        NOptional<String[]> p = t.asStringArrayOrString();
//                        if (p.isPresent()) {
//                            for (String sp : p.get()) {
//                                NOptional<NDocItem> a = importOne(sp, loaded, putInto, context);
//                                if (a != null) {
//                                    return a;
//                                } else {
//                                    someLoaded.set(true);
//                                }
//                            }
//                        }
//                    }
//                    if (someLoaded.get()) {
//                        return NOptional.of(new NDocItemList().addAll(loaded));
//                    }
//                }
//                break;
//            }
//        }
//        context.messages().log(NDocMsg.of(NMsg.ofC("missing include elements from %s", tsonElement).asSevere(), context.source()));
//        return NOptional.ofNamedEmpty("include elements");
//    }

    public NOptional<NDocItem> importOne(String anyPath, List<NDocItem> loaded, NDocNode putInto, NDocNodeFactoryParseContext context) {
        NPath spp = context.resolvePath(anyPath);
        if (spp.isDirectory()) {
            spp = spp.resolve(NDocEngineUtils.NDOC_EXT_STAR_STAR);
        }
        context.document().resources().add(spp);
        List<NPath> list = spp.walkGlob().toList();
        list.sort(NDocEngineUtils::comparePaths);
        for (NPath nPath : list) {
            if (nPath.isRegularFile()) {
                NOptional<NDocItem> se = context.engine().loadNode(putInto, nPath, context.document());
                if (se.isPresent()) {
                    loaded.add(se.get());
                } else {
                    context.messages().log(NDocMsg.of(NMsg.ofC("invalid include. error loading : %s", nPath).asSevere(), context.source()));
                    return NOptional.ofError(() -> NMsg.ofC("invalid include. error loading : %s", nPath));
                }
            }
        }
        return NOptional.of(new NDocItemList().addAll(loaded));
    }

    @Override
    public NElement toElem(NDocNode item) {
        throw new IllegalStateException("not implemented toElem "+id());
    }

    @Override
    public NDocNode newNode() {
        throw new IllegalStateException("not implemented newNode "+id());
    }
}

