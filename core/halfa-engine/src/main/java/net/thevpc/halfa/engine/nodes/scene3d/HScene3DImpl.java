package net.thevpc.halfa.engine.nodes.scene3d;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.AbstractHNodeTypeFactory;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HScene3DImpl extends AbstractHNodeTypeFactory {
    public HScene3DImpl() {
        super(false, HNodeType.SCENE3D);
    }

    @Override
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "camera-orientation":
                        case "light-orientation": {
                            NOptional<HPoint3D> p2d = v.asHPoint3D();
                            if (p2d.isPresent()) {
                                node.setProperty(spp.getNameId(), p2d.get());
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
                break;
            }
        }
        return super.processArg(id, node, e, f, context);
    }

}
