package net.thevpc.halfa.api.style;

public class HStyleAndMagnitude implements Comparable<HStyleAndMagnitude> {
    private HProp style;
    private HStyleMagnitude magnitude;

    public HStyleAndMagnitude(HProp style, HStyleMagnitude magnitude) {
        this.style = style;
        this.magnitude = magnitude;
    }

    public HProp getStyle() {
        return style;
    }

    public HStyleMagnitude getMagnetude() {
        return magnitude;
    }

    @Override
    public int compareTo(HStyleAndMagnitude o) {
        if (this.style.getName() != o.getStyle().getName()) {
            return this.style.getName().compareTo(o.getStyle().getName());
        }
        return this.magnitude.compareTo(o.magnitude);
    }
}
