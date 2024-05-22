package net.thevpc.halfa.api.model;

import java.util.Objects;

public class XYConstraints {
    private XLenConstraints x;
    private YLenConstraints y;

    public XYConstraints(XLenConstraints x, YLenConstraints y) {
        this.x = x;
        this.y = y;
    }

    public XLenConstraints getX() {
        return x;
    }

    public YLenConstraints getY() {
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
        XYConstraints that = (XYConstraints) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
