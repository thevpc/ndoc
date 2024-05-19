package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HEllipse;
import net.thevpc.halfa.api.model.HRectangle;

public class DefaultHEllipse extends AbstractHPagePart implements HEllipse {

    public DefaultHEllipse() {
    }

    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.ELLIPSE;
    }
}
