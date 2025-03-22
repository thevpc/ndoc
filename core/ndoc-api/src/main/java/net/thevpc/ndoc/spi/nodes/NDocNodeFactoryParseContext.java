package net.thevpc.ndoc.spi.nodes;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.nuts.io.NPath;
import net.thevpc.tson.TsonElement;

public interface NDocNodeFactoryParseContext {

    HLogger messages();

    NDocEngine engine();

    NDocNodeFactoryParseContext push(HNode node);

    HNode node();

    NDocument document();

    HNode[] nodePath();

    TsonElement element();

    HResource source();

    NPath resolvePath(String path);

    NPath resolvePath(NPath path);

    NDocDocumentFactory documentFactory();

    TsonElement asPathRef(TsonElement element);

}
