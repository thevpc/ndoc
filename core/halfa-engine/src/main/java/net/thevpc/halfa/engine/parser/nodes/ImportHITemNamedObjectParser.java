package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.api.node.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
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
                if (u.size() == 0) {
                    return NOptional.ofError(s -> NMsg.ofC("missing path argument : %s", tsonElement));
                }
                HNode putInto = context.node();
                List<HItem> loaded = new ArrayList<>();
                boolean someLoaded = false;
                for (TsonElement ee : u) {
                    ObjEx t = new ObjEx(ee);
                    NOptional<String[]> p = t.asStringArrayOrString();
                    if (p.isPresent()) {
                        someLoaded = true;
                        for (String sp : p.get()) {
                            List<NPath> list = context.resolvePath(sp).walkGlob().toList();
                            list.sort(Comparator.comparing(NPath::toString));
                            for (NPath nPath : list) {
                                if (nPath.isRegularFile()) {
                                    NOptional<HItem> se = context.engine().loadNode(putInto, nPath);
                                    if (se.isPresent()) {
                                        loaded.add(se.get());
                                    } else {
                                        return NOptional.ofError(s -> NMsg.ofC("invalid include. error loading : %s", nPath));
                                    }
                                }
                            }
                        }
                    }
                }
                if (someLoaded) {
                    return NOptional.of(new HItemList().addAll(loaded));
                }
            }
        }
        return NOptional.ofNamedEmpty("include elements");
    }
}
