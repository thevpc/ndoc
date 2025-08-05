package net.thevpc.ntexup.api.parser;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.Map;

public interface NDocAllArgumentReader {
    NDocResource source();

    String getId();

    String getUid();

    NElement element();

    NTxNode node();

    NElement[] allArguments();

    NTxDocumentFactory documentFactory();

    NDocNodeFactoryParseContext parseContext();

    Map<String, Object> props();

}
