package net.thevpc.halfa.api.style;

public class HStyleMagnitude implements Comparable<HStyleMagnitude> {
    private HStyleRuleSelector selector;
    private int distance;
    private int support;

    public HStyleMagnitude(int distance, int support, HStyleRuleSelector selector) {
        this.distance = distance;
        this.support = support;
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
}
