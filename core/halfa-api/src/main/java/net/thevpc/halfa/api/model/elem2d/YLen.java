package net.thevpc.halfa.api.model.elem2d;

import java.util.Objects;

public class YLen {
    private double value;
    private boolean root;

    public static YLen ofMaxParent() {
        return ofParent(100);
    }

    public static YLen ofMaxRoot() {
        return ofRoot(100);
    }

    public static YLen ofMin() {
        return new YLen(0, false);
    }

    public static YLen ofParent(double d) {
        return new YLen(d, false);
    }

    public static YLen ofRoot(double d) {
        return new YLen(d, true);
    }

    public YLen(double value, boolean root) {
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
                return screenBounds.getHeight();
            }
            return value / 100 * screenBounds.getHeight();
        } else {
            if (value >= 100) {
                return parentBounds.getHeight();
            }
            return value / 100 * parentBounds.getHeight();
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
        YLen yLen = (YLen) o;
        return Double.compare(value, yLen.value) == 0 && root == yLen.root;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, root);
    }

    @Override
    public String toString() {
        if(root){
            return "YLenOfParent(" +value+")";
        }
        return "YLenOfRoot(" +value+")";
    }
}
