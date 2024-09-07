package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.engine.parser.util.GitHelper;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NExecCmd;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIO;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NRef;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class ImportHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
    public ImportHITemNamedObjectParser() {
        super("import", "include");
    }

    @Override
    public boolean accept(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        return true;
    }

    @Override
    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case FUNCTION: {
                List<TsonElement> u = tsonElement.toFunction().all();
                if (u.isEmpty()) {
                    context.messages().addError(NMsg.ofC("missing path argument : %s", tsonElement), context.source());
                    return NOptional.ofError(s -> NMsg.ofC("missing path argument : %s", tsonElement));
                }
                HNode putInto = context.node();
                List<HItem> loaded = new ArrayList<>();
                NRef<Boolean> someLoaded = NRef.of(false);
                for (TsonElement ee : u) {
                    ObjEx t = ObjEx.of(ee);
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
                break;
            }
        }
        context.messages().addError(NMsg.ofC("missing include elements from %s", tsonElement), context.source());
        return NOptional.ofNamedEmpty("include elements");
    }

    public NOptional<HItem> importOne(String anyPath, List<HItem> loaded, HNode putInto, HNodeFactoryParseContext context) {
        NPath spp = context.resolvePath(anyPath);
        context.document().resources().add(spp);
        List<NPath> list = spp.walkGlob().toList();
        list.sort(Comparator.comparing(NPath::toString));
        for (NPath nPath : list) {
            if (nPath.isRegularFile()) {
                NOptional<HItem> se = context.engine().loadNode(putInto, nPath, context.document(), context.messages());
                if (se.isPresent()) {
                    loaded.add(se.get());
                } else {
                    context.messages().addError(NMsg.ofC("invalid include. error loading : %s", nPath), context.source());
                    return NOptional.ofError(s -> NMsg.ofC("invalid include. error loading : %s", nPath));
                }
            }
        }
        return null;
    }
}

