package net.thevpc.ndoc.api.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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

    NElement read(Predicate<NElement> e);

    NElement peek(Predicate<NElement> e);

    NPairElement peekNamedPair();

    NPairElement readNamedPair();

    int getCurrentArgIndex();

    NDocDocumentFactory getF();

    NDocNodeFactoryParseContext getContext();

    Map<String, Object> getProps();
}
