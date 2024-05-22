package net.thevpc.halfa.api.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

public class HPoint {
    private double x;
    private double y;
    private boolean root;

    public static HPoint ofRoot(double x, double y) {
        return new HPoint(x, y, true);
    }

    public static HPoint ofParent(double x, double y) {
        return new HPoint(x, y, false);
    }

    public HPoint(double x, double y, boolean root) {
        this.x = x;
        this.y = y;
        this.root = root;
    }

    public Point2D.Double value(Rectangle2D.Double parentBounds, Rectangle2D.Double screenBounds) {
        return new Point2D.Double(
                (root ? XLen.ofRoot(x) : XLen.ofParent(x)).value(parentBounds, screenBounds),
                (root ? YLen.ofRoot(y) : YLen.ofParent(y)).value(parentBounds, screenBounds)
        );
    }
}
