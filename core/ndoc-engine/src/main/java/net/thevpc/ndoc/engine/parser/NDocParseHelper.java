/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.parser.NDocParseMode;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElementAnnotation;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vpc
 */
public class NDocParseHelper {

    public static String toString(NElement e, NDocParseMode mode) {
        if(e.isAnyString()){
            return e.asStringValue().get();
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


    public static boolean fillAnnotations(NElement e, NDocNode p) {
        for (NElementAnnotation a : e.annotations()) {
            String nn=a.name();
            if (!NNameFormat.equalsIgnoreFormat(nn, NDocUtils.COMPILER_DECLARATION_PATH)) {
                // add classes as well
                Set<String> allClasses = new HashSet<>();
                List<NElement> params = a.params();
                if (params != null) {
                    for (NElement cls : params) {
                        NOptional<String[]> ss = NDocObjEx.of(cls).asStringArrayOrString();
                        if (ss.isPresent()) {
                            allClasses.addAll(Arrays.asList(ss.get()));
                        }
                    }
                }
                if (!allClasses.isEmpty()) {
                    p.addStyleClasses(allClasses.toArray(new String[0]));
                }
                return true;
            }
        }
        return false;
    }
}
