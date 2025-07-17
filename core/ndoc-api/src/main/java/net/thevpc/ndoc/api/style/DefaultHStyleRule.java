package net.thevpc.ndoc.api.style;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.nuts.elem.NElement;

import java.util.*;

/**
 * immutable value object
 */
public class DefaultHStyleRule implements HStyleRule {
    private final HStyleMagnitude magnetude;
    private final NDocProperties styles;
    private final NDocStyleRuleSelector selector;

    public static DefaultHStyleRule ofAny(HProp... styles) {
        return new DefaultHStyleRule(DefaultNDocNodeSelector.ofAny(), styles);
    }

    public static DefaultHStyleRule ofType(String type, HProp... styles) {
        return of(type == null ? DefaultNDocNodeSelector.ofAny() : DefaultNDocNodeSelector.ofType(type), styles);
    }

    public static DefaultHStyleRule ofName(String name, HProp... styles) {
        return of(name == null ? DefaultNDocNodeSelector.ofAny() : DefaultNDocNodeSelector.ofName(name), styles);
    }

    public static DefaultHStyleRule ofClass(String name, HProp... styles) {
        return of(name == null ? DefaultNDocNodeSelector.ofAny() : DefaultNDocNodeSelector.ofClasses(name), styles);
    }

    public static DefaultHStyleRule of(NDocStyleRuleSelector filter, HProp... styles) {
        return new DefaultHStyleRule(filter, styles);
    }

    public DefaultHStyleRule(NDocStyleRuleSelector selector, HProp... styles) {
        this.styles = new NDocProperties();
        this.magnetude = new HStyleMagnitude(
                0,
                selector
        );
        this.selector = selector;
        this.styles.set(styles);
    }

    public NElement toElement() {
        if (selector == null) {
            return NElement.ofPair(
                    NElement.ofString("*"),
                    styles.toElement()
            );
        }
        return NElement.ofPair(
                selector.toElement(),
                styles.toElement()
        );
    }

    @Override
    public boolean acceptNode(NDocNode node) {
        return selector.acceptNode(node);
    }

    public NDocStyleRuleSelector selector() {
        return selector;
    }

    @Override
    public NDocProperties styles() {
        return styles;
    }

    @Override
    public HStyleRuleResult styles(NDocNode node) {
        if (selector == null || selector.acceptNode(node)) {
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
                selector + " : "
                + styles
                + '}';
    }
}
