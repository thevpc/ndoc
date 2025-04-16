/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.spi.eval;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonAnnotation;
import net.thevpc.nuts.elem.NElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author vpc
 */
public class NDocParseHelper {

    public static String toString(NElement e, NDocParseMode mode) {
        if(e.isName()){
            return e.toName().value();
        }
        if(e.isAnyString()){
            return e.stringValue();
        }
        switch (mode) {
            case ERROR: {
                throw new IllegalArgumentException("expected string. got " + e);
            }
            case BEST_ERROR: {
                return e.toString();
            }
            case NULL: {
                return null;
            }
        }
        throw new IllegalArgumentException("expected string. got " + e);
    }


    public static boolean fillAnnotations(NElement e, HNode p) {
        for (TsonAnnotation a : e.annotations()) {
            String nn = a.name();
            if (!NBlankable.isBlank(nn)) {
                HashSet<String> o = new HashSet<>(Arrays.asList(p.getAncestors()));
                o.add(HUtils.uid(nn));
                p.setAncestors(o.toArray(new String[0]));
            }
            // add classes as well
            Set<String> allClasses = new HashSet<>();
            for (NElement cls : a.children()) {
                NOptional<String[]> ss = NDocObjEx.of(cls).asStringArrayOrString();
                if (ss.isPresent()) {
                    allClasses.addAll(Arrays.asList(ss.get()));
                }
            }
            if (!allClasses.isEmpty()) {
                p.addStyleClasses(allClasses.toArray(new String[0]));
            }
            return true;
        }
        return false;
    }
}
