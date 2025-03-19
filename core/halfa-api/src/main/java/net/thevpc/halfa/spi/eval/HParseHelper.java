/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.eval;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonAnnotation;
import net.thevpc.tson.TsonElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author vpc
 */
public class HParseHelper {

    public static String toString(TsonElement e, ParseMode mode) {
        switch (e.type()) {
            case NAME: {
                return e.toName().value();
            }
            case STRING: {
                return e.toString();
            }
            default: {
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
            }
        }
        throw new IllegalArgumentException("expected string. got " + e);
    }


    public static boolean fillAnnotations(TsonElement e, HNode p) {
        for (TsonAnnotation a : e.annotations()) {
            String nn = a.name();
            if (!NBlankable.isBlank(nn)) {
                HashSet<String> o = new HashSet<>(Arrays.asList(p.getAncestors()));
                o.add(HUtils.uid(nn));
                p.setAncestors(o.toArray(new String[0]));
            }
            // add classes as well
            Set<String> allClasses = new HashSet<>();
            for (TsonElement cls : a.children()) {
                NOptional<String[]> ss = ObjEx.of(cls).asStringArrayOrString();
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
