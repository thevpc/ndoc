package net.thevpc.ntexup.api.document.style;

import java.util.Objects;

public class NTxStyleAndMagnitude implements Comparable<NTxStyleAndMagnitude> {
    private NTxProp style;
    private NTxStyleMagnitude magnitude;

    public NTxStyleAndMagnitude(NTxProp style, NTxStyleMagnitude magnitude) {
        this.style = style;
        this.magnitude = magnitude;
    }

    public NTxProp getStyle() {
        return style;
    }

    public NTxStyleMagnitude getMagnetude() {
        return magnitude;
    }

    @Override
    public int compareTo(NTxStyleAndMagnitude o) {
        if (!Objects.equals(this.style.getName(), o.getStyle().getName())) {
            return this.style.getName().compareTo(o.getStyle().getName());
        }
        return this.magnitude.compareTo(o.magnitude);
    }
}
