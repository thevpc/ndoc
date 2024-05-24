package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HNodeTypeEnumParser {
    public static NOptional<HNodeType> parse(String e) {
        switch (HParseHelper.uid(e)) {
            case "page":
                return NOptional.of(HNodeType.PAGE);
            case "page-group":
                return NOptional.of(HNodeType.PAGE_GROUP);
            case "stack":
                return NOptional.of(HNodeType.STACK);
            case "grid":
                return NOptional.of(HNodeType.GRID);
            case "flow":
                return NOptional.of(HNodeType.FLOW);

            case "ellipse":
                return NOptional.of(HNodeType.ELLIPSE);
            case "circle":
                return NOptional.of(HNodeType.ELLIPSE);
            case "rectangle":
                return NOptional.of(HNodeType.RECTANGLE);
            case "square":
                return NOptional.of(HNodeType.RECTANGLE);
            case "filler":
                return NOptional.of(HNodeType.FILLER);
            case "arc":
                return NOptional.of(HNodeType.ARC);
            case "line":
                return NOptional.of(HNodeType.LINE);
            case "eq":
            case "equation":
                return NOptional.of(HNodeType.EQUATION);
            case "img":
            case "image":
                return NOptional.of(HNodeType.IMAGE);
            case "ol":
            case "ordered-list":
                return NOptional.of(HNodeType.ORDERED_LIST);
            case "ul":
            case "unordered-list":
                return NOptional.of(HNodeType.UNORDERED_LIST);
            case "polygon":
                return NOptional.of(HNodeType.POLYGON);
            case "polyline":
                return NOptional.of(HNodeType.POLYLINE);
            case "text":
            case "txt":
                return NOptional.of(HNodeType.TEXT);
            case "custom":
                return NOptional.of(HNodeType.CUSTOM);
        }
        return NOptional.ofNamedEmpty("node type " + e);
    }

    public static HNodeType parse(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        NOptional<String> k = new TsonElementParseHelper(e).asStringOrName();
        if (k.isPresent()) {
            switch (HParseHelper.uid(k.get())) {
                case "page":
                    return HNodeType.PAGE;
                case "page-group":
                    return HNodeType.PAGE_GROUP;

                case "stack":
                    return HNodeType.STACK;
                case "grid":
                    return HNodeType.GRID;
                case "flow":
                    return HNodeType.FLOW;

                case "ellipse":
                    return HNodeType.ELLIPSE;
                case "circle":
                    return HNodeType.ELLIPSE;
                case "rectangle":
                    return HNodeType.RECTANGLE;
                case "square":
                    return HNodeType.RECTANGLE;
                case "filler":
                    return HNodeType.FILLER;
                case "arc":
                    return HNodeType.ARC;
                case "line":
                    return HNodeType.LINE;
                case "eq":
                case "equation":
                    return HNodeType.EQUATION;
                case "img":
                case "image":
                    return HNodeType.IMAGE;
                case "ol":
                case "ordered-list":
                    return HNodeType.ORDERED_LIST;
                case "ul":
                case "unordered-list":
                    return HNodeType.UNORDERED_LIST;
                case "polygon":
                    return HNodeType.POLYGON;
                case "polyline":
                    return HNodeType.POLYLINE;
                case "text":
                case "txt":
                    return HNodeType.TEXT;
                case "custom":
                    return HNodeType.CUSTOM;
            }
        }
        throw new IllegalArgumentException("invalid node type " + e);
    }
}
