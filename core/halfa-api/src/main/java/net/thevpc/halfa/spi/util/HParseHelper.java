/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.util;

import net.thevpc.halfa.api.node.HNode;
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
//    public static Object parsePairValue(TsonElement e,HNode node, String name, Class ...types){
//        ObjEx ee = ObjEx.of(e);
//        NOptional<ObjEx.SimplePair> p = ee.asSimplePair();
//        if(p.isPresent()){
//            ObjEx.SimplePair r = p.get();
//            if(r.getNameId().equals(HUtils.uid(name))){
//                for (Class type : types) {
//                    NOptional<?> u = r.getValue().as(type);
//                    if(u.isPresent()){
//                        return u.get();
//                    }
//                }
//            }
//        }
//        return null;
//    }
//


//    public static enum ModelType{
//        STRING,
//        BOOLEAN,
//        DOUBLE,
//        DOUBLE2,
//        INT,
//    }
//    private static class AA{
//        private String name;
//        private String type;
//
//        public AA(String name, String type) {
//            this.name = name;
//            this.type = type;
//        }
//    }
//
//    public static boolean isFunction(String name, TsonElement ff) {
//        if (ff.type() == TsonElementType.FUNCTION) {
//            return name.equals(ff.toFunction().getName());
//        }
//        return false;
//    }


    public static String toString(TsonElement e, ParseMode mode) {
        switch (e.type()) {
            case NAME: {
                return e.toName().getName();
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

//    public static HSize toSize(TsonElement e, ParseMode mode) {
//        switch (e.type()) {
//            case STRING: {
//                String s = e.toString();
//                if (s.endsWith("%")) {
//                    return HSize.of(Double.parseDouble(s.substring(0, s.length() - 1).trim()));
//                }
//                return HSize.of(Double.parseDouble(s));
//            }
//            case DOUBLE: {
//                return HSize.of(e.toDouble().getDouble());
//            }
//            case INT: {
//                return HSize.of(e.toInt().getDouble());
//            }
//            case LONG: {
//                return HSize.of(e.toLong().getDouble());
//            }
//
//            default: {
//                switch (mode) {
//                    case ERROR: {
//                        throw new IllegalArgumentException("expected size. got " + e);
//                    }
//                    case NULL: {
//                        return null;
//                    }
//                }
//            }
//        }
//        throw new IllegalArgumentException("expected string. got " + e);
//    }
//
//    public static Double toDouble(TsonElement e, ParseMode mode) {
//        switch (e.type()) {
//            case DOUBLE: {
//                return e.toDouble().getDouble();
//            }
//            case INT: {
//                return e.toInt().getDouble();
//            }
//            case LONG: {
//                return e.toLong().getDouble();
//            }
//            default: {
//                switch (mode) {
//                    case ERROR: {
//                        throw new IllegalArgumentException("expected double. got " + e);
//                    }
//                    case NULL: {
//                        return null;
//                    }
//                }
//            }
//        }
//        throw new IllegalArgumentException("expected double. got " + e);
//    }
//    public static Integer toInt(TsonElement e, ParseMode mode) {
//        switch (e.type()) {
//            case INT: {
//                return e.toInt().getValue();
//            }
//            case LONG: {
//                return e.toLong().getInt();
//            }
//            default: {
//                switch (mode) {
//                    case ERROR: {
//                        throw new IllegalArgumentException("expected double. got " + e);
//                    }
//                    case NULL: {
//                        return null;
//                    }
//                }
//            }
//        }
//        throw new IllegalArgumentException("expected double. got " + e);
//    }


//    public static Boolean toBoolean(TsonElement e, ParseMode mode) {
//        switch (e.type()) {
//            case BOOLEAN: {
//                return e.toBoolean().getBoolean();
//            }
//            default: {
//                switch (mode) {
//                    case ERROR: {
//                        throw new IllegalArgumentException("expected boolean. got " + e);
//                    }
//                    case NULL: {
//                        return null;
//                    }
//                }
//            }
//        }
//        throw new IllegalArgumentException("expected boolean. got " + e);
//    }

//    public static HProp toStyle(String name, TsonElement a) {
//        switch (name) {
//            case "fontSize":
//                return HProps.fontSize(toDouble(a, ParseMode.ERROR));
//            case "fontBold":
//            case "bold":
//                return HProps.fontBold(toBoolean(a, ParseMode.ERROR));
//            case "fontItalic":
//            case "italic":
//                return HProps.fontItalic(toBoolean(a, ParseMode.ERROR));
//            case "fontUnderlined":
//            case "underlined":
//                return HProps.fontUnderlined(toBoolean(a, ParseMode.ERROR));
//            case "fontFamily":
//                return HProps.fontFamily(toString(a, ParseMode.ERROR));
//            case "width":
//                return HProps.width(toSize(a, ParseMode.ERROR));
//            case "height":
//                return HProps.height(toSize(a, ParseMode.ERROR));
//        }
//        return null;
//    }

//    public static boolean fillElementHDocumentItemStyle(HNode to, String style, TsonElement a) {
//        HProp s = toStyle(style, a);
//        if (s != null) {
//            to.setProperty(s);
//            return true;
//        }
//        return false;
//    }

//    public static boolean fillElementHDocumentItem(HNode to, TsonElement a) {
//        switch (a.type()) {
//            case PAIR: {
//                TsonPair pair = a.toPair();
//                TsonElement k = pair.getKey();
//                TsonElement v = pair.getKey();
//                String name = "";
//                switch (k.type()) {
//                    case NAME:
//                    case STRING: {
//                        name = k.getString();
//                        switch (name) {
//                            case "style": {
//                                switch (v.type()) {
//                                    case OBJECT: {
//                                        for (TsonElement e : v.toObject().all()) {
//                                            fillElementHDocumentItemStyle(to, name, e);
//                                        }
//                                        break;
//                                    }
//                                    case ARRAY: {
//                                        for (TsonElement e : v.toObject().all()) {
//                                            fillElementHDocumentItemStyle(to, name, e);
//                                        }
//                                        break;
//                                    }
//                                    default:
//                                        throw new IllegalArgumentException("expected object or array");
//                                }
//                                return true;
//                            }
//                        }
//                        break;
//                    }
//                }
//                break;
//            }
//        }
//        return false;
//    }

    public static boolean fillAnnotations(TsonElement e, HNode p) {
        for (TsonAnnotation a : e.getAnnotations()) {
            String nn = a.getName();
            if (!NBlankable.isBlank(nn)) {
                HashSet<String> o = new HashSet<>(Arrays.asList(p.getAncestors()));
                o.add(HUtils.uid(nn));
                p.setAncestors(o.toArray(new String[0]));
            }
            // add classes as well
            Set<String> allClasses = new HashSet<>();
            for (TsonElement cls : a.all()) {
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
