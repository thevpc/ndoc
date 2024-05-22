package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class DefaultHStyleRule implements HStyleRule {
    private final HStyleMagnitude magnetude;
    private final HStyleMap styles;
    private final HStyleRuleSelector selector;

    public static DefaultHStyleRule ofAny(HStyle... styles) {
        return new DefaultHStyleRule(null, styles);
    }

    public static DefaultHStyleRule ofType(HNodeType type, HStyle... styles) {
        return of(type == null ? null : DefaultHNodeSelector.ofType(type), styles);
    }

    public static DefaultHStyleRule ofName(String name, HStyle... styles) {
        return of(name == null ? null : DefaultHNodeSelector.ofName(name), styles);
    }

    public static DefaultHStyleRule ofClass(String name, HStyle... styles) {
        return of(name == null ? null : DefaultHNodeSelector.ofClass(name), styles);
    }

    public static DefaultHStyleRule of(HStyleRuleSelector filter, HStyle... styles) {
        return new DefaultHStyleRule(filter, styles);
    }

    public DefaultHStyleRule(HStyleRuleSelector selector, HStyle... styles) {
        this.styles = new HStyleMap();
        this.magnetude = new HStyleMagnitude(
                0,
                1,
                selector
        );
        this.selector = selector;
        this.styles.set(styles);
    }

    @Override
    public TsonElement toTson() {
        if(selector==null){
            return Tson.pair(
                    Tson.string("*"),
                    styles.toTson()
            );
        }
        return Tson.pair(
                selector.toTson(),
                styles.toTson()
        );
    }

    @Override
    public HStyleRuleResult styles(HNode node) {
        if (selector == null || selector.test(node)) {
            return new HStyleRuleResult() {
                @Override
                public boolean isValid() {
                    return true;
                }

                @Override
                public HStyleMagnitude magnitude() {
                    return magnetude;
                }

                @Override
                public Set<HStyle> value() {
                    return styles.styles();
                }
            };
        }
        return new HStyleRuleResult() {
            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public HStyleMagnitude magnitude() {
                throw new IllegalArgumentException("not supported");
            }

            @Override
            public Set<HStyle> value() {
                throw new IllegalArgumentException("not supported");
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultHStyleRule that = (DefaultHStyleRule) o;
        return Objects.equals(magnetude, that.magnetude) && Objects.equals(styles, that.styles) && Objects.equals(selector, that.selector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(magnetude, styles, selector);
    }


}
