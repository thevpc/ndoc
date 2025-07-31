package net.thevpc.ndoc.api.document.style;

import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.util.NOptional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HPropUtils {
    public static boolean addPoint(NDocNode line, NDocPoint2D point) {
        if (point != null) {
            NDocObjEx o = NDocObjEx.of(line.getPropertyValue(NDocPropName.POINTS).orNull());
            NOptional<NDocPoint2D[]> hPoint2DArray = o.asHPoint2DArray();
            List<NDocPoint2D> v = new ArrayList<>();
            if (hPoint2DArray.isPresent()) {
                v.addAll(Arrays.asList(hPoint2DArray.get()));
            }
            v.add(point);
            line.setProperty(NDocPropName.POINTS, NElemUtils.toElement(v.toArray(new NDocPoint2D[0])));
            return true;
        }
        return false;
    }

    public static boolean addPoint(NDocNode line, NDocPoint3D point) {
        if (point != null) {
            NDocObjEx o = NDocObjEx.of(line.getPropertyValue(NDocPropName.POINTS).orNull());
            NOptional<NDocPoint3D[]> hPoint2DArray = o.asHPoint3DArray();
            List<NDocPoint3D> v = new ArrayList<>();
            if (hPoint2DArray.isPresent()) {
                v.addAll(Arrays.asList(hPoint2DArray.get()));
            }
            v.add(point);
            line.setProperty(NDocPropName.POINTS, NElemUtils.toElement(v.toArray(new NDocPoint3D[0])));
            return true;
        }
        return false;
    }
}
