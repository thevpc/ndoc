package net.thevpc.ndoc.api.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.List;
import java.util.Map;

public interface NDocArgumentReader {
    NDocResource source();

    String getId();

    String getUid();

    NElement element();

    NDocNode node();

    int availableCount();

    List<NElement> availableArguments();

    NElement[] allArguments();

    boolean isEmpty();

    NElement peek();

    NElement read();

    NDocDocumentFactory documentFactory();

    NDocNodeFactoryParseContext parseContext();

    Map<String, Object> props();

    int currentIndex();
}
