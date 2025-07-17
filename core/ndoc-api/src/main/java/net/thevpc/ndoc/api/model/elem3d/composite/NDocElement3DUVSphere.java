package net.thevpc.ndoc.api.model.elem3d.composite;

import net.thevpc.ndoc.api.model.elem3d.*;

public class NDocElement3DUVSphere extends AbstractElement3D {
    private NDocPoint3D origin;
    private int meridians;
    private int parallels;
    private double radiusX;
    private double radiusY;
    private double radiusZ;
    private boolean showMesh = true;

    public NDocElement3DUVSphere(NDocPoint3D origin
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

    public NDocPoint3D getOrigin() {
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
