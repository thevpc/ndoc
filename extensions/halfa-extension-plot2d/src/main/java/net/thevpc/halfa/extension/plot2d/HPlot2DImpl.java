package net.thevpc.halfa.extension.plot2d;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

/**
 *
 */
public class HPlot2DImpl extends HNodeParserBase {

    public HPlot2DImpl() {
        super(false, HNodeType.PLOT2D);
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
                        case "width": {
                            node.setProperty(HProp.ofDouble(HPropName.WIDTH, v.asDouble().get()));
                            return true;
                        }
                        case "height": {
                            node.setProperty(HProp.ofDouble(HPropName.HEIGHT, v.asDouble().get()));
                            return true;
                        }
                        case "fun":
                        case "function":
                        {
                            node.setProperty(HProp.ofObject("function", v.asTson().get()));
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArg(id, node, e, f, context);
    }


    @Override
    public TsonElement toTson(HNode item) {
        HProp width = item.getProperty(HPropName.WIDTH).orNull();
        HProp height = item.getProperty(HPropName.HEIGHT).orNull();
        HProp points = item.getProperty("function").orNull();

        return ToTsonHelper.of(
                item,
                engine()
        ).addChildren(
                width == null ? null : Tson.ofPair("width", HUtils.toTson(width.getValue())),
                height == null ? null : Tson.ofPair("height", HUtils.toTson(height.getValue())),
                points == null ? null : Tson.ofPair("fun", HUtils.toTson(points.getValue()))
        ).build();
    }

}
