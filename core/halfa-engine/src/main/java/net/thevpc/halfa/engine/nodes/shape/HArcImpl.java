package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.HArc;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

public class HArcImpl extends AbstractHNode implements HArc {
    private double startArc;
    private double endArc;

    public HArcImpl(double startArc, double endArc) {
        this.startArc = startArc;
        this.endArc = endArc;
        set(HStyles.position(HAlign.CENTER));
    }

    @Override
    public double startAngle() {
        return startArc;
    }

    @Override
    public double endAngle() {
        return endArc;
    }

    @Override
    public HNodeType type() {
        return HNodeType.ARC;
    }
}
