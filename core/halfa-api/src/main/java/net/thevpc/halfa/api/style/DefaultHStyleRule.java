package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.*;

/**
 * immutable value object
 */
public class DefaultHStyleRule implements HStyleRule {
    private final HStyleMagnitude magnetude;
    private final HProperties styles;
    private final HStyleRuleSelector selector;

    public static DefaultHStyleRule ofAny(HProp... styles) {
        return new DefaultHStyleRule(DefaultHNodeSelector.ofAny(), styles);
    }

    public static DefaultHStyleRule ofType(String type, HProp... styles) {
        return of(type == null ? DefaultHNodeSelector.ofAny() : DefaultHNodeSelector.ofType(type), styles);
    }

    public static DefaultHStyleRule ofName(String name, HProp... styles) {
        return of(name == null ? DefaultHNodeSelector.ofAny() : DefaultHNodeSelector.ofName(name), styles);
    }

    public static DefaultHStyleRule ofClass(String name, HProp... styles) {
        return of(name == null ? DefaultHNodeSelector.ofAny() : DefaultHNodeSelector.ofClasses(name), styles);
    }

    public static DefaultHStyleRule of(HStyleRuleSelector filter, HProp... styles) {
        return new DefaultHStyleRule(filter, styles);
    }

    public DefaultHStyleRule(HStyleRuleSelector selector, HProp... styles) {
        this.styles = new HProperties();
        this.magnetude = new HStyleMagnitude(
                0,
                selector
        );
        this.selector = selector;
        this.styles.set(styles);
    }

    public TsonElement toTson() {
        if(selector==null){
            return Tson.ofPair(
                    Tson.ofString("*"),
                    styles.toTson()
            );
        }
        return Tson.ofPair(
                selector.toTson(),
                styles.toTson()
        );
    }

    @Override
    public boolean accept(HNode node) {
        return selector.test(node);
    }

    public HStyleRuleSelector selector() {
        return selector;
    }

    @Override
    public HProperties styles() {
        return styles;
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
                public Set<HProp> value() {
                    return styles.toSet();
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
            public Set<HProp> value() {
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

    @Override
    public String toString() {
        return "DefaultHStyleRule{" +
                selector +" : "
                +styles
                +'}';
    }
}
