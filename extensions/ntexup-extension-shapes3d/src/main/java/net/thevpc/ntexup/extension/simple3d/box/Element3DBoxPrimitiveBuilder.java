package net.thevpc.ntexup.extension.simple3d.box;

import net.thevpc.ntexup.api.document.elem3d.NtxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NTxPoint3D;
import net.thevpc.ntexup.api.document.elem3d.NTxRenderState3D;
import net.thevpc.ntexup.api.document.elem3d.composite.NtxElement3DBox;
import net.thevpc.ntexup.api.document.elem3d.primitives.NtxElement3DLine;
import net.thevpc.ntexup.api.document.elem3d.primitives.NtxElement3DPolygon;
import net.thevpc.ntexup.api.renderer.NTxElement3DRenderer;

import java.util.ArrayList;
import java.util.List;

public class Element3DBoxPrimitiveBuilder implements NTxElement3DRenderer {
    @Override
    public Class<? extends NtxElement3D> forType() {
        return NtxElement3DBox.class;
    }

    @Override
    public NtxElement3DPrimitive[] toPrimitives(NtxElement3D e, NTxRenderState3D renderState) {
        NtxElement3DBox ee = (NtxElement3DBox) e;
        NTxPoint3D origin = ee.getOrigin();
        double sizeX = ee.getSizeX();
        double sizeY = ee.getSizeY();
        double sizeZ = ee.getSizeZ();
        NTxPoint3D[] vertices = {
                new NTxPoint3D(origin.x, origin.y, origin.z),
                new NTxPoint3D(origin.x + sizeX, origin.y, origin.z),
                new NTxPoint3D(origin.x + sizeX, origin.y + sizeY, origin.z),
                new NTxPoint3D(origin.x, origin.y + sizeY, origin.z),
                new NTxPoint3D(origin.x, origin.y, origin.z + sizeZ),
                new NTxPoint3D(origin.x + sizeX, origin.y, origin.z + sizeZ),
                new NTxPoint3D(origin.x + sizeX, origin.y + sizeY, origin.z + sizeZ),
                new NTxPoint3D(origin.x, origin.y + sizeY, origin.z + sizeZ)
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
        List<NtxElement3DPrimitive> elements = new ArrayList<>();
        for (int i = 0; i < surfaces.length; i++) {
            int[] surface = surfaces[i];
            NTxPoint3D[] points = new NTxPoint3D[surface.length];
            for (int j = 0; j < points.length; j++) {
                points[j] = vertices[surface[j]];
            }
            elements.add(new NtxElement3DPolygon(points, true, false));
        }
        for (int i = 0; i < edges.length; i++) {
            int[] edge = edges[i];
            elements.add(new NtxElement3DLine(vertices[edge[0]], vertices[edge[1]]));
        }
        return elements.toArray(new NtxElement3DPrimitive[0]);
    }
}
