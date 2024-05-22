/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.document;

import java.util.Map;
import java.util.Properties;

import net.thevpc.halfa.api.document.HDocumentClass;
import net.thevpc.halfa.api.node.HPageGroup;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public class DefaultHDocument implements HDocument {

    private HDocumentClass documentClass;
    private Properties properties = new Properties();
    private HPageGroup root = new HPageGroupImpl();

    public DefaultHDocument() {
    }

    public HDocumentClass getDocumentClass() {
        return documentClass;
    }

    public void setDocumentClass(HDocumentClass documentClass) {
        this.documentClass = documentClass;
    }

    public HPageGroup root() {
        return root;
    }

    public Properties getProperties() {
        return properties;
    }

    public NOptional<String> getName(String name) {
        return getProperty("name");
    }

    public HDocument setProperty(String name, String value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.setProperty(name, value);
        }
        return this;
    }

    public HDocument add(HNode part) {
        root().add(part);
        return this;
    }

    public NOptional<String> getProperty(String name) {
        return NOptional.ofNamed(properties.getProperty(name), name);
    }

    @Override
    public void mergeDocument(HDocument other) {
        if(other!=null){
            if(documentClass==null){
                documentClass = other.getDocumentClass();
            }else{
                //ignore!
            }
            for (Map.Entry<Object, Object> e : other.getProperties().entrySet()) {
                if(!properties.containsKey(e.getKey())) {
                    properties.setProperty((String)e.getKey(), (String) e.getValue());
                }else{
                    //ignore
                }
            }
            this.root().mergeNode(other.root());
        }
    }


    @Override
    public TsonElement toTson() {
        TsonElement tson = root().toTson();
        return tson;
    }
}
