package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPolyline;
import net.thevpc.halfa.engine.parser.util.HParseHelper;
import net.thevpc.halfa.engine.parser.util.TsonElementParseHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class PolylineHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
    public PolylineHITemNamedObjectParser() {
        super("polyline");
    }

    @Override
    protected HNode node(HDocumentFactory f) {
        return f.ofPolyline();
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
                        case "point": {
                            NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                ((HPolyline) p).add(p2d.get());
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
                    ((HPolyline) p).add(p2d.get());
                } else {
                    return false;
                }
                break;
            }
        }
        return false;
    }
}
