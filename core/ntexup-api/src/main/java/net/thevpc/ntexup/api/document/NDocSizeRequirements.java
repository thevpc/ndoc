package net.thevpc.ntexup.api.document;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;

public class NDocSizeRequirements {
    public double minX;
    public double maxX;
    public double preferredX;
    public double minY;
    public double maxY;
    public double preferredY;

    public NDocSizeRequirements() {

    }

    public NDocSizeRequirements(double minX, double maxX, double preferredX, double minY, double maxY, double preferredY) {
        this.minX = minX;
        this.maxX = maxX;
        this.preferredX = preferredX;
        this.minY = minY;
        this.maxY = maxY;
        this.preferredY = preferredY;
    }

    public NTxBounds2 toBounds2() {
        return new NTxBounds2(
                minX, minY,
                maxX, maxY
        );
    }

    public NDocSizeRequirements(NTxBounds2 b) {
        this.minX = b.getMinX();
        this.maxX = b.getMaxX();
        this.minY = b.getMinY();
        this.maxY = b.getMaxY();
        this.preferredX = b.getMaxY();
        this.preferredY = b.getMaxY();
    }

    @Override
    public String toString() {
        return "HSizeRequirements{"
                + "min=(" + minX +","+minY+")"
                + " max=(" + maxX +","+maxY+")"
                + " pref=(" + preferredX +","+preferredY+")"
                +'}';
    }
}
