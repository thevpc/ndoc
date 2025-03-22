package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.model.elem2d.HImageOptions;

public interface NDocGraphicsImageDrawer {
    void drawImage(double x, double y, HImageOptions options, NDocGraphics graphics);
}
