package net.thevpc.ntexup.api.document.style;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.*;

/**
 * immutable value object
 */
public class DefaultNTxStyleRule implements NTxStyleRule {
    private final NTxStyleMagnitude magnetude;
    private final NTxProperties styles;
    private final NTxStyleRuleSelector selector;
    private final NDocResource source;
    private final NTxNode parent;

    public static DefaultNTxStyleRule ofAny(NTxNode parent, NDocResource source, NTxProp... styles) {
        return new DefaultNTxStyleRule(parent, source, DefaultNTxNodeSelector.ofAny(), styles);
    }

    public static DefaultNTxStyleRule ofType(NTxNode parent, NDocResource source, String type, NTxProp... styles) {
        return of(parent, source, type == null ? DefaultNTxNodeSelector.ofAny() : DefaultNTxNodeSelector.ofType(type), styles);
    }

    public static DefaultNTxStyleRule ofName(NTxNode parent, NDocResource source, String name, NTxProp... styles) {
        return of(parent, source, name == null ? DefaultNTxNodeSelector.ofAny() : DefaultNTxNodeSelector.ofName(name), styles);
    }

    public static DefaultNTxStyleRule ofClass(NTxNode parent, NDocResource source, String name, NTxProp... styles) {
        return of(parent, source, name == null ? DefaultNTxNodeSelector.ofAny() : DefaultNTxNodeSelector.ofClasses(name), styles);
    }

    public static DefaultNTxStyleRule of(NTxNode parent, NDocResource source, NTxStyleRuleSelector filter, NTxProp... styles) {
        return new DefaultNTxStyleRule(parent, source, filter, styles);
    }

    public DefaultNTxStyleRule(NTxNode parent, NDocResource source, NTxStyleRuleSelector selector, NTxProp... styles) {
        this.parent = parent;
        this.styles = new NTxProperties(parent);
        this.source = source;
        this.magnetude = new NTxStyleMagnitude(
                0,
                selector
        );
        this.selector = selector;
        this.styles.set(styles);
    }

    public NTxNode parent() {
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
    public boolean acceptNode(NTxNode node) {
        return selector.acceptNode(node);
    }

    public NTxStyleRuleSelector selector() {
        return selector;
    }

    @Override
    public NTxProperties styles() {
        return styles;
    }

    @Override
    public NTxStyleRuleResult styles(NTxNode node) {
        if (selector == null || selector.acceptNode(node)) {
            return new NTxStyleRuleResult() {
                @Override
                public boolean isValid() {
                    return true;
                }

                @Override
                public NTxStyleMagnitude magnitude() {
                    return magnetude;
                }

                @Override
                public Set<NTxProp> value() {
                    return styles.toSet();
                }
            };
        }
        return new NTxStyleRuleResult() {
            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public NTxStyleMagnitude magnitude() {
                throw new IllegalArgumentException("not supported");
            }

            @Override
            public Set<NTxProp> value() {
                throw new IllegalArgumentException("not supported");
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultNTxStyleRule that = (DefaultNTxStyleRule) o;
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
