package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
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
                ObjEx h = new ObjEx(e.toPair().getKey());
                NOptional<String> uu = h.asString();
                if (uu.isPresent()) {
                    String uid = HUtils.uid(uu.get());
                    switch (uid) {
                        case "point": {
                            NOptional<Double2> p2d = new ObjEx(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                HPropUtils.addPoint(p,p2d.get());
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
                NOptional<Double2> p2d = new ObjEx(e.toPair().getValue()).asDouble2();
                if (p2d.isPresent()) {
                    HPropUtils.addPoint(p,p2d.get());
                } else {
                    return false;
                }
                break;
            }
        }
        return false;
    }
}
