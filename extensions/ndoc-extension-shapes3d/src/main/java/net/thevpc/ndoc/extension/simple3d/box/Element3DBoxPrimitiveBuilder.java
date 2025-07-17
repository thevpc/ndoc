package net.thevpc.ndoc.extension.simple3d.box;

import net.thevpc.ndoc.api.model.elem3d.NDocElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.NDocElement3D;
import net.thevpc.ndoc.api.model.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.model.elem3d.RenderState3D;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DBox;
import net.thevpc.ndoc.api.model.elem3d.primitives.NDocElement3DLine;
import net.thevpc.ndoc.api.model.elem3d.primitives.NDocElement3DPolygon;
import net.thevpc.ndoc.spi.NDocElement3DRenderer;

import java.util.ArrayList;
import java.util.List;

public class Element3DBoxPrimitiveBuilder implements NDocElement3DRenderer {
    @Override
    public Class<? extends NDocElement3D> forType() {
        return NDocElement3DBox.class;
    }

    @Override
    public NDocElement3DPrimitive[] toPrimitives(NDocElement3D e, RenderState3D renderState) {
        NDocElement3DBox ee = (NDocElement3DBox) e;
        NDocPoint3D origin = ee.getOrigin();
        double sizeX = ee.getSizeX();
        double sizeY = ee.getSizeY();
        double sizeZ = ee.getSizeZ();
        NDocPoint3D[] vertices = {
                new NDocPoint3D(origin.x, origin.y, origin.z),
                new NDocPoint3D(origin.x + sizeX, origin.y, origin.z),
                new NDocPoint3D(origin.x + sizeX, origin.y + sizeY, origin.z),
                new NDocPoint3D(origin.x, origin.y + sizeY, origin.z),
                new NDocPoint3D(origin.x, origin.y, origin.z + sizeZ),
                new NDocPoint3D(origin.x + sizeX, origin.y, origin.z + sizeZ),
                new NDocPoint3D(origin.x + sizeX, origin.y + sizeY, origin.z + sizeZ),
                new NDocPoint3D(origin.x, origin.y + sizeY, origin.z + sizeZ)
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
        List<NDocElement3DPrimitive> elements = new ArrayList<>();
        for (int i = 0; i < surfaces.length; i++) {
            int[] surface = surfaces[i];
            NDocPoint3D[] points = new NDocPoint3D[surface.length];
            for (int j = 0; j < points.length; j++) {
                points[j] = vertices[surface[j]];
            }
            elements.add(new NDocElement3DPolygon(points, true, false));
        }
        for (int i = 0; i < edges.length; i++) {
            int[] edge = edges[i];
            elements.add(new NDocElement3DLine(vertices[edge[0]], vertices[edge[1]]));
        }
        return elements.toArray(new NDocElement3DPrimitive[0]);
    }
}
