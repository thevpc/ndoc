package net.thevpc.ndoc.extension.shapes2d.donut;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.primitives.*;
import net.thevpc.ndoc.api.document.elem2d.*;
import net.thevpc.ndoc.api.document.elem3d.*;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.*;
import net.thevpc.ndoc.api.model.*;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.base.format.ToElementHelper;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;


public class NDocDonutImpl extends NDocNodeParserBase {
    public NDocDonutImpl(){
        super(false, NDocNodeType.DONUT);
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
                        case "inner-radius": {
                            info.node.setProperty(NDocProp.ofDouble(NDocPropName.INNER_RADIUS, v.asDouble().get()));
                            return true;
                        }
                        case "start-angle": {
                            info.node.setProperty(NDocProp.ofDouble(NDocPropName.START_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "extent-angle": {
                            info.node.setProperty(NDocProp.ofDouble(NDocPropName.EXTENT_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "slice-count": {
                            info.node.setProperty(NDocProp.ofInt(NDocPropName.SLICE_COUNT, v.asInt().get()));
                            return true;
                        }
                        case "dash": {
                            info.node.setProperty(NDocProp.ofDouble(NDocPropName.DASH, v.asDouble().get()));
                            return true;
                        }
                        case "slices": {
                            info.node.setProperty(NDocProp.ofDoubleArray(NDocPropName.SLICES, v.asDoubleArray().get()));
                            return true;
                        }
                        case "colors": {
                            info.node.setProperty(NDocProp.ofStringArray(NDocPropName.COLORS, v.asStringArray().get()));
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

        NDocProp innerRadius = item.getProperty(NDocPropName.INNER_RADIUS).orNull();
        NDocProp startAngle = item.getProperty(NDocPropName.START_ANGLE).orNull();
        NDocProp extentAngle = item.getProperty(NDocPropName.EXTENT_ANGLE).orNull();
        NDocProp sliceCount = item.getProperty(NDocPropName.SLICE_COUNT).orNull();
        NDocProp dash = item.getProperty(NDocPropName.DASH).orNull();
        NDocProp slices = item.getProperty(NDocPropName.SLICES).orNull();
        NDocProp colors = item.getProperty(NDocPropName.COLORS).orNull();



        return ToElementHelper.of(
                        item,
                        engine()
                ).addChildren(
                        innerRadius == null ? null : NElement.ofPair("inner-radius", NDocUtils.toElement(innerRadius.getValue())),
                        startAngle == null ? null : NElement.ofPair("start-angle", NDocUtils.toElement(startAngle.getValue())),
                        extentAngle == null ? null : NElement.ofPair("extent-angle", NDocUtils.toElement(extentAngle.getValue())),
                        sliceCount == null ? null : NElement.ofPair("slice-count", NDocUtils.toElement(sliceCount.getValue())),
                        dash == null ? null : NElement.ofPair("dash", NDocUtils.toElement(dash.getValue())),
                        colors == null ? null : NElement.ofPair("colors", NDocUtils.toElement(colors.getValue())),
                        slices == null ? null : NElement.ofPair("slices", NDocUtils.toElement(slices.getValue()))

                )
                .build();
    }
}
