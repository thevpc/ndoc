package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.nuts.util.NOptional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HPropUtils {
    public static boolean addPoint(HNode line, Double2 point) {
        if (point != null) {
            Object o = line.getPropertyValue(HPropName.POINTS).orNull();
            List<Double2> v = new ArrayList<>();
            if (o instanceof Double2[]) {
                v.addAll(Arrays.asList((Double2[]) o));
            }
            v.add(point);
            line.setProperty(HPropName.POINTS, v.toArray(new Double2[0]));
            return true;
        }
        return false;
    }
}
