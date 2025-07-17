package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.model.elem2d.NDocImageOptions;

public interface NDocGraphicsImageDrawer {
    void drawImage(double x, double y, NDocImageOptions options, NDocGraphics graphics);
}
