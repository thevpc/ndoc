package net.thevpc.ntexup.api.document;

public class NTxArrow {
    private NTxArrowType type;
    private double width;
    private double height;

    public NTxArrow() {
        this(NTxArrowType.DEFAULT);
    }

    public NTxArrow(NTxArrowType type) {
        this(type, 0,0);
    }

    public NTxArrow(NTxArrowType type, double width, double height) {
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public NTxArrowType getType() {
        return type;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
