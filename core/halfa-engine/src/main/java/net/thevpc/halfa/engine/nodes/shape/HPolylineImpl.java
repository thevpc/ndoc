package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.HUtils;
import net.thevpc.tson.TsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HPolylineImpl extends AbstractHNode implements HPolyline {
    private List<Double2> points;

    public HPolylineImpl(Double2... points) {
        this.points = new ArrayList<>(Arrays.asList(points));
    }

    @Override
    public HPolyline add(Double2 d) {
        if (d != null) {
            points.add(d);
        }
        return this;
    }
    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HPolyline) {
                HPolyline t = (HPolyline) other;
                for (Double2 point : t.points()) {
                    add(point);
                }
            }
        }
    }

    public Double2[] points() {
        return points.toArray(new Double2[0]);
    }

    @Override
    public HNodeType type() {
        return HNodeType.POLYLINE;
    }

    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this).addChildren(HUtils.toTson(points()))
                .build();
    }
}
