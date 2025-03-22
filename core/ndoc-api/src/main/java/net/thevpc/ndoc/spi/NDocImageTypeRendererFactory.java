package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.elem2d.HImageOptions;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;

public interface NDocImageTypeRendererFactory {
    NCallableSupport<NDocGraphicsImageDrawer> resolveRenderer(NPath path, HImageOptions options, NDocGraphics graphics);
}
