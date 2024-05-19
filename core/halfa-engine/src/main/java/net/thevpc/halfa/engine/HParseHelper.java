/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.HStyles;
import net.thevpc.halfa.api.model.HDocumentItem;
import net.thevpc.halfa.api.model.HSize;
import net.thevpc.halfa.api.model.HStyle;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

/**
 *
 * @author vpc
 */
public class HParseHelper {

    public static String toString(TsonElement e, ParseMode mode) {
        switch (e.getType()) {
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

    public static HSize toSize(TsonElement e, ParseMode mode) {
        switch (e.getType()) {
            case STRING: {
                String s = e.toString();
                if (s.endsWith("%")) {
                    return HSize.of(Double.parseDouble(s.substring(0, s.length() - 1).trim()));
                }
                return HSize.of(Double.parseDouble(s));
            }
            case DOUBLE: {
                return HSize.of(e.toDouble().getDouble());
            }
            case INT: {
                return HSize.of(e.toInt().getDouble());
            }
            case LONG: {
                return HSize.of(e.toLong().getDouble());
            }

            default: {
                switch (mode) {
                    case ERROR: {
                        throw new IllegalArgumentException("expected size. got " + e);
                    }
                    case NULL: {
                        return null;
                    }
                }
            }
        }
        throw new IllegalArgumentException("expected string. got " + e);
    }

    public static Double toDouble(TsonElement e, ParseMode mode) {
        switch (e.getType()) {
            case DOUBLE: {
                return e.toDouble().getDouble();
            }
            case INT: {
                return e.toInt().getDouble();
            }
            case LONG: {
                return e.toLong().getDouble();
            }
            default: {
                switch (mode) {
                    case ERROR: {
                        throw new IllegalArgumentException("expected double. got " + e);
                    }
                    case NULL: {
                        return null;
                    }
                }
            }
        }
        throw new IllegalArgumentException("expected double. got " + e);
    }

    public static Boolean toBoolean(TsonElement e, ParseMode mode) {
        switch (e.getType()) {
            case BOOLEAN: {
                return e.toBoolean().getBoolean();
            }
            default: {
                switch (mode) {
                    case ERROR: {
                        throw new IllegalArgumentException("expected boolean. got " + e);
                    }
                    case NULL: {
                        return null;
                    }
                }
            }
        }
        throw new IllegalArgumentException("expected boolean. got " + e);
    }

    public static HStyle toStyle(String name, TsonElement a) {
        switch (name) {
            case "fontSize":
                return HStyles.fontSize(toDouble(a, ParseMode.ERROR));
            case "fontBold":
            case "bold":
                return HStyles.fontBold(toBoolean(a, ParseMode.ERROR));
            case "fontItalic":
            case "italic":
                return HStyles.fontItalic(toBoolean(a, ParseMode.ERROR));
            case "fontUnderlined":
            case "underlined":
                return HStyles.fontUnderlined(toBoolean(a, ParseMode.ERROR));
            case "fontFamily":
                return HStyles.fontFamily(toString(a, ParseMode.ERROR));
            case "width":
                return HStyles.width(toSize(a, ParseMode.ERROR));
            case "height":
                return HStyles.height(toSize(a, ParseMode.ERROR));
        }
        return null;
    }

    public static boolean fillElementHDocumentItemStyle(HDocumentItem to, String style, TsonElement a) {
        HStyle s = toStyle(style, a);
        if (s != null) {
            to.set(s);
            return true;
        }
        return false;
    }

    public static boolean fillElementHDocumentItem(HDocumentItem to, TsonElement a) {
        switch (a.getType()) {
            case PAIR: {
                TsonPair pair = a.toKeyValue();
                TsonElement k = pair.getKey();
                TsonElement v = pair.getKey();
                String name = "";
                switch (k.getType()) {
                    case NAME:
                    case STRING: {
                        name = k.getString();
                        switch (name) {
                            case "style": {
                                switch (v.getType()) {
                                    case OBJECT: {
                                        for (TsonElement e : v.toObject().all()) {
                                            fillElementHDocumentItemStyle(to, name, e);
                                        }
                                        break;
                                    }
                                    case ARRAY: {
                                        for (TsonElement e : v.toObject().all()) {
                                            fillElementHDocumentItemStyle(to, name, e);
                                        }
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException("expected object or array");
                                }
                                return true;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return false;
    }

}
