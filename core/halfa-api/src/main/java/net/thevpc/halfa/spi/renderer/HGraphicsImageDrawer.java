package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.model.elem2d.HImageOptions;

public interface HGraphicsImageDrawer {
    void drawImage(double x, double y, HImageOptions options, HGraphics graphics);
}
