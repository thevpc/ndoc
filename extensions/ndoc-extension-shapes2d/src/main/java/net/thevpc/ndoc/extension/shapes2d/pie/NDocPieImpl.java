package net.thevpc.ndoc.extension.shapes2d.pie;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.util.NOptional;


public class NDocPieImpl extends NDocNodeParserBase {
    public NDocPieImpl(){
        super(false, HNodeType.PIE);
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
                            info.node.setProperty(HProp.ofDouble(HPropName.START_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "extent-angle": {
                            info.node.setProperty(HProp.ofDouble(HPropName.EXTENT_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "slice-count": {
                            info.node.setProperty(HProp.ofInt(HPropName.SLICE_COUNT, v.asInt().get()));
                            return true;
                        }
                        case "dash": {
                            info.node.setProperty(HProp.ofDouble(HPropName.DASH, v.asDouble().get()));
                            return true;
                        }
                        case "slices": {
                            info.node.setProperty(HProp.ofDoubleArray(HPropName.SLICES, v.asDoubleArray().get()));
                            return true;
                        }
                        case "colors": {
                            info.node.setProperty(HProp.ofStringArray(HPropName.COLORS, v.asStringArray().get()));
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

        HProp startAngle = item.getProperty(HPropName.START_ANGLE).orNull();
        HProp extentAngle = item.getProperty(HPropName.EXTENT_ANGLE).orNull();
        HProp sliceCount = item.getProperty(HPropName.SLICE_COUNT).orNull();
        HProp dash = item.getProperty(HPropName.DASH).orNull();
        HProp slices = item.getProperty(HPropName.SLICES).orNull();
        HProp colors = item.getProperty(HPropName.COLORS).orNull();



        return ToElementHelper.of(
                        item,
                        engine()
                ).addChildren(
                        startAngle == null ? null : NElements.of().ofPair("start-angle", startAngle.getValue()),
                        extentAngle == null ? null : NElements.of().ofPair("extent-angle", extentAngle.getValue()),
                        sliceCount == null ? null : NElements.of().ofPair("slice-count", sliceCount.getValue()),
                        dash == null ? null : NElements.of().ofPair("dash", dash.getValue()),
                        colors == null ? null : NElements.of().ofPair("colors", colors.getValue()),
                        slices == null ? null : NElements.of().ofPair("slices", slices.getValue())

                )
                .build();
    }
}
