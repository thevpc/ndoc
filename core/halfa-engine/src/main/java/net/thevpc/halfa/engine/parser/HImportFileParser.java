package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.node.container.HStackContainer;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class HImportFileParser {

    public static NOptional<HItem> parseImport(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case FUNCTION: {
                List<TsonElement> u = e.toFunction().all();
                if (u.size() == 0) {
                    return NOptional.ofError(s -> NMsg.ofC("missing path argument : %s", e));
                }
                HNode[] parents = context.parents();
                HNode putInto = parents.length == 0 ? null : parents[parents.length - 1];
                List<HItem> loaded = new ArrayList<>();
                boolean someLoaded = false;
                for (TsonElement ee : u) {
                    TsonElementParseHelper t = new TsonElementParseHelper(ee);
                    NOptional<String[]> p = t.asStringOrNameArray();
                    if (p.isPresent()) {
                        someLoaded = true;
                        for (String sp : p.get()) {
                            HashSet<HNodeType> expected = new HashSet<>(Arrays.asList(HNodeType.values()));
                            if (putInto != null) {
                                switch (putInto.type()) {
                                    case PAGE_GROUP: {
                                        break;
                                    }
                                    case PAGE: {
                                        expected.remove(HNodeType.PAGE_GROUP);
                                        break;
                                    }
                                    default: {
                                        expected.remove(HNodeType.PAGE);
                                        expected.remove(HNodeType.PAGE_GROUP);
                                        break;
                                    }
                                }
                            }
                            List<NPath> list = context.resolvePath(sp).walkGlob().toList();
                            //list.sort();
                            for (NPath nPath : list) {
                                if (nPath.isRegularFile()) {
                                    NOptional<HItem> se = context.engine().loadNode(putInto, expected, nPath);
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
