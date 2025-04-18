package net.thevpc.ndoc.extension.shapes2d.cylinder;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NPairElement;


public class NDocCylinderImpl extends NDocNodeParserBase {

    public NDocCylinderImpl(){
        super(false, HNodeType.CYLINDER);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if(info.currentArg.isSimplePair()){
                    NPairElement pair = info.currentArg.asPair().get();
                    switch (HUtils.uid(pair.key().asStringValue().get())) {
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
    public NElement toElem(HNode item) {
        HProp ellipseHeight = item.getProperty(HPropName.ELLIPSE_H).orNull();
        HProp topColor = item.getProperty(HPropName.TOP_COLOR).orNull();
        HProp segmentCount = item.getProperty(HPropName.SEGMENT_COUNT).orNull();
        return ToElementHelper.of(
                        item,
                        engine()
                ).addChildren(
                        ellipseHeight == null ? null : NElements.of().ofPair("ellipse-height", net.thevpc.ndoc.api.util.HUtils.toElement(ellipseHeight.getValue())),
                        topColor == null ? null : NElements.of().ofPair("top-color", net.thevpc.ndoc.api.util.HUtils.toElement(topColor.getValue())),
                        segmentCount == null ? null : NElements.of().ofPair("segment-count", net.thevpc.ndoc.api.util.HUtils.toElement(segmentCount.getValue()))

                )
                .build();
    }
}
