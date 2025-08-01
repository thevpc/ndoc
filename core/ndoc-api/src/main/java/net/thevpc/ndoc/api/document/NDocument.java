package net.thevpc.ndoc.api.document;

import java.util.List;
import java.util.Properties;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.ndoc.api.source.NDocResourceMonitor;
import net.thevpc.nuts.util.NOptional;

public interface NDocument {
    NDocResource source();
    NDocResourceMonitor resources();

    NDocument add(NDocNode part);

    NDocDocumentClass documentClass();

    NDocNode root();

    NOptional<String> name();

    Properties getProperties();

    NOptional<String> getProperty(String name);

    void setDocumentClass(NDocDocumentClass documentClass);

    NDocument setProperty(String name, String value);

    void mergeDocument(NDocument other);

    NDocument copy();

    List<NDocNode> pages();
}
