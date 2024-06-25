package net.thevpc.halfa.api.model.elem2d;

import java.util.Objects;

public class XLen {
    private double value;
    private boolean root;

    public static XLen ofMaxParent() {
        return ofParent(100);
    }

    public static XLen ofMaxRoot() {
        return ofRoot(100);
    }

    public static XLen ofMin() {
        return new XLen(0, false);
    }

    public static XLen ofParent(double d) {
        return new XLen(d, false);
    }

    public static XLen ofRoot(double d) {
        return new XLen(d, true);
    }

    public XLen(double value, boolean root) {
        if (!Double.isFinite(value)) {
            this.value = 0;
        } else if (value <= 0) {
            this.value = 0;
        } else {
            this.value = value;
        }
        this.root = root;
    }

    public double value(Bounds2 parentBounds, Bounds2 screenBounds) {
        if (this.root) {
            if (value >= 100) {
                return screenBounds.getWidth();
            }
            return value / 100 * screenBounds.getWidth();
        } else {
            if (value >= 100) {
                return parentBounds.getWidth();
            }
            return value / 100 * parentBounds.getWidth();
        }
    }

    public double getValue() {
        return value;
    }

    public boolean isRoot() {
        return root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XLen xLen = (XLen) o;
        return Double.compare(value, xLen.value) == 0 && root == xLen.root;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, root);
    }

    @Override
    public String toString() {
        if (root) {
            return "XLenOfParent(" + value + ")";
        }
        return "XLenOfRoot(" + value + ")";
    }
}
