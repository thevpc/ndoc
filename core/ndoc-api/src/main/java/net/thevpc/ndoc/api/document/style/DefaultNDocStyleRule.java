package net.thevpc.ndoc.api.document.style;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.*;

/**
 * immutable value object
 */
public class DefaultNDocStyleRule implements NDocStyleRule {
    private final HStyleMagnitude magnetude;
    private final NDocProperties styles;
    private final NDocStyleRuleSelector selector;
    private final NDocResource source;
    private final NDocNode parent;

    public static DefaultNDocStyleRule ofAny(NDocNode parent, NDocResource source, NDocProp... styles) {
        return new DefaultNDocStyleRule(parent, source, DefaultNDocNodeSelector.ofAny(), styles);
    }

    public static DefaultNDocStyleRule ofType(NDocNode parent, NDocResource source, String type, NDocProp... styles) {
        return of(parent, source, type == null ? DefaultNDocNodeSelector.ofAny() : DefaultNDocNodeSelector.ofType(type), styles);
    }

    public static DefaultNDocStyleRule ofName(NDocNode parent, NDocResource source, String name, NDocProp... styles) {
        return of(parent, source, name == null ? DefaultNDocNodeSelector.ofAny() : DefaultNDocNodeSelector.ofName(name), styles);
    }

    public static DefaultNDocStyleRule ofClass(NDocNode parent, NDocResource source, String name, NDocProp... styles) {
        return of(parent, source, name == null ? DefaultNDocNodeSelector.ofAny() : DefaultNDocNodeSelector.ofClasses(name), styles);
    }

    public static DefaultNDocStyleRule of(NDocNode parent, NDocResource source, NDocStyleRuleSelector filter, NDocProp... styles) {
        return new DefaultNDocStyleRule(parent, source, filter, styles);
    }

    public DefaultNDocStyleRule(NDocNode parent, NDocResource source, NDocStyleRuleSelector selector, NDocProp... styles) {
        this.parent = parent;
        this.styles = new NDocProperties(parent);
        this.source = source;
        this.magnetude = new HStyleMagnitude(
                0,
                selector
        );
        this.selector = selector;
        this.styles.set(styles);
    }

    public NDocNode parent() {
        return parent;
    }

    public NDocResource source() {
        return source;
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
                public Set<NDocProp> value() {
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
            public Set<NDocProp> value() {
                throw new IllegalArgumentException("not supported");
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultNDocStyleRule that = (DefaultNDocStyleRule) o;
        return Objects.equals(magnetude, that.magnetude) && Objects.equals(styles, that.styles) && Objects.equals(selector, that.selector) && Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(magnetude, styles, selector,source);
    }

    @Override
    public String toString() {
        return "DefaultHStyleRule{" +
                selector + " : "
                + styles
                + '}';
    }
}
