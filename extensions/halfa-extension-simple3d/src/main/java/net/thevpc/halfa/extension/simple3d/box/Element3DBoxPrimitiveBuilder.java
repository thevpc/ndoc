package net.thevpc.halfa.extension.simple3d.box;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DBox;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DLine;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DPolygon;
import net.thevpc.halfa.spi.HNode3DSimplifier;

import java.util.ArrayList;
import java.util.List;

public class Element3DBoxPrimitiveBuilder implements HNode3DSimplifier {
    @Override
    public Class<? extends HElement3D> forType() {
        return Element3DBox.class;
    }

    @Override
    public HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState) {
        Element3DBox ee = (Element3DBox) e;
        HPoint3D origin = ee.getOrigin();
        double sizeX = ee.getSizeX();
        double sizeY = ee.getSizeY();
        double sizeZ = ee.getSizeZ();
        HPoint3D[] vertices = {
                new HPoint3D(origin.x, origin.y, origin.z),
                new HPoint3D(origin.x + sizeX, origin.y, origin.z),
                new HPoint3D(origin.x + sizeX, origin.y + sizeY, origin.z),
                new HPoint3D(origin.x, origin.y + sizeY, origin.z),
                new HPoint3D(origin.x, origin.y, origin.z + sizeZ),
                new HPoint3D(origin.x + sizeX, origin.y, origin.z + sizeZ),
                new HPoint3D(origin.x + sizeX, origin.y + sizeY, origin.z + sizeZ),
                new HPoint3D(origin.x, origin.y + sizeY, origin.z + sizeZ)
        };
        // Define the edges of the cube
        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0}, // back face
                {4, 5}, {5, 6}, {6, 7}, {7, 4}, // front face
                {0, 4}, {1, 5}, {2, 6}, {3, 7}  // connecting edges
        };
        int[][] surfaces = {
                {0, 1, 2, 3}, //FRONT
                {4, 5, 6, 7}, // BACK
                {0, 4, 5, 1}, //TOP
                {3, 2, 6, 7}, //BOTTOM
                {1, 2, 6, 5}, //RIGHT
                {0, 3, 7, 4}, //LEFT
        };
        List<HElement3DPrimitive> elements = new ArrayList<>();
        for (int i = 0; i < surfaces.length; i++) {
            int[] surface = surfaces[i];
            HPoint3D[] points = new HPoint3D[surface.length];
            for (int j = 0; j < points.length; j++) {
                points[j] = vertices[surface[j]];
            }
            elements.add(new Element3DPolygon(points, true, false));
        }
        for (int i = 0; i < edges.length; i++) {
            int[] edge = edges[i];
            elements.add(new Element3DLine(vertices[edge[0]], vertices[edge[1]]));
        }
        return elements.toArray(new HElement3DPrimitive[0]);
    }
}
