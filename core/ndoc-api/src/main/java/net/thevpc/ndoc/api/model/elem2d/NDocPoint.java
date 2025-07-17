package net.thevpc.ndoc.api.model.elem2d;


public class NDocPoint {
    private double x;
    private double y;
    private boolean root;

    public static NDocPoint ofRoot(double x, double y) {
        return new NDocPoint(x, y, true);
    }

    public static NDocPoint ofParent(NDocDouble2 d) {
        return new NDocPoint(d.getX(), d.getY(), false);
    }

    public static NDocPoint ofParent(NDocPoint2D d) {
        return new NDocPoint(d.getX(), d.getY(), false);
    }

    public static NDocPoint ofParent(double x, double y) {
        return new NDocPoint(x, y, false);
    }

    public NDocPoint(double x, double y, boolean root) {
        this.x = x;
        this.y = y;
        this.root = root;
    }

    public NDocDouble2 valueDouble2(NDocBounds2 parentBounds, NDocBounds2 screenBounds) {
        return new NDocDouble2(
                (root ? XLen.ofRoot(x) : XLen.ofParent(x)).value(parentBounds, screenBounds),
                (root ? YLen.ofRoot(y) : YLen.ofParent(y)).value(parentBounds, screenBounds)
        );
    }

    public NDocPoint2D valueHPoint2D(NDocBounds2 parentBounds, NDocBounds2 screenBounds) {
        return new NDocPoint2D(
                (root ? XLen.ofRoot(x) : XLen.ofParent(x)).value(parentBounds, screenBounds),
                (root ? YLen.ofRoot(y) : YLen.ofParent(y)).value(parentBounds, screenBounds)
        );
    }
}
