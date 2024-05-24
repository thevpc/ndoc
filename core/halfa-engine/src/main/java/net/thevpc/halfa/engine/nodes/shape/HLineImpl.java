package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.HUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class HLineImpl extends AbstractHNode implements HLine {
    private Double2 from;
    private Double2 to;

    public HLineImpl() {
        this(new Double2(0, 0), new Double2(100, 100));
    }

    public HLineImpl(Double2 from, Double2 to) {
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

    public HLine setFrom(Double2 from) {
        this.from = from;
        return this;
    }

    public HLine setTo(Double2 to) {
        this.to = to;
        return this;
    }

    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(
                        this
                ).addChildren(
                        from==null?null:Tson.pair("from", HUtils.toTson(from)),
                        to==null?null:Tson.pair("to", HUtils.toTson(to))
                )
                .build();
    }


    @Override
    public Double2 from() {
        return from;
    }

    @Override
    public Double2 to() {
        return to;
    }

    @Override
    public HNodeType type() {
        return HNodeType.LINE;
    }
}
