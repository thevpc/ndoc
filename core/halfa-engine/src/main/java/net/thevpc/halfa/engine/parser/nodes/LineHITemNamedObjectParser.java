package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HLine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.parser.util.HParseHelper;
import net.thevpc.halfa.engine.parser.util.TsonElementParseHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class LineHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
    public LineHITemNamedObjectParser() {
        super("line");
    }

    public static boolean addLinePoint(HLine line, Double2 point) {
        if (point != null) {
            if (line.from() == null) {
                line.setFrom(point);
                return true;
            } else if (line.to() == null) {
                line.setTo(point);
                return true;
            }
        }
        return false;
    }

    @Override
    protected HNode node(HDocumentFactory f) {
        return f.ofLine();
    }

    @Override
    protected boolean processChild(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                NOptional<String> uu = h.asStringOrName();
                if (uu.isPresent()) {
                    String uid = HParseHelper.uid(uu.get());
                    switch (uid) {
                        case "from": {
                            NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                ((HLine) p).setFrom(p2d.get());
                            } else {
                                return false;
                            }
                            break;
                        }
                        case "to": {
                            NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                ((HLine) p).setTo(p2d.get());
                            } else {
                                return false;
                            }
                            break;
                        }
                        case "point": {
                            NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                addLinePoint(((HLine) p), p2d.get());
                            } else {
                                return false;
                            }
                            break;
                        }
                        default: {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
                break;
            }
            case UPLET: {
                NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                if (p2d.isPresent()) {
                    addLinePoint(((HLine) p), p2d.get());
                } else {
                    return false;
                }
                break;
            }
        }
        return false;
    }
}
