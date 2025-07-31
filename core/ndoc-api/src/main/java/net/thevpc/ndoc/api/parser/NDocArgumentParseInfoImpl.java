package net.thevpc.ndoc.api.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.elem.NElement;

import java.util.*;
import java.util.function.Predicate;

public class ParseArgumentInfoImpl implements ParseArgumentInfo {
    private String id;
    private String uid;
    private NElement tsonElement;
    private NDocNode node;
    private NElement currentArg;
    private NElement[] arguments;
    private List<NElement> remainingArguments;
    private int currentArgIndex;
    private NDocDocumentFactory f;
    private NDocNodeFactoryParseContext context;
    private Map<String, Object> props = new HashMap<>();

    @Override
    public NDocResource source() {
        return getContext().source();
    }

    @Override
    public String getId() {
        return id;
    }

    public ParseArgumentInfo setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public ParseArgumentInfo setUid(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public NElement getTsonElement() {
        return tsonElement;
    }

    public ParseArgumentInfo setTsonElement(NElement tsonElement) {
        this.tsonElement = tsonElement;
        return this;
    }

    @Override
    public NDocNode getNode() {
        return node;
    }

    public ParseArgumentInfo setNode(NDocNode node) {
        this.node = node;
        return this;
    }

    @Override
    public NElement getCurrentArg() {
        return currentArg;
    }

    public ParseArgumentInfo setCurrentArg(NElement currentArg) {
        this.currentArg = currentArg;
        return this;
    }

    @Override
    public NElement[] getArguments() {
        return arguments;
    }

    @Override
    public int getRemainingArgumentsCount() {
        return remainingArguments.size();
    }

    public List<NElement> getRemainingArguments() {
        return Collections.unmodifiableList(remainingArguments);
    }

    public ParseArgumentInfo setArguments(NElement[] arguments) {
        this.arguments = arguments;
        this.remainingArguments = new ArrayList<>(Arrays.asList(arguments));
        return this;
    }

    @Override
    public boolean isEmpty() {
        return remainingArguments.isEmpty();
    }

    @Override
    public NElement peek() {
        if (isEmpty()) {
            return null;
        }
        return remainingArguments.get(0);
    }

    @Override
    public NElement read() {
        if (isEmpty()) {
            return null;
        }
        return remainingArguments.remove(0);
    }

    @Override
    public NElement peekNamedPair() {
        for (NElement remainingArgument : remainingArguments) {
            if (remainingArgument.isNamedPair()) {
                return remainingArgument;
            }
        }
        return null;
    }

    @Override
    public NElement peek(Predicate<NElement> filter) {
        for (NElement e : remainingArguments) {
            if (filter == null || filter.test(e)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public NElement read(Predicate<NElement> test) {
        for (int i = 0; i < remainingArguments.size(); i++) {
            NElement e = remainingArguments.get(i);
            if (test == null || test.test(e)) {
                remainingArguments.remove(i);
                return e;
            }
        }
        return null;
    }

    @Override
    public NElement readNamedPair() {
        return read(NElement::isNamedPair);
    }

    @Override
    public int getCurrentArgIndex() {
        return currentArgIndex;
    }

    public ParseArgumentInfo setCurrentArgIndex(int currentArgIndex) {
        this.currentArgIndex = currentArgIndex;
        return this;
    }

    @Override
    public NDocDocumentFactory getF() {
        return f;
    }

    public ParseArgumentInfo setF(NDocDocumentFactory f) {
        this.f = f;
        return this;
    }

    @Override
    public NDocNodeFactoryParseContext getContext() {
        return context;
    }

    public ParseArgumentInfo setContext(NDocNodeFactoryParseContext context) {
        this.context = context;
        return this;
    }

    @Override
    public Map<String, Object> getProps() {
        return props;
    }

    public ParseArgumentInfo setProps(Map<String, Object> props) {
        this.props = props;
        return this;
    }
}
