package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HPropUtils {
    public static boolean addPoint(HNode line, HPoint2D point) {
        if (point != null) {
            ObjEx o = ObjEx.of(line.getPropertyValue(HPropName.POINTS).orNull());
            NOptional<HPoint2D[]> hPoint2DArray = o.asHPoint2DArray();
            List<HPoint2D> v = new ArrayList<>();
            if (hPoint2DArray.isPresent()) {
                v.addAll(Arrays.asList(hPoint2DArray.get()));
            }
            v.add(point);
            line.setProperty(HPropName.POINTS, v.toArray(new HPoint2D[0]));
            return true;
        }
        return false;
    }

    public static boolean addPoint(HNode line, HPoint3D point) {
        if (point != null) {
            ObjEx o = ObjEx.of(line.getPropertyValue(HPropName.POINTS).orNull());
            NOptional<HPoint3D[]> hPoint2DArray = o.asHPoint3DArray();
            List<HPoint3D> v = new ArrayList<>();
            if (hPoint2DArray.isPresent()) {
                v.addAll(Arrays.asList(hPoint2DArray.get()));
            }
            v.add(point);
            line.setProperty(HPropName.POINTS, v.toArray(new HPoint3D[0]));
            return true;
        }
        return false;
    }
}
