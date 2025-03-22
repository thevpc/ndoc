package net.thevpc.ndoc.api.model.elem2d;

import java.util.Objects;

public class XLenConstraints {
    private XLen min;
    private XLen preferred;
    private XLen max;

    public XLenConstraints(XLen min, XLen preferred, XLen max) {
        this.min = min;
        this.preferred = preferred;
        this.max = max;
    }

    public XLen getMin() {
        return min;
    }

    public XLen getPreferred() {
        return preferred;
    }

    public XLen getMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XLenConstraints that = (XLenConstraints) o;
        return Objects.equals(min, that.min) && Objects.equals(preferred, that.preferred) && Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, preferred, max);
    }

    @Override
    public String toString() {
        return "XLenConstraints{" +
                "min=" + min +
                ", preferred=" + preferred +
                ", max=" + max +
                '}';
    }
}
