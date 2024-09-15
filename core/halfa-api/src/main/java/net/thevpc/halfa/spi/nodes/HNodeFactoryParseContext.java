package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.tson.TsonElement;

public interface HNodeFactoryParseContext {

    HMessageList messages();

    NSession session();

    HEngine engine();

    HNodeFactoryParseContext push(HNode node);

    HNode node();

    HDocument document();

    HNode[] nodePath();

    TsonElement element();

    HResource source();

    NPath resolvePath(String path);

    NPath resolvePath(NPath path);

    HDocumentFactory documentFactory();

    TsonElement asPathRef(TsonElement element);

}
