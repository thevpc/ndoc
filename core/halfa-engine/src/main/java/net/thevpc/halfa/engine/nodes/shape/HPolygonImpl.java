package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HPolygon;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HPolygonImpl extends AbstractHNode implements HPolygon {
    private List<Point2D.Double> points = new ArrayList<>();

    public HPolygonImpl(Point2D.Double... points) {
        this.points.addAll(Arrays.asList(points));
    }

    @Override
    public HPolygon add(Point2D.Double d) {
        if (d != null) {
            points.add(d);
        }
        return this;
    }

    public Point2D.Double[] points() {
        return points.toArray(new Point2D.Double[0]);
    }

    @Override
    public HNodeType type() {
        return HNodeType.POLYGON;
    }
}
