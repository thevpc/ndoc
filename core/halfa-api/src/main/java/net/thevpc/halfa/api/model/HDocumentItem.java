package net.thevpc.halfa.api.model;

public interface HDocumentItem {

    HDocumentItemType type();

    HStyle getStyle(HStyleType s);

    HStyle getStyle(HStyleType s, HDocumentPart... context);

    HDocumentPart addStyle(HStyle s);

    HDocumentPart removeStyle(HStyleType s);
}
