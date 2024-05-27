package net.thevpc.halfa.api.style;

import java.util.Objects;

public class HStyleMagnitude implements Comparable<HStyleMagnitude> {
    private HStyleRuleSelector selector;
    private int distance;
    private int support;

    public HStyleMagnitude(int distance, HStyleRuleSelector selector) {
        this.distance = distance;
        this.selector = selector==null? DefaultHNodeSelector.ofAny():selector;
    }

    public HStyleRuleSelector getSelector() {
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
        if (this.distance != o.distance) {
            return Integer.compare(this.distance, o.distance);
        }
        if (this.support != o.support) {
            // bigger is first!
            return -Integer.compare(this.support, o.support);
        }
        if (this.selector != o.selector) {
            return this.selector.compareTo(o.selector);
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
