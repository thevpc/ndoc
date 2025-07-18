package net.thevpc.ndoc.api.style;

import java.util.Objects;

public class HStyleAndMagnitude implements Comparable<HStyleAndMagnitude> {
    private NDocProp style;
    private HStyleMagnitude magnitude;

    public HStyleAndMagnitude(NDocProp style, HStyleMagnitude magnitude) {
        this.style = style;
        this.magnitude = magnitude;
    }

    public NDocProp getStyle() {
        return style;
    }

    public HStyleMagnitude getMagnetude() {
        return magnitude;
    }

    @Override
    public int compareTo(HStyleAndMagnitude o) {
        if (!Objects.equals(this.style.getName(), o.getStyle().getName())) {
            return this.style.getName().compareTo(o.getStyle().getName());
        }
        return this.magnitude.compareTo(o.magnitude);
    }
}
