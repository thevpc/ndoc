package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HRectangle;

public class DefaultHRectangle extends AbstractHPagePart implements HRectangle {

    public DefaultHRectangle() {
    }


    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.RECTANGLE;
    }
}
