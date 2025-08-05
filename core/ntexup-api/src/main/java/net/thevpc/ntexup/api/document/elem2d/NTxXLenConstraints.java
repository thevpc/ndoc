package net.thevpc.ntexup.api.document.elem2d;

import java.util.Objects;

public class NTxXLenConstraints {
    private NTxXLen min;
    private NTxXLen preferred;
    private NTxXLen max;

    public NTxXLenConstraints(NTxXLen min, NTxXLen preferred, NTxXLen max) {
        this.min = min;
        this.preferred = preferred;
        this.max = max;
    }

    public NTxXLen getMin() {
        return min;
    }

    public NTxXLen getPreferred() {
        return preferred;
    }

    public NTxXLen getMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NTxXLenConstraints that = (NTxXLenConstraints) o;
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
