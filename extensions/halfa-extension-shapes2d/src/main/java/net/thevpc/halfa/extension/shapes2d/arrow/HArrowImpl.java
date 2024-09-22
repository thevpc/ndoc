package net.thevpc.halfa.extension.shapes2d.arrow;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

/**
 *
 */
public class HArrowImpl extends HNodeParserBase {

    public HArrowImpl() {
        super(false, HNodeType.ARROW);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    TsonPair p = info.currentArg.toPair();
                    switch (HUtils.uid(p.key().stringValue())) {
                        case "width": {
                            info.node.setProperty(HPropName.WIDTH, p.value());
                            return true;
                        }
                        case "height": {
                            info.node.setProperty(HPropName.HEIGHT, p.value());
                            return true;
                        }
                        case "points": {
                            info.node.setProperty(HProp.ofObject("points", p.value()));
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
        HProp points = item.getProperty(HPropName.POINTS).orNull();

        return ToTsonHelper.of(
                item,
                engine()
        ).addChildren(
                width == null ? null : Tson.ofPair("width", net.thevpc.halfa.api.util.HUtils.toTson(width.getValue())),
                height == null ? null : Tson.ofPair("height", net.thevpc.halfa.api.util.HUtils.toTson(height.getValue())),
                points == null ? null : Tson.ofPair("points", net.thevpc.halfa.api.util.HUtils.toTson(points.getValue()))
        ).build();
    }

}
