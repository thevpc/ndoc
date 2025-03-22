package net.thevpc.ndoc.extension.presenters;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.format.ToTsonHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

/**
 *
 */
public class ScoreBoardImpl extends NDocNodeParserBase {

    public ScoreBoardImpl() {
        super(false, "scoreboard");
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    NDocObjEx.SimplePair spp = sp.get();
                    NDocObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "width": {
                            info.node.setProperty(HProp.ofDouble(HPropName.WIDTH, v.asDouble().get()));
                            return true;
                        }
                        case "height": {
                            info.node.setProperty(HProp.ofDouble(HPropName.HEIGHT, v.asDouble().get()));
                            return true;
                        }
                        case "fun":
                        case "function":
                        {
                            info.node.setProperty(HProp.ofObject("function", v.asTson().get()));
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArgument(info);
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
                width == null ? null : Tson.ofPair("width", net.thevpc.ndoc.api.util.HUtils.toTson(width.getValue())),
                height == null ? null : Tson.ofPair("height", net.thevpc.ndoc.api.util.HUtils.toTson(height.getValue())),
                points == null ? null : Tson.ofPair("fun", HUtils.toTson(points.getValue()))
        ).build();
    }

}
