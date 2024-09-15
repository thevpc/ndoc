package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HScene3DParser extends HNodeParserBase {
    public HScene3DParser() {
        super(false, HNodeType.SCENE3D);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(currentArg).asSimplePair();
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
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
    }

}
