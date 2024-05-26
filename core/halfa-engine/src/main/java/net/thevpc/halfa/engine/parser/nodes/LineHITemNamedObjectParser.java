package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class LineHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
    public LineHITemNamedObjectParser() {
        super("line");
    }

    public static boolean addLinePoint(HNode line, Double2 point) {
        if (point != null) {
            if (!line.getPropertyValue(HPropName.FROM).isPresent()) {
                line.setProperty(HPropName.FROM, point);
                return true;
            } else if (!line.getPropertyValue(HPropName.TO).isPresent()) {
                line.setProperty(HPropName.TO, point);
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
                ObjEx h = new ObjEx(e.toPair().getKey());
                NOptional<String> uu = h.asString();
                if (uu.isPresent()) {
                    String uid = HUtils.uid(uu.get());
                    switch (uid) {
                        case "from": {
                            NOptional<Double2> p2d = new ObjEx(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                p.setProperty(HPropName.FROM,p2d.get());
                            } else {
                                return false;
                            }
                            break;
                        }
                        case "to": {
                            NOptional<Double2> p2d = new ObjEx(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                p.setProperty(HPropName.TO,p2d.get());
                            } else {
                                return false;
                            }
                            break;
                        }
                        case "point": {
                            NOptional<Double2> p2d = new ObjEx(e.toPair().getValue()).asDouble2();
                            if (p2d.isPresent()) {
                                return addLinePoint(p, p2d.get());
                            } else {
                                return false;
                            }
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
                    return addLinePoint(p, p2d.get());
                } else {
                    return false;
                }
            }
        }
        return false;
    }

}
