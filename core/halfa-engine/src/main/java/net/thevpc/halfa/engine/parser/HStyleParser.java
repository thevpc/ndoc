package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.awt.geom.Point2D;

public class HStyleParser {

    public static NOptional<HItem> parseStyle(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
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
                        case "cols": {
                            return NOptional.of(
                                    HStyles.columns(
                                            new TsonElementParseHelper(e.toPair().getValue()).asInt().get()
                                    )
                            );
                        }
                        case "colspan": {
                            return NOptional.of(
                                    HStyles.colspan(
                                            new TsonElementParseHelper(e.toPair().getValue()).asInt().get()
                                    )
                            );
                        }
                        case "rows": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Integer> p = new TsonElementParseHelper(v).asInt();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.rows(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "rowspan": {
                            return NOptional.of(
                                    HStyles.rowspan(
                                            new TsonElementParseHelper(e.toPair().getValue()).asInt().get()
                                    )
                            );
                        }
                        case "column-weight":
                        case "col-weight":
                        case "wcol":
                        {
                            TsonElement v = e.toPair().getValue();
                            NOptional<double[]> p = new TsonElementParseHelper(v).asDoubleArray();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.columnsWeight(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "row-weight":
                        case "wrow":
                        {
                            TsonElement v = e.toPair().getValue();
                            NOptional<double[]> p = new TsonElementParseHelper(v).asDoubleArray();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.rowsWeight(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
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
                        case "at": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Point2D.Double> p = new TsonElementParseHelper(v).asPoint2D();
                            if (p.isPresent()) {
                                return NOptional.of(
                                        new HItemList().add(
//                                                HStyles.origin(p.get().getX(), p.get().getY())
                                                HStyles.origin(0, 0)
                                        ).add(
                                                HStyles.position(p.get().getX(), p.get().getY())
                                        )
                                );
                            }
                            NOptional<HAlign> uu = HAlignEnumParser.parseHAlign(v, f, context);
                            if (uu.isPresent()) {
                                return NOptional.of(
                                        new HItemList().add(
                                                HStyles.origin(uu.get())
                                        ).add(
                                                HStyles.position(uu.get())
                                        )
                                );
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
                        case "name": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<String> p = new TsonElementParseHelper(v).asStringOrName();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.name(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "template-name": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<String> p = new TsonElementParseHelper(v).asStringOrName();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.templateName(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "disabled": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.disabled(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "template": {
                            TsonElement v = e.toPair().getValue();
                            NOptional<Boolean> p = new TsonElementParseHelper(v).asBoolean();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.template(p.get()));
                            }
                            return NOptional.ofNamedEmpty("invalid " + uid);
                        }
                        case "style-class":
                        case "class":
                        {
                            TsonElement v = e.toPair().getValue();
                            NOptional<String[]> p = new TsonElementParseHelper(v).asStringOrNameArray();
                            if (p.isPresent()) {
                                return NOptional.of(HStyles.styleClasses(p.get()));
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
