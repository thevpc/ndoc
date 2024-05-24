package net.thevpc.halfa.engine.parser;

abstract class AbstractHITemNamedObjectParser implements HITemNamedObjectParser {
    private String[] ids;

    public AbstractHITemNamedObjectParser(String... ids) {
        this.ids = ids;
    }

    public String[] ids() {
        return ids;
    }
}
