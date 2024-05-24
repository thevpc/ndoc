package net.thevpc.halfa.api.model;

public class Int2 {
    private Integer x;
    private Integer y;

    public Int2(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }
}
