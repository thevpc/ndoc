package net.thevpc.halfa.extension.shapes2d.donut;

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
public class HDonutImpl extends HNodeParserBase {
    public HDonutImpl(){
        super(false, HNodeType.DONUT);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(currentArg).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "inner-radius": {
                            node.setProperty(HProp.ofDouble(HPropName.INNER_RADIUS, v.asDouble().get()));
                            return true;
                        }
                        case "start-angle": {
                            node.setProperty(HProp.ofDouble(HPropName.START_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "extent-angle": {
                            node.setProperty(HProp.ofDouble(HPropName.EXTENT_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "slice-count": {
                            node.setProperty(HProp.ofInt(HPropName.SLICE_COUNT, v.asInt().get()));
                            return true;
                        }
                        case "dash": {
                            node.setProperty(HProp.ofDouble(HPropName.DASH, v.asDouble().get()));
                            return true;
                        }
                        case "slices": {
                            node.setProperty(HProp.ofDoubleArray(HPropName.SLICES, v.asDoubleArray().get()));
                            return true;
                        }
                        case "colors": {
                            node.setProperty(HProp.ofStringArray(HPropName.COLORS, v.asStringArray().get()));
                            return true;
                        }

                    }
                }
                break;
            }
        }
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
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
                        innerRadius == null ? null : Tson.ofPair("inner-radius", HUtils.toTson(innerRadius.getValue())),
                        startAngle == null ? null : Tson.ofPair("start-angle", HUtils.toTson(startAngle.getValue())),
                        extentAngle == null ? null : Tson.ofPair("extent-angle", HUtils.toTson(extentAngle.getValue())),
                        sliceCount == null ? null : Tson.ofPair("slice-count", HUtils.toTson(sliceCount.getValue())),
                        dash == null ? null : Tson.ofPair("dash", HUtils.toTson(dash.getValue())),
                        colors == null ? null : Tson.ofPair("colors", HUtils.toTson(colors.getValue())),
                        slices == null ? null : Tson.ofPair("slices", HUtils.toTson(slices.getValue()))

                )
                .build();
    }
}
