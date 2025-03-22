package net.thevpc.ndoc.api.model.elem2d;

import java.util.Objects;

public class YLenConstraints {
    private YLen min;
    private YLen preferred;
    private YLen max;

    public YLenConstraints(YLen min, YLen preferred, YLen max) {
        this.min = min;
        this.preferred = preferred;
        this.max = max;
    }

    public YLen getMin() {
        return min;
    }

    public YLen getPreferred() {
        return preferred;
    }

    public YLen getMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YLenConstraints that = (YLenConstraints) o;
        return Objects.equals(min, that.min) && Objects.equals(preferred, that.preferred) && Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, preferred, max);
    }

    @Override
    public String toString() {
        return "YLenConstraints{" +
                "min=" + min +
                ", preferred=" + preferred +
                ", max=" + max +
                '}';
    }
}
