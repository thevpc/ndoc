package net.thevpc.ndoc.api.model.elem3d.composite;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3D;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;

public class Element3DBox extends AbstractElement3D {
    private HPoint3D origin;
    private double sizeX;
    private double sizeY;
    private double sizeZ;

    public Element3DBox(HPoint3D origin, double sizeX, double sizeY, double sizeZ) {
        this.origin = origin;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public HPoint3D getOrigin() {
        return origin;
    }

    public double getSizeX() {
        return sizeX;
    }

    public double getSizeY() {
        return sizeY;
    }

    public double getSizeZ() {
        return sizeZ;
    }

}
