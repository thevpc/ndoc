package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.api.style.HStyleType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstracStyleValueParser implements HStyleValueParser {
    private HStyleType type;
    private Set<String> ids;

    public AbstracStyleValueParser(HStyleType type, String... ids) {
        this.ids = new HashSet<>(new HashSet<>(Arrays.asList(ids)));
    }

    @Override
    public String[] ids() {
        return ids.toArray(new String[0]);
    }
}
