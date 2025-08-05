package net.thevpc.ntexup.engine.parser.nodeparsers;

import net.thevpc.ntexup.api.parser.NDocArgumentReader;
import net.thevpc.ntexup.api.document.elem3d.NTxPoint3D;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.engine.parser.NDocNodeParserBase;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public class NDocScene3DParser extends NDocNodeParserBase {
    public NDocScene3DParser() {
        super(false, NTxNodeType.SCENE3D);
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
                            NOptional<NTxPoint3D> p2d = v.asHPoint3D();
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
