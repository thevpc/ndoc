package net.thevpc.ndoc.api.model.elem2d;

public class SizeD {
    private double width;
    private double height;

    public SizeD(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "(" +width+","+height+")";
    }
}
