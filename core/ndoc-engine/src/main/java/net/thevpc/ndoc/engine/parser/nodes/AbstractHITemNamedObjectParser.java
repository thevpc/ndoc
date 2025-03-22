package net.thevpc.ndoc.engine.parser.nodes;

public abstract class AbstractHITemNamedObjectParser implements HITemNamedObjectParser {
    private String[] ids;

    public AbstractHITemNamedObjectParser(String... ids) {
        this.ids = ids;
    }

    public String[] ids() {
        return ids;
    }
}
