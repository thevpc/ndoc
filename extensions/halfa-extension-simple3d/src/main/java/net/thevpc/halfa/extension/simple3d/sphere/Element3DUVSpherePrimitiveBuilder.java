package net.thevpc.halfa.extension.simple3d.sphere;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DUVSphere;
import net.thevpc.halfa.api.model.elem3d.composite.Mesh3D;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DTriangle;
import net.thevpc.halfa.spi.HNode3DSimplifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Element3DUVSpherePrimitiveBuilder implements HNode3DSimplifier {
    @Override
    public Class<? extends HElement3D> forType() {
        return Element3DUVSphere.class;
    }

    @Override
    public HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState) {
        Element3DUVSphere ee = (Element3DUVSphere) e;
        HPoint3D origin = ee.getOrigin();
        double radiusX = ee.getRadiusX();
        double radiusY = ee.getRadiusY();
        double radiusZ = ee.getRadiusZ();
        int meridians = ee.getMeridians();
        int parallels = ee.getParallels();
        boolean showMesh = ee.isShowMesh();
        Mesh3D mesh = new Mesh3D();
        mesh.getVertices().add(new HPoint3D(origin.x, origin.y + radiusY, origin.z));
        for (int j = 0; j < parallels - 1; ++j) {
            double polar = Math.PI * (j + 1) / (parallels);
            double sp = Math.sin(polar);
            double cp = Math.cos(polar);
            for (int i = 0; i < meridians; ++i) {
                double azimuth = 2.0 * Math.PI * (i) / (meridians);
                double sa = Math.sin(azimuth);
                double ca = Math.cos(azimuth);
                double x = sp * ca * radiusX + origin.x;
                double y = cp * radiusY + origin.x;
                double z = sp * sa * radiusZ + origin.x;
                mesh.getVertices().add(new HPoint3D(x, y, z));
            }
        }
        mesh.getVertices().add(new HPoint3D(origin.x, origin.y - radiusY, origin.z));

        for (int i = 0; i < meridians; ++i) {
            int a = i + 1;
            int b = (i + 1) % meridians + 1;
            mesh.addTriangle(0, b, a);
        }

        for (int j = 0; j < parallels - 2; ++j) {
            int aStart = j * meridians + 1;
            int bStart = (j + 1) * meridians + 1;
            for (int i = 0; i < meridians; ++i) {
                int a = aStart + i;
                int a1 = aStart + (i + 1) % meridians;
                int b = bStart + i;
                int b1 = bStart + (i + 1) % meridians;
                mesh.addQuad(a, a1, b1, b);
            }
        }

        for (int i = 0; i < meridians; ++i) {
            int a = i + meridians * (parallels - 2) + 1;
            int b = (i + 1) % meridians + meridians * (parallels - 2) + 1;
            mesh.addTriangle(mesh.getVertices().size() - 1, a, b);
        }

        mesh.sortByZ();

        List<HElement3DPrimitive> elements = new ArrayList<>();
        int t = mesh.size();
        HashSet<HElement3D> contours = new HashSet<>();
        for (int i = 0; i < t; i++) {
            Element3DTriangle tt = mesh.triangleAt(i);
            tt.setContour(showMesh);
            tt.setFilled(true);
            elements.add(tt);
            HPoint3D p1 = tt.getP1();
            HPoint3D p2 = tt.getP2();
            HPoint3D p3 = tt.getP3();
//            if (showMesh) {
//                for (Element3DPrimitive e : new Element3DPrimitive[]{
//                        Element3DFactory.line(p1, p2),
//                        Element3DFactory.line(p2, p3),
//                        Element3DFactory.line(p3, p1),
//                }) {
//                    if (contours.add(e)) {
//                        elements.add(e);
//                    }
//                }
//            }
        }
        return elements.toArray(new HElement3DPrimitive[0]);
    }
}
