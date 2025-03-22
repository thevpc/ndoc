/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.document;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.thevpc.ndoc.api.document.HDocumentClass;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.base.model.DefaultHNode;
import net.thevpc.ndoc.api.resources.HResourceMonitor;
import net.thevpc.ndoc.spi.util.PagesHelper;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public class DefaultNDocument implements NDocument, Cloneable {

    private HDocumentClass documentClass;
    private Properties properties = new Properties();
    private DefaultHNode root = new DefaultHNode(HNodeType.PAGE_GROUP);
    private HResourceMonitor resources = new HResourceMonitorImpl();

    public DefaultNDocument() {
    }

    @Override
    public HResourceMonitor resources() {
        return resources;
    }

    @Override
    public HDocumentClass documentClass() {
        return documentClass;
    }

    @Override
    public void setDocumentClass(HDocumentClass documentClass) {
        this.documentClass = documentClass;
    }

    @Override
    public HNode root() {
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
    public NDocument add(HNode part) {
        root().add(part);
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
            cloned.root = (DefaultHNode) cloned.root.copy();
        }
        if (cloned.properties != null) {
            cloned.properties = new Properties();
            cloned.properties.putAll(properties);
        }
        return cloned;
    }

    @Override
    public List<HNode> pages() {
        return PagesHelper.resolvePages(this);
    }
}
