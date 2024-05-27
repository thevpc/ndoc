package net.thevpc.halfa.api.document;

import java.util.Properties;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.nuts.util.NOptional;

public interface HDocument {

    HDocument add(HNode part);

    HDocumentClass getDocumentClass();

    HNode root();

    NOptional<String> getName(String name);

    Properties getProperties();

    NOptional<String> getProperty(String name);

    void setDocumentClass(HDocumentClass documentClass);

    HDocument setProperty(String name, String value);

    void mergeDocument(HDocument other);
}
