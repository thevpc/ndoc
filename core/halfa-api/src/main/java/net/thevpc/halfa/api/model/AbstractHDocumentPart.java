/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vpc
 */
public abstract class AbstractHDocumentPart implements HDocumentPart {

    private Map<HStyleType, HStyle> styles = new HashMap<>();

    public HStyle getStyle(HStyleType s) {
        return styles.get(s);
    }

    public HStyle getStyle(HStyleType s,HDocumentPart... context) {
        HStyle i = styles.get(s);
        return i;
    }

    public HDocumentPart addStyle(HStyle s) {
        if (s != null) {
            styles.put(s.getName(), s);
        }
        return this;
    }

    public HDocumentPart removeStyle(HStyleType s) {
        if (s != null) {
            styles.remove(s);
        }
        return this;
    }
}
