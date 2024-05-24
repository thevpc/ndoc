package net.thevpc.halfa.engine;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class DefaultHNodeFactoryParseContext implements HNodeFactoryParseContext {
    private final TsonElement element;
    private final HEngine engine;
    private final NSession session;
    private final List<HNode> parents = new ArrayList<>();
    private final Set<HNodeType> expectedTypes = new HashSet<>();
    private final Object source;

    public DefaultHNodeFactoryParseContext(TsonElement element, HEngine engine, NSession session
            , List<HNode> parents
            , Set<HNodeType> expectedTypes,
                                           Object source
    ) {
        this.element = element;
        this.engine = engine;
        this.session = session;
        this.source = source;
        this.parents.addAll(parents);
        this.expectedTypes.addAll(expectedTypes);
    }

    @Override
    public HDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    public Object source() {
        return source;
    }

    @Override
    public Set<HNodeType> expectedTypes() {
        return expectedTypes;
    }

    @Override
    public NSession session() {
        return session;
    }

    @Override
    public HNode[] parents() {
        return parents.toArray(new HNode[0]);
    }

    @Override
    public HEngine engine() {
        return engine;
    }

    @Override
    public TsonElement element() {
        return element;
    }

    @Override
    public NPath resolvePath(String path) {
        if (NBlankable.isBlank(path)) {
            return null;
        }
        Object src = source;
        if (src == null) {
            if (parents != null) {
                for (int i = parents.size() - 1; i >= 0; i--) {
                    if (parents.get(i) != null) {
                        Object ss = parents.get(i).getSource();
                        if (ss != null) {
                            src = ss;
                            break;
                        }
                    }
                }
            }
        }
        NPath base;
        if (src instanceof NPath) {
            NPath sp = (NPath) src;
            if (sp.isRegularFile()) {
                sp = sp.getParent();
            }
            if (sp != null) {
                base = sp.resolve(path);
            } else {
                base = NPath.of(path, session);
            }
        } else {
            base = NPath.of(path, session);
        }
        return base;
    }
}
