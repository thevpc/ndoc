package net.thevpc.ndoc.api.document;

public class NDocArrow {
    private NDocArrowType type;
    private double width;
    private double height;

    public NDocArrow() {
        this(NDocArrowType.DEFAULT);
    }

    public NDocArrow(NDocArrowType type) {
        this(type, 0,0);
    }

    public NDocArrow(NDocArrowType type, double width, double height) {
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public NDocArrowType getType() {
        return type;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
