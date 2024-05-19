package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HArc;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HLine;

public class DefaultHArc extends AbstractHPagePart implements HArc {
    private double startArc;
    private double endArc;

    public DefaultHArc(double startArc, double endArc) {
        this.startArc = startArc;
        this.endArc = endArc;
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
    public HDocumentItemType type() {
        return HDocumentItemType.ARC;
    }
}
