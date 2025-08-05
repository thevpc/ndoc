package net.thevpc.ntexup.api.parser;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeFactoryParseContext {

    NDocLogger messages();

    NDocEngine engine();

    NDocNodeFactoryParseContext push(NTxNode node);

    NTxNode node();

    NDocument document();

    NTxNode[] nodePath();

    NElement element();

    NDocResource source();

//    NPath resolvePath(NStringElement path);
//
//    NPath resolvePath(String path);
//
//    NPath resolvePath(NPath path);

    NTxDocumentFactory documentFactory();

    NElement asPathRef(NElement element);

}
