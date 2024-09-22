package net.thevpc.halfa.extension.shapes2d.cylinder;

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

public class HCylinderImpl extends HNodeParserBase {

    public HCylinderImpl(){
        super(false, HNodeType.CYLINDER);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if(info.currentArg.isSimplePair()){
                    TsonPair pair = info.currentArg.toPair();
                    switch (HUtils.uid(pair.key().stringValue())) {
                        case "ellipse-height": {
                            info.node.setProperty(HPropName.ELLIPSE_H, pair.value());
                            return true;
                        }

                        case "top-color": {
                            info.node.setProperty(HPropName.TOP_COLOR, pair.value());
                            return true;
                        }
                        case "segment-count": {
                            info.node.setProperty(HPropName.SEGMENT_COUNT, pair.value());
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
    public TsonElement toTson (HNode item) {
        HProp ellipseHeight = item.getProperty(HPropName.ELLIPSE_H).orNull();
        HProp topColor = item.getProperty(HPropName.TOP_COLOR).orNull();
        HProp segmentCount = item.getProperty(HPropName.SEGMENT_COUNT).orNull();
        return ToTsonHelper.of(
                        item,
                        engine()
                ).addChildren(
                        ellipseHeight == null ? null : Tson.ofPair("ellipse-height", net.thevpc.halfa.api.util.HUtils.toTson(ellipseHeight.getValue())),
                        topColor == null ? null : Tson.ofPair("top-color", net.thevpc.halfa.api.util.HUtils.toTson(topColor.getValue())),
                        segmentCount == null ? null : Tson.ofPair("segment-count", net.thevpc.halfa.api.util.HUtils.toTson(segmentCount.getValue()))

                )
                .build();
    }
}
