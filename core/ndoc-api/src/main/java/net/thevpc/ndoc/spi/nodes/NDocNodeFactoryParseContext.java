package net.thevpc.ndoc.spi.nodes;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NStringElement;
import net.thevpc.nuts.io.NPath;

public interface NDocNodeFactoryParseContext {

    NDocLogger messages();

    NDocEngine engine();

    NDocNodeFactoryParseContext push(NDocNode node);

    NDocNode node();

    NDocument document();

    NDocNode[] nodePath();

    NElement element();

    NDocResource source();

    NPath resolvePath(NStringElement path);

    NPath resolvePath(String path);

    NPath resolvePath(NPath path);

    NDocDocumentFactory documentFactory();

    NElement asPathRef(NElement element);

}
