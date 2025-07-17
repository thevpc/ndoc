package net.thevpc.ndoc.api.model.elem3d;

import net.thevpc.ndoc.api.model.HArrow;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DBox;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DGroup;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DSurface;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DUVSphere;
import net.thevpc.ndoc.api.model.elem3d.primitives.NDocElement3DLine;
import net.thevpc.ndoc.api.model.elem3d.primitives.NDocElement3DPolygon;
import net.thevpc.ndoc.api.model.elem3d.primitives.NDocElement3DPolyline;
import net.thevpc.nuts.util.NDoubleFunction2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NDocElement3DFactory {
    public static NDocElement3DGroup group() {
        return new NDocElement3DGroup();
    }

    public static NDocElement3D axis(NDocPoint3D origin, double length) {
        NDocElement3DGroup g = group();
        g.add(segment(origin, new NDocVector3D(1, 0, 0), length).setEndArrow(new HArrow()).setLinePaint(Color.BLUE));
        g.add(segment(origin, new NDocVector3D(0, 1, 0), length).setEndArrow(new HArrow()).setLinePaint(Color.GREEN));
        g.add(segment(origin, new NDocVector3D(0, 0, 1), length).setEndArrow(new HArrow()).setLinePaint(Color.RED));
        return g;
    }

    public static NDocElement3DLine segment(NDocPoint3D origin, NDocVector3D orientation, double length) {
        double x1 = origin.x;
        double y1 = origin.y;
        double z1 = origin.z;
        NDocVector3D oo = orientation.normalize();
        return line(origin,
                new NDocPoint3D(
                        x1 + length * oo.x,
                        y1 + length * oo.y,
                        z1 + length * oo.z
                )
        );
    }

    public static NDocElement3DLine line(NDocPoint3D a, NDocPoint3D b) {
        return new NDocElement3DLine(a, b);
    }

    public static NDocElement3D cube(NDocPoint3D origin, double size) {
        return box(origin, size, size, size);
    }

    public static NDocElement3DPolygon polygon(NDocPoint3D... points) {
        return new NDocElement3DPolygon(points, true, true);
    }

    public static NDocElement3DSurface surface(NDocPoint3D... points) {
        return new NDocElement3DSurface(points);
    }

    public static NDocElement3DSurface surface(double[] x, double[] y, NDoubleFunction2 f) {
        List<NDocPoint3D> all = new ArrayList<>();
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y.length; j++) {
                all.add(new NDocPoint3D(x[i], y[j], f.apply(x[i], y[j])));
            }
        }
        return new NDocElement3DSurface(all.toArray(new NDocPoint3D[0]));
    }

    public static NDocElement3DPolyline polyline(NDocPoint3D... points) {
        return new NDocElement3DPolyline(points);
    }

    public static NDocElement3D box(NDocPoint3D origin, double sizeX, double sizeY, double sizeZ) {
        return new NDocElement3DBox(origin, sizeX, sizeY, sizeZ);
    }

    public static NDocElement3D sphereUV(NDocPoint3D origin, double radiusX, double radiusY, double radiusZ, int meridians, int parallels) {
        return new NDocElement3DUVSphere(origin, radiusX, radiusY, radiusZ, meridians, parallels);
    }

    public static NDocElement3D sphereUV(NDocPoint3D origin, double radius, int meridians, int parallels) {
        return sphereUV(origin, radius, radius, radius, meridians, parallels);
    }

    public static NDocElement3D sphereUV(NDocPoint3D origin, double radius, int meridians) {
        return sphereUV(origin, radius, radius, radius, meridians, meridians);
    }
}
