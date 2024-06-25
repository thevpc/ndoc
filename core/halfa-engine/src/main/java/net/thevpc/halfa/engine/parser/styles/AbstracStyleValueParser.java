package net.thevpc.halfa.engine.parser.styles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstracStyleValueParser implements HStyleValueParser {
    private String type;
    private Set<String> ids;

    public AbstracStyleValueParser(String type, String... ids) {
        this.type = type;
        this.ids = new HashSet<>(new HashSet<>(Arrays.asList(ids)));
    }

    public String type() {
        return type;
    }

    @Override
    public String[] ids() {
        return ids.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return "StyleValueParser(" + type + ")" + Arrays.asList(ids);
    }
}
