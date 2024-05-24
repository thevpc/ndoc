package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.HUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class HArcImpl extends AbstractHNode implements HArc {
    private Double startAngle;
    private Double endAngle;

    public HArcImpl(Double startAngle, Double endAngle) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HArc) {
                HArc t = (HArc) other;
                if (t.startAngle() != null) {
                    this.startAngle = t.startAngle();
                }
                if (t.endAngle() != null) {
                    this.endAngle = t.endAngle();
                }
            }
        }
    }

    @Override
    public Double startAngle() {
        return startAngle;
    }

    @Override
    public Double endAngle() {
        return endAngle;
    }

    @Override
    public HNodeType type() {
        return HNodeType.ARC;
    }


    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(
                        this
                ).addChildren(
                        startAngle==null?null: Tson.pair("start", HUtils.toTson(startAngle)),
                        endAngle==null?null:Tson.pair("end", HUtils.toTson(endAngle))
                )
                .build();
    }

}
