package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.elem2d.NTxImageOptions;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;

public interface NDocImageTypeRendererFactory {
    NCallableSupport<NDocGraphicsImageDrawer> resolveRenderer(NPath path, NTxImageOptions options, NDocGraphics graphics);
}
