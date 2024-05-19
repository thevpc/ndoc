package net.thevpc.halfa.api.model;

import java.util.List;
import java.util.Properties;
import net.thevpc.nuts.util.NOptional;

public interface HDocument {

    HDocument addDocumentPart(HDocumentPart part);

    HDocument add(HPage part);

    HDocumentClass getDocumentClass();

    List<HDocumentPart> getDocumentParts();

    NOptional<String> getName(String name);

    Properties getProperties();

    NOptional<String> getProperty(String name);

    void setDocumentClass(HDocumentClass documentClass);

    HDocument setProperty(String name, String value);
}
