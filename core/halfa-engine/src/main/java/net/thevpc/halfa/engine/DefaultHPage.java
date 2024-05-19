package net.thevpc.halfa.engine;

import java.util.ArrayList;
import java.util.List;
import net.thevpc.halfa.api.model.AbstractHDocumentPart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HPage;
import net.thevpc.halfa.api.model.HPagePart;

/**
 *
 * @author vpc
 */
public class DefaultHPage extends AbstractHDocumentPart implements HPage {

    private List<HPagePart> parts = new ArrayList<>();

    public List<HPagePart> getParts() {
        return parts;
    }

    @Override
    public HPage add(HPagePart part) {
        if (part != null) {
            this.parts.add(part);
        }
        return this;
    }

    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.PAGE;
    }

}
