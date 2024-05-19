package net.thevpc.halfa.api.model;

import net.thevpc.nuts.util.NOptional;

public interface HDocumentItem {

    HDocumentItemType type();

    NOptional<HStyle> getStyle(HStyleType s);

    HStyle getStyle(HStyleType s, HDocumentPart... context);

    HDocumentItem set(HStyle s);

    HDocumentItem unset(HStyleType s);
}
