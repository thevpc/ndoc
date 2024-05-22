package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HLine;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

import java.awt.geom.Point2D;

public class HLineImpl extends AbstractHNode implements HLine {
    private Point2D.Double from;
    private Point2D.Double to;

    public HLineImpl() {
        this(new Point2D.Double(0,0), new Point2D.Double(100,100));
    }

    public HLineImpl(Point2D.Double from, Point2D.Double to) {
        this.from = from;
        this.to = to;
    }

    public HLine setFrom(Point2D.Double from) {
        this.from = from;
        return this;
    }

    public HLine setTo(Point2D.Double to) {
        this.to = to;
        return this;
    }

    @Override
    public Point2D.Double from() {
        return from;
    }

    @Override
    public Point2D.Double to() {
        return to;
    }

    @Override
    public HNodeType type() {
        return HNodeType.LINE;
    }
}
