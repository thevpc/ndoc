/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model;

import net.thevpc.nuts.util.NOptional;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vpc
 */
public abstract class AbstractHDocumentPart implements HDocumentPart {

    private Map<HStyleType, HStyle> styles = new HashMap<>();

    public NOptional<HStyle> getStyle(HStyleType s) {
        return NOptional.ofNamed(styles.get(s),"style "+s);
    }

    public HStyle getStyle(HStyleType s,HDocumentPart... context) {
        HStyle i = styles.get(s);
        return i;
    }

    public HDocumentItem set(HStyle s) {
        if (s != null) {
            styles.put(s.getName(), s);
        }
        return this;
    }

    public HDocumentItem unset(HStyleType s) {
        if (s != null) {
            styles.remove(s);
        }
        return this;
    }
}
