package net.thevpc.ntexup.engine.util;

import net.thevpc.ntexup.api.document.elem2d.NDocPoint2D;
import net.thevpc.ntexup.api.document.elem3d.NDocPoint3D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.nuts.util.NOptional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NDocUtilsPoints {
    static
    public boolean addPoints(NTxNode line, NDocPoint2D[] points) {
        boolean result = false;
        for (NDocPoint2D point : points) {
            result |= addPoint(line, point);
        }
        return result;
    }

    static
    public boolean addPoints(NTxNode line, NDocPoint3D[] points) {
        boolean result = false;
        for (NDocPoint3D point : points) {
            result |= addPoint(line, point);
        }
        return result;
    }

    static
    public boolean addPoint(NTxNode line, NDocPoint2D point) {
        if (point != null) {
            NDocValue o = NDocValue.of(line.getPropertyValue(NDocPropName.POINTS).orNull());
            NOptional<NDocPoint2D[]> hPoint2DArray = o.asHPoint2DArray();
            java.util.List<NDocPoint2D> v = new ArrayList<>();
            if (hPoint2DArray.isPresent()) {
                v.addAll(Arrays.asList(hPoint2DArray.get()));
            }
            v.add(point);
            line.setProperty(NDocPropName.POINTS, NDocElementUtils.toElement(v.toArray(new NDocPoint2D[0])));
            return true;
        }
        return false;
    }

    static
    public boolean addPoint(NTxNode line, NDocPoint3D point) {
        if (point != null) {
            NDocValue o = NDocValue.of(line.getPropertyValue(NDocPropName.POINTS).orNull());
            NOptional<NDocPoint3D[]> hPoint2DArray = o.asHPoint3DArray();
            List<NDocPoint3D> v = new ArrayList<>();
            if (hPoint2DArray.isPresent()) {
                v.addAll(Arrays.asList(hPoint2DArray.get()));
            }
            v.add(point);
            line.setProperty(NDocPropName.POINTS, NDocElementUtils.toElement(v.toArray(new NDocPoint3D[0])));
            return true;
        }
        return false;
    }
}
