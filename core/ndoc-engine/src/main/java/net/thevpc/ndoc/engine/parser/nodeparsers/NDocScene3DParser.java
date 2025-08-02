package net.thevpc.ndoc.engine.parser.nodeparsers;

import net.thevpc.ndoc.api.parser.NDocArgumentReader;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.engine.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public class NDocScene3DParser extends NDocNodeParserBase {
    public NDocScene3DParser() {
        super(false, NDocNodeType.SCENE3D);
    }

    @Override
    protected boolean processArgument(NDocArgumentReader info) {
        NElement currentArg = info.peek();
        switch (currentArg.type()) {
            case PAIR: {
                NOptional<NDocValue.SimplePair> sp = NDocValue.of(currentArg).asSimplePair();
                if (sp.isPresent()) {
                    NDocValue.SimplePair spp = sp.get();
                    NDocValue v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "camera-orientation":
                        case "light-orientation": {
                            NOptional<NDocPoint3D> p2d = v.asHPoint3D();
                            if (p2d.isPresent()) {
                                info.node().setProperty(spp.getNameId(), p2d.get());
                                info.read();
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
        return super.processArgument(info);
    }

}
