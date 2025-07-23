package net.thevpc.ndoc.api.document.elem3d.composite;

import net.thevpc.ndoc.api.document.elem3d.AbstractElement3D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;

public class NDocElement3DBox extends AbstractElement3D {
    private NDocPoint3D origin;
    private double sizeX;
    private double sizeY;
    private double sizeZ;

    public NDocElement3DBox(NDocPoint3D origin, double sizeX, double sizeY, double sizeZ) {
        this.origin = origin;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public NDocPoint3D getOrigin() {
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
