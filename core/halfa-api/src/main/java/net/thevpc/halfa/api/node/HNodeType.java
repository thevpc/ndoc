package net.thevpc.halfa.api.node;

import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

public enum HNodeType {
    PAGE_GROUP,
    PAGE,
    STACK,
    FLOW,
    GRID,
    TEXT,
    RECTANGLE,
    ORDERED_LIST,
    UNORDERED_LIST,
    POLYGON,
    POLYLINE,
    LINE,
    ARC,
    IMAGE,
    EQUATION,
    LATEX,
    ELLIPSE,
    FILLER,
    VOID,
    CUSTOM,
    CTRL_ASSIGN;

    public static NOptional<HNodeType> parse(String e) {
        try {
            String u = NNameFormat.CONST_NAME.format(NStringUtils.trim(e));
            HNodeType p = valueOf(u);
            return NOptional.of(p);
        } catch (Exception ex) {
            //
        }
        e = NNameFormat.LOWER_KEBAB_CASE.format(NStringUtils.trim(e));
        switch (e) {
            case "circle":
                return NOptional.of(HNodeType.ELLIPSE);
            case "square":
                return NOptional.of(HNodeType.RECTANGLE);
            case "eq":
                return NOptional.of(HNodeType.EQUATION);
            case "img":
                return NOptional.of(HNodeType.IMAGE);
            case "ol":
                return NOptional.of(HNodeType.ORDERED_LIST);
            case "ul":
                return NOptional.of(HNodeType.UNORDERED_LIST);
            case "txt":
                return NOptional.of(HNodeType.TEXT);
            case "custom":
                return NOptional.of(HNodeType.CUSTOM);
        }
        return NOptional.ofNamedEmpty("node type " + e);
    }
}
