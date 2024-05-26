package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.tson.TsonElement;

public interface HNodeFactoryParseContext {

    NSession session();

    HEngine engine();

    HNodeFactoryParseContext push(HNode node);

    HNode node();

    HNode[] nodePath();

    TsonElement element();

    Object source();


    NPath resolvePath(String path);

    NPath resolvePath(NPath path);

    HDocumentFactory documentFactory();

}
