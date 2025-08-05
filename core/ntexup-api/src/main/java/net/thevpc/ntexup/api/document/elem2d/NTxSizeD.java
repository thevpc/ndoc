package net.thevpc.ntexup.api.document.elem2d;

public class NTxSizeD {
    private double width;
    private double height;

    public NTxSizeD(double width, double height) {
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
