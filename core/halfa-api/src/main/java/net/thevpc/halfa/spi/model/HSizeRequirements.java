package net.thevpc.halfa.spi.model;

import net.thevpc.halfa.api.model.elem2d.Bounds2;

public class HSizeRequirements {
    public double minX;
    public double maxX;
    public double preferredX;
    public double minY;
    public double maxY;
    public double preferredY;

    public HSizeRequirements() {

    }

    public HSizeRequirements(double minX, double maxX, double preferredX, double minY, double maxY, double preferredY) {
        this.minX = minX;
        this.maxX = maxX;
        this.preferredX = preferredX;
        this.minY = minY;
        this.maxY = maxY;
        this.preferredY = preferredY;
    }

    public Bounds2 toBounds2() {
        return new Bounds2(
                minX, minY,
                maxX,maxY
        );
    }

    public HSizeRequirements(Bounds2 b) {
        this.minX = b.getMinX();
        this.maxX = b.getMaxX();
        this.minY = b.getMinY();
        this.maxY = b.getMaxY();
        this.preferredX = b.getMaxY();
        this.preferredY = b.getMaxY();
    }

}
