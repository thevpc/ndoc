package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.elem2d.NTxImageOptions;

public interface NTxGraphicsImageDrawer {
    void drawImage(double x, double y, NTxImageOptions options, NTxGraphics graphics);
}
