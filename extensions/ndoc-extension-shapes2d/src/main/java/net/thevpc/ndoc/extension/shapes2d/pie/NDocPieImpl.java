package net.thevpc.ndoc.extension.shapes2d.pie;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;


public class NDocPieImpl extends NDocNodeParserBase {
    public NDocPieImpl(){
        super(false, NDocNodeType.PIE);
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
                        case "start-angle": {
                            info.node.setProperty(HProp.ofDouble(NDocPropName.START_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "extent-angle": {
                            info.node.setProperty(HProp.ofDouble(NDocPropName.EXTENT_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "slice-count": {
                            info.node.setProperty(HProp.ofInt(NDocPropName.SLICE_COUNT, v.asInt().get()));
                            return true;
                        }
                        case "dash": {
                            info.node.setProperty(HProp.ofDouble(NDocPropName.DASH, v.asDouble().get()));
                            return true;
                        }
                        case "slices": {
                            info.node.setProperty(HProp.ofDoubleArray(NDocPropName.SLICES, v.asDoubleArray().get()));
                            return true;
                        }
                        case "colors": {
                            info.node.setProperty(HProp.ofStringArray(NDocPropName.COLORS, v.asStringArray().get()));
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

        HProp startAngle = item.getProperty(NDocPropName.START_ANGLE).orNull();
        HProp extentAngle = item.getProperty(NDocPropName.EXTENT_ANGLE).orNull();
        HProp sliceCount = item.getProperty(NDocPropName.SLICE_COUNT).orNull();
        HProp dash = item.getProperty(NDocPropName.DASH).orNull();
        HProp slices = item.getProperty(NDocPropName.SLICES).orNull();
        HProp colors = item.getProperty(NDocPropName.COLORS).orNull();



        return ToElementHelper.of(
                        item,
                        engine()
                ).addChildren(
                        startAngle == null ? null : NElement.ofPair("start-angle", startAngle.getValue()),
                        extentAngle == null ? null : NElement.ofPair("extent-angle", extentAngle.getValue()),
                        sliceCount == null ? null : NElement.ofPair("slice-count", sliceCount.getValue()),
                        dash == null ? null : NElement.ofPair("dash", dash.getValue()),
                        colors == null ? null : NElement.ofPair("colors", colors.getValue()),
                        slices == null ? null : NElement.ofPair("slices", slices.getValue())

                )
                .build();
    }
}
