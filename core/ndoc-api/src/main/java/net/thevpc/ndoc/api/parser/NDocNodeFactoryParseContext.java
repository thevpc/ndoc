package net.thevpc.ndoc.api.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeFactoryParseContext {

    NDocLogger messages();

    NDocEngine engine();

    NDocNodeFactoryParseContext push(NDocNode node);

    NDocNode node();

    NDocument document();

    NDocNode[] nodePath();

    NElement element();

    NDocResource source();

//    NPath resolvePath(NStringElement path);
//
//    NPath resolvePath(String path);
//
//    NPath resolvePath(NPath path);

    NDocDocumentFactory documentFactory();

    NElement asPathRef(NElement element);

}
