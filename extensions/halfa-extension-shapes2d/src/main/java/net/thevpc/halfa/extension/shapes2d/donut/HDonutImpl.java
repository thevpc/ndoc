package net.thevpc.halfa.extension.shapes2d.donut;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
public class HDonutImpl extends HNodeParserBase {
    public HDonutImpl(){
        super(false, HNodeType.DONUT);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "inner-radius": {
                            info.node.setProperty(HProp.ofDouble(HPropName.INNER_RADIUS, v.asDouble().get()));
                            return true;
                        }
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
    public TsonElement toTson(HNode item) {

        HProp innerRadius = item.getProperty(HPropName.INNER_RADIUS).orNull();
        HProp startAngle = item.getProperty(HPropName.START_ANGLE).orNull();
        HProp extentAngle = item.getProperty(HPropName.EXTENT_ANGLE).orNull();
        HProp sliceCount = item.getProperty(HPropName.SLICE_COUNT).orNull();
        HProp dash = item.getProperty(HPropName.DASH).orNull();
        HProp slices = item.getProperty(HPropName.SLICES).orNull();
        HProp colors = item.getProperty(HPropName.COLORS).orNull();



        return ToTsonHelper.of(
                        item,
                        engine()
                ).addChildren(
                        innerRadius == null ? null : Tson.ofPair("inner-radius", net.thevpc.halfa.api.util.HUtils.toTson(innerRadius.getValue())),
                        startAngle == null ? null : Tson.ofPair("start-angle", net.thevpc.halfa.api.util.HUtils.toTson(startAngle.getValue())),
                        extentAngle == null ? null : Tson.ofPair("extent-angle", net.thevpc.halfa.api.util.HUtils.toTson(extentAngle.getValue())),
                        sliceCount == null ? null : Tson.ofPair("slice-count", net.thevpc.halfa.api.util.HUtils.toTson(sliceCount.getValue())),
                        dash == null ? null : Tson.ofPair("dash", net.thevpc.halfa.api.util.HUtils.toTson(dash.getValue())),
                        colors == null ? null : Tson.ofPair("colors", net.thevpc.halfa.api.util.HUtils.toTson(colors.getValue())),
                        slices == null ? null : Tson.ofPair("slices", HUtils.toTson(slices.getValue()))

                )
                .build();
    }
}
