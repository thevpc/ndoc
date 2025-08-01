/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.document;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.thevpc.ndoc.api.document.NDocDocumentClass;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.ndoc.api.source.NDocResourceMonitor;
import net.thevpc.ndoc.engine.util.NDocUtilsPages;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public class DefaultNDocument implements NDocument, Cloneable {

    private NDocDocumentClass documentClass;
    private Properties properties = new Properties();
    private DefaultNDocNode root;
    private NDocResourceMonitor resources = new NDocResourceMonitored();
    private NDocResource source;

    public DefaultNDocument(NDocResource source) {
        this.source = source;
        root = new DefaultNDocNode(NDocNodeType.PAGE_GROUP, source);
    }

    @Override
    public NDocResource source() {
        return source;
    }

    @Override
    public NDocResourceMonitor resources() {
        return resources;
    }

    @Override
    public NDocDocumentClass documentClass() {
        return documentClass;
    }

    @Override
    public void setDocumentClass(NDocDocumentClass documentClass) {
        this.documentClass = documentClass;
    }

    @Override
    public NDocNode root() {
        return root;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public NOptional<String> name() {
        return getProperty("name");
    }

    @Override
    public NDocument setProperty(String name, String value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.setProperty(name, value);
        }
        return this;
    }

    @Override
    public NDocument add(NDocNode part) {
        root().addChild(part);
        return this;
    }

    @Override
    public NOptional<String> getProperty(String name) {
        return NOptional.ofNamed(properties.getProperty(name), name);
    }

    @Override
    public void mergeDocument(NDocument other) {
        if (other != null) {
            if (documentClass == null) {
                documentClass = other.documentClass();
            } else {
                //ignore!
            }
            for (Map.Entry<Object, Object> e : other.getProperties().entrySet()) {
                if (!properties.containsKey(e.getKey())) {
                    properties.setProperty((String) e.getKey(), (String) e.getValue());
                } else {
                    //ignore
                }
            }
            this.root().mergeNode(other.root());
        }
    }

    @Override
    public NDocument copy() {
        DefaultNDocument cloned;
        try {
            cloned = (DefaultNDocument) this.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException(ex);
        }
        if (cloned.root != null) {
            cloned.root = (DefaultNDocNode) cloned.root.copy();
        }
        if (cloned.properties != null) {
            cloned.properties = new Properties();
            cloned.properties.putAll(properties);
        }
        return cloned;
    }

    @Override
    public List<NDocNode> pages() {
        return NDocUtilsPages.resolvePages(this);
    }
}
