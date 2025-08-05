package net.thevpc.ntexup.api.parser;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.nuts.elem.NElement;

import java.util.Map;

public interface NTxAllArgumentReader {
    NTxSource source();

    String getId();

    String getUid();

    NElement element();

    NTxNode node();

    NElement[] allArguments();

    NTxDocumentFactory documentFactory();

    NTxNodeFactoryParseContext parseContext();

    Map<String, Object> props();

}
