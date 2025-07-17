package net.thevpc.ndoc.extension.simple3d.sphere;

import net.thevpc.ndoc.api.model.elem3d.NDocElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.NDocElement3D;
import net.thevpc.ndoc.api.model.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.model.elem3d.RenderState3D;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DUVSphere;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocMesh3D;
import net.thevpc.ndoc.api.model.elem3d.primitives.NDocElement3DTriangle;
import net.thevpc.ndoc.spi.NDocElement3DRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Element3DUVSpherePrimitiveBuilder implements NDocElement3DRenderer {
    @Override
    public Class<? extends NDocElement3D> forType() {
        return NDocElement3DUVSphere.class;
    }

    @Override
    public NDocElement3DPrimitive[] toPrimitives(NDocElement3D e, RenderState3D renderState) {
        NDocElement3DUVSphere ee = (NDocElement3DUVSphere) e;
        NDocPoint3D origin = ee.getOrigin();
        double radiusX = ee.getRadiusX();
        double radiusY = ee.getRadiusY();
        double radiusZ = ee.getRadiusZ();
        int meridians = ee.getMeridians();
        int parallels = ee.getParallels();
        boolean showMesh = ee.isShowMesh();
        NDocMesh3D mesh = new NDocMesh3D();
        mesh.getVertices().add(new NDocPoint3D(origin.x, origin.y + radiusY, origin.z));
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
                mesh.getVertices().add(new NDocPoint3D(x, y, z));
            }
        }
        mesh.getVertices().add(new NDocPoint3D(origin.x, origin.y - radiusY, origin.z));

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

        List<NDocElement3DPrimitive> elements = new ArrayList<>();
        int t = mesh.size();
        HashSet<NDocElement3D> contours = new HashSet<>();
        for (int i = 0; i < t; i++) {
            NDocElement3DTriangle tt = mesh.triangleAt(i);
            tt.setContour(showMesh);
            tt.setFilled(true);
            elements.add(tt);
            NDocPoint3D p1 = tt.getP1();
            NDocPoint3D p2 = tt.getP2();
            NDocPoint3D p3 = tt.getP3();
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
        return elements.toArray(new NDocElement3DPrimitive[0]);
    }
}
