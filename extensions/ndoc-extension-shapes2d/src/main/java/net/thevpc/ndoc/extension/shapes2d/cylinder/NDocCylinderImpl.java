package net.thevpc.ndoc.extension.shapes2d.cylinder;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocProp;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.base.format.ToElementHelper;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;


public class NDocCylinderImpl extends NDocNodeParserBase {

    public NDocCylinderImpl(){
        super(false, NDocNodeType.CYLINDER);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if(info.currentArg.isSimplePair()){
                    NPairElement pair = info.currentArg.asPair().get();
                    switch (NDocUtils.uid(pair.key().asStringValue().get())) {
                        case "ellipse-height": {
                            info.node.setProperty(NDocPropName.ELLIPSE_H, pair.value());
                            return true;
                        }

                        case "top-color": {
                            info.node.setProperty(NDocPropName.TOP_COLOR, pair.value());
                            return true;
                        }
                        case "segment-count": {
                            info.node.setProperty(NDocPropName.SEGMENT_COUNT, pair.value());
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
    public NElement toElem(NDocNode item) {
        NDocProp ellipseHeight = item.getProperty(NDocPropName.ELLIPSE_H).orNull();
        NDocProp topColor = item.getProperty(NDocPropName.TOP_COLOR).orNull();
        NDocProp segmentCount = item.getProperty(NDocPropName.SEGMENT_COUNT).orNull();
        return ToElementHelper.of(
                        item,
                        engine()
                ).addChildren(
                        ellipseHeight == null ? null : NElement.ofPair("ellipse-height", NDocUtils.toElement(ellipseHeight.getValue())),
                        topColor == null ? null : NElement.ofPair("top-color", NDocUtils.toElement(topColor.getValue())),
                        segmentCount == null ? null : NElement.ofPair("segment-count", NDocUtils.toElement(segmentCount.getValue()))

                )
                .build();
    }
}
