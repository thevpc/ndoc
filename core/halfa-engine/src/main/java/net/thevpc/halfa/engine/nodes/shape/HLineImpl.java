package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engin.spibase.parser.AbstractHNodeTypeFactory;
import net.thevpc.halfa.engin.spibase.format.ToTsonHelper;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HLineImpl extends AbstractHNodeTypeFactory {
    public HLineImpl() {
        super(false, HNodeType.LINE);
    }

    @Override
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                TsonPair pp = e.toPair();
                TsonElement k = pp.getKey();
                TsonElement v = pp.getValue();
                ObjEx ph = ObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {
                    switch (HUtils.uid(n.get())) {
                        case "from": {
                            if (isAncestorScene3D(node)) {
                                node.setProperty(HProp.ofHPoint3D(HPropName.FROM, ObjEx.of(v).asHPoint3D().get()));
                            } else {
                                node.setProperty(HProp.ofHPoint2D(HPropName.FROM, ObjEx.of(v).asHPoint2D().get()));
                            }
                            return true;
                        }
                        case "to": {
                            if (isAncestorScene3D(node)) {
                                node.setProperty(HProp.ofHPoint3D(HPropName.TO, ObjEx.of(v).asHPoint3D().get()));
                            } else {
                                node.setProperty(HProp.ofHPoint2D(HPropName.TO, ObjEx.of(v).asHPoint2D().get()));
                            }
                            return false;
                        }
                        case "start-arrow": {
                            node.setProperty(new HProp(HPropName.START_ARROW, ObjEx.of(v).asHArrayHead().get()));
                            return true;
                        }
                        case "end-arrow": {
                            node.setProperty(new HProp(HPropName.END_ARROW, ObjEx.of(v).asHArrayHead().get()));
                            return true;
                        }
                    }
                }
            }
        }
        return super.processArg(id, node, e, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {
        HProp from = item.getProperty(HPropName.FROM).orElse(HProp.ofHPoint2D(HPropName.TO, 0, 0));
        HProp to = item.getProperty(HPropName.TO).orElse(HProp.ofHPoint2D(HPropName.TO, 100, 100));
        return ToTsonHelper.of(
                        item, engine()
                ).addChildren(
                        from == null ? null : Tson.ofPair("from", HUtils.toTson(from)),
                        to == null ? null : Tson.ofPair("to", HUtils.toTson(to))
                )
                .build();
    }

}
