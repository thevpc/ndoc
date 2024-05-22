package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class DefaultHNodeFactoryParseContext implements HNodeFactoryParseContext {
    private final TsonElement element;
    private final HEngine engine;
    private final NSession session;
    private final List<HNode> parents = new ArrayList<>();
    private final Set<HNodeType> expectedTypes = new HashSet<>();

    public DefaultHNodeFactoryParseContext(TsonElement element, HEngine engine, NSession session
            , List<HNode> parents
            , Set<HNodeType> expectedTypes

    ) {
        this.element = element;
        this.engine = engine;
        this.session = session;
        this.parents.addAll(parents);
        this.expectedTypes.addAll(expectedTypes);
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

}
