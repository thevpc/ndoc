package net.thevpc.halfa.api.document;

import java.util.List;
import java.util.Properties;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResourceMonitor;
import net.thevpc.nuts.util.NOptional;

public interface HDocument {
    HResourceMonitor resources();

    HDocument add(HNode part);

    HDocumentClass documentClass();

    HNode root();

    NOptional<String> name();

    Properties getProperties();

    NOptional<String> getProperty(String name);

    void setDocumentClass(HDocumentClass documentClass);

    HDocument setProperty(String name, String value);

    void mergeDocument(HDocument other);

    HDocument copy();

    List<HNode> pages();
}
