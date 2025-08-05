package net.thevpc.ntexup.api.document;

import java.util.List;
import java.util.Properties;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.ntexup.api.source.NDocResourceMonitor;
import net.thevpc.nuts.util.NOptional;

public interface NDocument {
    NDocResource source();
    NDocResourceMonitor resources();

    NDocument add(NTxNode part);

    NDocDocumentClass documentClass();

    NTxNode root();

    NOptional<String> name();

    Properties getProperties();

    NOptional<String> getProperty(String name);

    void setDocumentClass(NDocDocumentClass documentClass);

    NDocument setProperty(String name, String value);

    void mergeDocument(NDocument other);

    NDocument copy();

    List<NTxNode> pages();
}
