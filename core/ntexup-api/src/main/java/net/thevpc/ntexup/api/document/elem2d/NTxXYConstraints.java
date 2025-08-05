package net.thevpc.ntexup.api.document.elem2d;

import java.util.Objects;

public class NTxXYConstraints {
    private NTxXLenConstraints x;
    private NTxYLenConstraints y;

    public NTxXYConstraints(NTxXLenConstraints x, NTxYLenConstraints y) {
        this.x = x;
        this.y = y;
    }

    public NTxXLenConstraints getX() {
        return x;
    }

    public NTxYLenConstraints getY() {
        return y;
    }

    @Override
    public String toString() {
        return "XYConstraints{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NTxXYConstraints that = (NTxXYConstraints) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
