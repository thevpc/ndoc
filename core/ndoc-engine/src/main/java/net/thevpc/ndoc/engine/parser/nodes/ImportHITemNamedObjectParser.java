package net.thevpc.ndoc.engine.parser.nodes;

import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.node.HItemList;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.engine.HEngineUtils;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NRef;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonUplet;

import java.util.*;

public class ImportHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
    public ImportHITemNamedObjectParser() {
        super("import", "include");
    }

    @Override
    public boolean accept(String id, TsonElement tsonElement, NDocNodeFactoryParseContext context) {
        return true;
    }

    @Override
    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, NDocNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case UPLET:
            case NAMED_UPLET:
            {
                TsonUplet uplet = tsonElement.toUplet();
                if(uplet.isNamed()) {
                    List<TsonElement> u = uplet.params().toList();
                    if (u.isEmpty()) {
                        context.messages().log(HMsg.of(NMsg.ofC("missing path argument : %s", tsonElement).asSevere(), context.source()));
                        return NOptional.ofError(() -> NMsg.ofC("missing path argument : %s", tsonElement));
                    }
                    HNode putInto = context.node();
                    List<HItem> loaded = new ArrayList<>();
                    NRef<Boolean> someLoaded = NRef.of(false);
                    for (TsonElement ee : u) {
                        NDocObjEx t = NDocObjEx.of(ee);
                        NOptional<String[]> p = t.asStringArrayOrString();
                        if (p.isPresent()) {
                            for (String sp : p.get()) {
                                NOptional<HItem> a = importOne(sp, loaded, putInto, context);
                                if (a != null) {
                                    return a;
                                } else {
                                    someLoaded.set(true);
                                }
                            }
                        }
                    }
                    if (someLoaded.get()) {
                        return NOptional.of(new HItemList().addAll(loaded));
                    }
                }
                break;
            }
        }
        context.messages().log(HMsg.of(NMsg.ofC("missing include elements from %s", tsonElement).asSevere(), context.source()));
        return NOptional.ofNamedEmpty("include elements");
    }

    public NOptional<HItem> importOne(String anyPath, List<HItem> loaded, HNode putInto, NDocNodeFactoryParseContext context) {
        NPath spp = context.resolvePath(anyPath);
        if (spp.isDirectory()) {
            spp = spp.resolve(HEngineUtils.HALFA_EXT_STAR_STAR);
        }
        context.document().resources().add(spp);
        List<NPath> list = spp.walkGlob().toList();
        list.sort(HEngineUtils::comparePaths);
        for (NPath nPath : list) {
            if (nPath.isRegularFile()) {
                NOptional<HItem> se = context.engine().loadNode(putInto, nPath, context.document(), context.messages());
                if (se.isPresent()) {
                    loaded.add(se.get());
                } else {
                    context.messages().log(HMsg.of(NMsg.ofC("invalid include. error loading : %s", nPath).asSevere(), context.source()));
                    return NOptional.ofError(() -> NMsg.ofC("invalid include. error loading : %s", nPath));
                }
            }
        }
        return NOptional.of(new HItemList().addAll(loaded));
    }
}

