package net.thevpc.ndoc.api.document.style;

import java.util.Objects;

public class HStyleMagnitude implements Comparable<HStyleMagnitude> {
    private NDocStyleRuleSelector selector;
    private int distance;
    private int support;

    public HStyleMagnitude(int distance, NDocStyleRuleSelector selector) {
        this.distance = distance;
        this.selector = selector == null ? DefaultNDocNodeSelector.ofAny() : selector;
    }

    public NDocStyleRuleSelector getSelector() {
        return selector;
    }

    public int getSupportLevel() {
        return support;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public int compareTo(HStyleMagnitude o) {
        if (this.selector != o.selector) {
            int u = this.selector.compareTo(o.selector);
            if (u != 0) {
                return u;
            }
        }
        if (this.distance != o.distance) {
            int u = Integer.compare(this.distance, o.distance);
            if (u != 0) {
                return u;
            }
        }
        if (this.support != o.support) {
            // bigger is first!
            int u = -Integer.compare(this.support, o.support);
            if (u != 0) {
                return u;
            }
            return u;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HStyleMagnitude that = (HStyleMagnitude) o;
        return distance == that.distance && support == that.support && Objects.equals(selector, that.selector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, distance, support);
    }

    @Override
    public String toString() {
        return "HStyleMagnitude{" +
                "selector=" + selector +
                ", distance=" + distance +
                ", support=" + support +
                '}';
    }
}
