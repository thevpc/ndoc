package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HLine;
import net.thevpc.halfa.api.model.HPolyline;

import java.awt.geom.Point2D;

public class DefaultHLine extends AbstractHPagePart implements HLine {
    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.LINE;
    }
}
