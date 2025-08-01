package net.thevpc.ndoc.api.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.elem.NElement;

import java.util.List;
import java.util.Map;

public interface NDocArgumentParseInfo {
    NDocResource source();

    String getId();

    String getUid();

    NElement getTsonElement();

    NDocNode node();

    int getRemainingArgumentsCount();

    List<NElement> getRemainingArguments();

    NElement[] getArguments();

    boolean isEmpty();

    NElement peek();

    NElement read();


    NDocDocumentFactory getDocumentFactory();

    NDocNodeFactoryParseContext getContext();

    Map<String, Object> getProps();
}
