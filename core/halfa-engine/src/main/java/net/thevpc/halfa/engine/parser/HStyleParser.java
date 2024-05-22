package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.awt.geom.Point2D;

public class HStyleParser {

    public static NOptional<HStyle> parseStyle(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HParseHelper.uid(u.get());
                    switch (uid) {
                        case "background-color":
                        case "background": {
                            NOptional<Color> r = new TsonElementParseHelper(e.toPair().getValue()).parseColor();
                            if (r.isPresent()) {
                                return NOptional.of(HStyles.backgroundColor(r.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "foreground-color":
                        case "foreground":
                        case "color": {
                            NOptional<Color> r = new TsonElementParseHelper(e.toPair().getValue()).parseColor();
                            if (r.isPresent()) {
                                return NOptional.of(HStyles.foregroundColor(r.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "grid-color": {
                            NOptional<Color> r = new TsonElementParseHelper(e.toPair().getValue()).parseColor();
                            if (r.isPresent()) {
                                return NOptional.of(HStyles.gridColor(r.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "line-color": {
                            NOptional<Color> r = new TsonElementParseHelper(e.toPair().getValue()).parseColor();
                            if (r.isPresent()) {
                                return NOptional.of(HStyles.lineColor(r.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "columns":
                        case "cols":
                            return NOptional.of(
                                    HStyles.columns(
                                            new TsonElementParseHelper(e.toPair().getValue()).asInt().get()
                                    )
                            );
                        case "colspan":
                            return NOptional.of(
                                    HStyles.colspan(
                                            new TsonElementParseHelper(e.toPair().getValue()).asInt().get()
                                    )
                            );
                        case "origin": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Point2D.Double> p = new TsonElementParseHelper(v).asPoint2D();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.origin(p.get().getX(), p.get().getY()));
                            }
                            NOptional<HAlign> uu = HAlignEnumParser.parseHAlign(v, f, context);
                            if (uu.isPresent()) {
                                return NOptional.of(HStyles.origin(uu.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "pos":
                        case "position": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Point2D.Double> p = new TsonElementParseHelper(v).asPoint2D();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.position(p.get().getX(), p.get().getY()));
                            }
                            NOptional<HAlign> uu = HAlignEnumParser.parseHAlign(v, f, context);
                            if (uu.isPresent()) {
                                return NOptional.of(HStyles.position(uu.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "size": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Point2D.Double> p = new TsonElementParseHelper(v).asPoint2D();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.size(p.get().getX(), p.get().getY()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "columns-weight": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<double[]> p = new TsonElementParseHelper(v).asDoubleArray();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.columnsWeight(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "rows-weight": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<double[]> p = new TsonElementParseHelper(v).asDoubleArray();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.rowsWeight(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "font-size": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Double> p = new TsonElementParseHelper(v).asDouble();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.fontSize(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "font-family": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<String> p = new TsonElementParseHelper(v).asStringOrName();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.fontFamily(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "font-bold": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.fontBold(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "font-italic": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.fontItalic(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "font-underlined": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.fontUnderlined(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "draw-contour": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.drawContour(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "draw-grid": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.drawGrid(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "fill":
                        case "fill-background": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.fillBackground(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "preserve-shape-ratio": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.preserveShapeRatio(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "raised": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.raised(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "threed":
                        case "three-d": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.threeD(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                    }
                }
            }
        }
        return NOptional.ofNamedEmpty("invalid style " + e);
    }
}
