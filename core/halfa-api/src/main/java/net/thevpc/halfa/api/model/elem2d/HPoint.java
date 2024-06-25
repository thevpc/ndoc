package net.thevpc.halfa.api.model.elem2d;


public class HPoint {
    private double x;
    private double y;
    private boolean root;

    public static HPoint ofRoot(double x, double y) {
        return new HPoint(x, y, true);
    }

    public static HPoint ofParent(Double2 d) {
        return new HPoint(d.getX(), d.getY(), false);
    }

    public static HPoint ofParent(HPoint2D d) {
        return new HPoint(d.getX(), d.getY(), false);
    }

    public static HPoint ofParent(double x, double y) {
        return new HPoint(x, y, false);
    }

    public HPoint(double x, double y, boolean root) {
        this.x = x;
        this.y = y;
        this.root = root;
    }

    public Double2 valueDouble2(Bounds2 parentBounds, Bounds2 screenBounds) {
        return new Double2(
                (root ? XLen.ofRoot(x) : XLen.ofParent(x)).value(parentBounds, screenBounds),
                (root ? YLen.ofRoot(y) : YLen.ofParent(y)).value(parentBounds, screenBounds)
        );
    }

    public HPoint2D valueHPoint2D(Bounds2 parentBounds, Bounds2 screenBounds) {
        return new HPoint2D(
                (root ? XLen.ofRoot(x) : XLen.ofParent(x)).value(parentBounds, screenBounds),
                (root ? YLen.ofRoot(y) : YLen.ofParent(y)).value(parentBounds, screenBounds)
        );
    }
}
