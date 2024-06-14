package net.thevpc.halfa.api.model.elem3d;

import net.thevpc.halfa.api.fct.HFunctionXY;
import net.thevpc.halfa.api.model.HArrayHead;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DBox;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DGroup;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DSurface;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DUVSphere;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DLine;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DPolygon;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DPolyline;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Element3DFactory {
    public static Element3DGroup group() {
        return new Element3DGroup();
    }

    public static HElement3D axis(HPoint3D origin, double length) {
        Element3DGroup g = group();
        g.add(segment(origin, new HVector3D(1, 0, 0), length).setEndType(HArrayHead.ARROW).setLinePaint(Color.BLUE));
        g.add(segment(origin, new HVector3D(0, 1, 0), length).setEndType(HArrayHead.ARROW).setLinePaint(Color.GREEN));
        g.add(segment(origin, new HVector3D(0, 0, 1), length).setEndType(HArrayHead.ARROW).setLinePaint(Color.RED));
        return g;
    }

    public static Element3DLine segment(HPoint3D origin, HVector3D orientation, double length) {
        double x1 = origin.x;
        double y1 = origin.y;
        double z1 = origin.z;
        HVector3D oo = orientation.normalize();
        return line(origin,
                new HPoint3D(
                        x1 + length * oo.x,
                        y1 + length * oo.y,
                        z1 + length * oo.z
                )
        );
    }

    public static Element3DLine line(HPoint3D a, HPoint3D b) {
        return new Element3DLine(a, b);
    }

    public static HElement3D cube(HPoint3D origin, double size) {
        return box(origin, size, size, size);
    }

    public static Element3DPolygon polygon(HPoint3D... points) {
        return new Element3DPolygon(points, true, true);
    }

    public static Element3DSurface surface(HPoint3D... points) {
        return new Element3DSurface(points);
    }

    public static Element3DSurface surface(double[] x, double[] y, HFunctionXY f) {
        List<HPoint3D> all=new ArrayList<>();
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y.length; j++) {
                all.add(new HPoint3D(x[i],y[j],f.apply(x[i],y[j])));
            }
        }
        return new Element3DSurface(all.toArray(new HPoint3D[0]));
    }

    public static Element3DPolyline polyline(HPoint3D... points) {
        return new Element3DPolyline(points);
    }

    public static HElement3D box(HPoint3D origin, double sizeX, double sizeY, double sizeZ) {
        return new Element3DBox(origin, sizeX, sizeY, sizeZ);
    }

    public static HElement3D sphereUV(HPoint3D origin, double radiusX, double radiusY, double radiusZ, int meridians, int parallels) {
        return new Element3DUVSphere(origin, radiusX, radiusY, radiusZ, meridians, parallels);
    }

    public static HElement3D sphereUV(HPoint3D origin, double radius, int meridians, int parallels) {
        return sphereUV(origin, radius, radius, radius, meridians, parallels);
    }

    public static HElement3D sphereUV(HPoint3D origin, double radius, int meridians) {
        return sphereUV(origin, radius, radius, radius, meridians, meridians);
    }
}
