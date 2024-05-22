package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.TsonSer;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.geom.Point2D;

public class HLineImpl extends AbstractHNode implements HLine {
    private Point2D.Double from;
    private Point2D.Double to;

    public HLineImpl() {
        this(new Point2D.Double(0, 0), new Point2D.Double(100, 100));
    }

    public HLineImpl(Point2D.Double from, Point2D.Double to) {
        this.from = from;
        this.to = to;
    }


    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HLine) {
                HLine t = (HLine) other;
                if (t.from() != null && t.to() != null) {
                    this.from = t.from();
                    this.to = t.to();
                } else if (t.from() != null) {
                    if (this.from == null) {
                        this.from = t.from();
                    } else if (this.to == null) {
                        this.from = t.to();
                    } else {
                        this.from = t.from();
                    }
                } else if (t.to() != null) {
                    if (this.from == null) {
                        this.from = t.to();
                    } else if (this.to == null) {
                        this.from = t.to();
                    } else {
                        this.to = t.to();
                    }
                }
            }
        }
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
    public TsonElement toTson() {
        return ToTsonHelper.of(
                        this
                ).addChildren(
                        from==null?null:Tson.pair("from",TsonSer.toTson(from)),
                        to==null?null:Tson.pair("to",TsonSer.toTson(to))
                )
                .build();
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
