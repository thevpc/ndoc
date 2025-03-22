package net.thevpc.ndoc.api.document;

import java.util.List;
import java.util.Properties;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.resources.HResourceMonitor;
import net.thevpc.nuts.util.NOptional;

public interface NDocument {
    HResourceMonitor resources();

    NDocument add(HNode part);

    HDocumentClass documentClass();

    HNode root();

    NOptional<String> name();

    Properties getProperties();

    NOptional<String> getProperty(String name);

    void setDocumentClass(HDocumentClass documentClass);

    NDocument setProperty(String name, String value);

    void mergeDocument(NDocument other);

    NDocument copy();

    List<HNode> pages();
}
