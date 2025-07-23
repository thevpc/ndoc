package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.elem2d.NDocImageOptions;

public interface NDocGraphicsImageDrawer {
    void drawImage(double x, double y, NDocImageOptions options, NDocGraphics graphics);
}
