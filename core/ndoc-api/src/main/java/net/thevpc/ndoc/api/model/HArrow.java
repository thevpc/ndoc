package net.thevpc.ndoc.api.model;

public class HArrow {
    private HArrowType type;
    private double width;
    private double height;

    public HArrow() {
        this(HArrowType.DEFAULT);
    }

    public HArrow(HArrowType type) {
        this(type, 0,0);
    }

    public HArrow(HArrowType type, double width, double height) {
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public HArrowType getType() {
        return type;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
