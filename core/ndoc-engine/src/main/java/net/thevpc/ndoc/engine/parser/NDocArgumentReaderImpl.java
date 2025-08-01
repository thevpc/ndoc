package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocAllArgumentReader;
import net.thevpc.ndoc.api.parser.NDocArgumentReader;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.*;

public class NDocArgumentReaderImpl implements NDocArgumentReader, NDocAllArgumentReader {
    private String id;
    private String uid;
    private NElement element;
    private NDocNode node;
    private NElement[] arguments;
    private List<NElement> availableArguments;
    private NDocDocumentFactory f;
    private NDocNodeFactoryParseContext context;
    private Map<String, Object> props = new HashMap<>();
    private int currentIndex = 0;

    @Override
    public NDocResource source() {
        return parseContext().source();
    }

    @Override
    public String getId() {
        return id;
    }

    public NDocArgumentReader setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public NDocArgumentReader setUid(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public NElement element() {
        return element;
    }

    public NDocArgumentReader setElement(NElement tsonElement) {
        this.element = tsonElement;
        return this;
    }

    @Override
    public NDocNode node() {
        return node;
    }

    public NDocArgumentReader setNode(NDocNode node) {
        this.node = node;
        return this;
    }

    @Override
    public NElement[] allArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }

    @Override
    public int availableCount() {
        return availableArguments.size();
    }

    public List<NElement> availableArguments() {
        return Collections.unmodifiableList(availableArguments);
    }

    public NDocArgumentReader setArguments(NElement[] arguments) {
        this.arguments = arguments;
        this.availableArguments = new ArrayList<>(Arrays.asList(arguments));
        return this;
    }

    @Override
    public boolean isEmpty() {
        return availableArguments.isEmpty();
    }

    @Override
    public NElement peek() {
        if (isEmpty()) {
            return null;
        }
        return availableArguments.get(0);
    }

    @Override
    public NElement read() {
        if (isEmpty()) {
            return null;
        }
        NElement n = availableArguments.remove(0);
        currentIndex++;
        return n;
    }

    @Override
    public NDocDocumentFactory documentFactory() {
        return f;
    }

    public NDocArgumentReader setF(NDocDocumentFactory f) {
        this.f = f;
        return this;
    }

    @Override
    public NDocNodeFactoryParseContext parseContext() {
        return context;
    }

    public NDocArgumentReader setContext(NDocNodeFactoryParseContext context) {
        this.context = context;
        return this;
    }

    @Override
    public Map<String, Object> props() {
        return props;
    }

    public NDocArgumentReader setProps(Map<String, Object> props) {
        this.props = props;
        return this;
    }

    @Override
    public int currentIndex() {
        return currentIndex;
    }
}
