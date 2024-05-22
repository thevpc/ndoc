package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HNodeTypeEnumParser {
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
