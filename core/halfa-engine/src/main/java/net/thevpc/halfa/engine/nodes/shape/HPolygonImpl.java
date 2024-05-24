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

public class HPolygonImpl extends AbstractHNode implements HPolygon {
    private List<Double2> points = new ArrayList<>();

    public HPolygonImpl(Double2... points) {
        this.points.addAll(Arrays.asList(points));
    }


    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HPolygon) {
                HPolygon t = (HPolygon) other;
                for (Double2 point : t.points()) {
                    add(point);
                }
            }
        }
    }

    @Override
    public HPolygon add(Double2 d) {
        if (d != null) {
            points.add(d);
        }
        return this;
    }

    public Double2[] points() {
        return points.toArray(new Double2[0]);
    }

    @Override
    public HNodeType type() {
        return HNodeType.POLYGON;
    }


    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this).addChildren(HUtils.toTson(points()))
                .build();
    }

}
