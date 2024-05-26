package net.thevpc.halfa.spi.model;

import net.thevpc.halfa.api.model.Bounds2;

public class HSizeRequirements {
    public double minX;
    public double maxX;
    public double preferredX;
    public double minY;
    public double maxY;
    public double preferredY;

    public HSizeRequirements() {

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
