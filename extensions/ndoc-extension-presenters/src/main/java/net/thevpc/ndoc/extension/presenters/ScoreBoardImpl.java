package net.thevpc.ndoc.extension.presenters;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.util.NOptional;


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
    public NElement toElem(HNode item) {
        HProp width = item.getProperty(HPropName.WIDTH).orNull();
        HProp height = item.getProperty(HPropName.HEIGHT).orNull();
        HProp points = item.getProperty("function").orNull();

        return ToElementHelper.of(
                item,
                engine()
        ).addChildren(
                width == null ? null : NElements.ofPair("width", width.getValue()),
                height == null ? null : NElements.ofPair("height", height.getValue()),
                points == null ? null : NElements.ofPair("fun", points.getValue())
        ).build();
    }

}
