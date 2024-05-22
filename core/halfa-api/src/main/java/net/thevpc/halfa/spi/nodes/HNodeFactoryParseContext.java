package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.tson.TsonElement;

import java.util.Set;

public interface HNodeFactoryParseContext {

    NSession session();

    HEngine engine();
    
    HNode[] parents();

    TsonElement element();
    Object source();

    Set<HNodeType> expectedTypes();

    NPath resolvePath(String path);

}
