package net.thevpc.ntexup.extension.simple3d.surface;

import net.thevpc.ntexup.api.document.elem3d.NtxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NTxPoint3D;
import net.thevpc.ntexup.api.document.elem3d.NTxRenderState3D;
import net.thevpc.ntexup.api.document.elem3d.composite.NtxElement3DSurface;
import net.thevpc.ntexup.api.document.elem3d.primitives.NtxElement3DTriangle;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.ntexup.api.util.NTxMinMax;
import net.thevpc.ntexup.api.renderer.NTxElement3DRenderer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.quadedge.QuadEdgeSubdivision;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Element3DSurfacePrimitiveBuilder implements NTxElement3DRenderer {
    @Override
    public Class<? extends NtxElement3D> forType() {
        return NtxElement3DSurface.class;
    }

    @Override
    public NtxElement3DPrimitive[] toPrimitives(NtxElement3D e, NTxRenderState3D renderState) {
        NtxElement3DSurface ee = (NtxElement3DSurface) e;
        NTxPoint3D[] points = ee.getPoints();
        // Perform Delaunay triangulation
        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(Arrays.asList(points).stream().map(x -> new Coordinate(x.x, x.y, x.z)).collect(Collectors.toList()));
        NTxMinMax m = NTxUtils.minMaxZ(points);
        QuadEdgeSubdivision subdivision = builder.getSubdivision();
        // Get the triangles as JTS Geometry objects
        GeometryCollection triangles = (GeometryCollection) subdivision.getTriangles(new GeometryFactory());
        int numGeometries = triangles.getNumGeometries();
        List<NtxElement3DPrimitive> elements = new ArrayList<>();
        for (int i = 0; i < numGeometries; i++) {
            Polygon geometryN = (Polygon) triangles.getGeometryN(i);
            Coordinate[] coordinates = geometryN.getCoordinates();
            double z = (coordinates[0].z + coordinates[1].z + coordinates[2].z) / 3;
            elements.add(new NtxElement3DTriangle(
                    new NTxPoint3D(coordinates[0].x, coordinates[0].y, coordinates[0].z)
                    , new NTxPoint3D(coordinates[1].x, coordinates[1].y, coordinates[1].z)
                    , new NTxPoint3D(coordinates[2].x, coordinates[2].y, coordinates[2].z)
                    , true, true
            ).setBackgroundPaint(
                    Color.getHSBColor(
                            (float) m.ratio(z),
                            1f,
                            1f
                    )
            ));
        }
        return elements.toArray(new NtxElement3DPrimitive[0]);
    }
}
