package net.thevpc.halfa.api.model.elem3d.composite;

import net.thevpc.halfa.api.model.elem3d.*;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DTriangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Element3DUVSphere extends AbstractElement3D {
    private HPoint3D origin;
    private int meridians;
    private int parallels;
    private double radiusX;
    private double radiusY;
    private double radiusZ;
    private boolean showMesh = true;

    public Element3DUVSphere(HPoint3D origin
            , double radiusX
            , double radiusY
            , double radiusZ
            , int meridians, int parallels) {
        this.origin = origin;
        this.parallels = parallels;
        this.meridians = meridians;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
    }

    public HPoint3D getOrigin() {
        return origin;
    }

    public int getMeridians() {
        return meridians;
    }

    public int getParallels() {
        return parallels;
    }

    public double getRadiusX() {
        return radiusX;
    }

    public double getRadiusY() {
        return radiusY;
    }

    public double getRadiusZ() {
        return radiusZ;
    }

    public boolean isShowMesh() {
        return showMesh;
    }

}
