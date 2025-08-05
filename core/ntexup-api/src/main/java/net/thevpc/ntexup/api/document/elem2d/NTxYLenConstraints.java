package net.thevpc.ntexup.api.document.elem2d;

import java.util.Objects;

public class NTxYLenConstraints {
    private NTxYLen min;
    private NTxYLen preferred;
    private NTxYLen max;

    public NTxYLenConstraints(NTxYLen min, NTxYLen preferred, NTxYLen max) {
        this.min = min;
        this.preferred = preferred;
        this.max = max;
    }

    public NTxYLen getMin() {
        return min;
    }

    public NTxYLen getPreferred() {
        return preferred;
    }

    public NTxYLen getMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NTxYLenConstraints that = (NTxYLenConstraints) o;
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
