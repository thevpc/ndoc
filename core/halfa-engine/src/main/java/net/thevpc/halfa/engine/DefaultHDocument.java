/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.halfa.api.model.HDocumentClass;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.halfa.api.model.HDocumentPart;
import net.thevpc.halfa.api.model.HPage;

/**
 *
 * @author vpc
 */
public class DefaultHDocument implements HDocument {

    private HDocumentClass documentClass;
    private Properties properties = new Properties();
    private List<HDocumentPart> documentParts = new ArrayList<>();

    public HDocumentClass getDocumentClass() {
        return documentClass;
    }

    public void setDocumentClass(HDocumentClass documentClass) {
        this.documentClass = documentClass;
    }

    public List<HDocumentPart> getDocumentParts() {
        return documentParts;
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

    public HDocument add(HPage part) {
        return addDocumentPart(part);
    }

    public HDocument addDocumentPart(HDocumentPart part) {
        if (part != null) {
            this.documentParts.add(part);
        }
        return this;
    }

    public NOptional<String> getProperty(String name) {
        return NOptional.ofNamed(properties.getProperty(name), name);
    }

}
