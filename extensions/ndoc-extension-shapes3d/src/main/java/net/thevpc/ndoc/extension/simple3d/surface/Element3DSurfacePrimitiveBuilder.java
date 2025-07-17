package net.thevpc.ndoc.extension.simple3d.surface;

import net.thevpc.ndoc.api.model.elem3d.NDocElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.NDocElement3D;
import net.thevpc.ndoc.api.model.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.model.elem3d.RenderState3D;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DSurface;
import net.thevpc.ndoc.api.model.elem3d.primitives.NDocElement3DTriangle;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.api.util.MinMax;
import net.thevpc.ndoc.spi.NDocElement3DRenderer;
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

public class Element3DSurfacePrimitiveBuilder implements NDocElement3DRenderer {
    @Override
    public Class<? extends NDocElement3D> forType() {
        return NDocElement3DSurface.class;
    }

    @Override
    public NDocElement3DPrimitive[] toPrimitives(NDocElement3D e, RenderState3D renderState) {
        NDocElement3DSurface ee = (NDocElement3DSurface) e;
        NDocPoint3D[] points = ee.getPoints();
        // Perform Delaunay triangulation
        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(Arrays.asList(points).stream().map(x -> new Coordinate(x.x, x.y, x.z)).collect(Collectors.toList()));
        MinMax m = HUtils.minMaxZ(points);
        QuadEdgeSubdivision subdivision = builder.getSubdivision();
        // Get the triangles as JTS Geometry objects
        GeometryCollection triangles = (GeometryCollection) subdivision.getTriangles(new GeometryFactory());
        int numGeometries = triangles.getNumGeometries();
        List<NDocElement3DPrimitive> elements = new ArrayList<>();
        for (int i = 0; i < numGeometries; i++) {
            Polygon geometryN = (Polygon) triangles.getGeometryN(i);
            Coordinate[] coordinates = geometryN.getCoordinates();
            double z = (coordinates[0].z + coordinates[1].z + coordinates[2].z) / 3;
            elements.add(new NDocElement3DTriangle(
                    new NDocPoint3D(coordinates[0].x, coordinates[0].y, coordinates[0].z)
                    , new NDocPoint3D(coordinates[1].x, coordinates[1].y, coordinates[1].z)
                    , new NDocPoint3D(coordinates[2].x, coordinates[2].y, coordinates[2].z)
                    , true, true
            ).setBackgroundPaint(
                    Color.getHSBColor(
                            (float) m.ratio(z),
                            1f,
                            1f
                    )
            ));
        }
        return elements.toArray(new NDocElement3DPrimitive[0]);
    }
}
