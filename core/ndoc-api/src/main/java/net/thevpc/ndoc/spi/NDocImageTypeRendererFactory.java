package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.elem2d.NDocImageOptions;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;

public interface NDocImageTypeRendererFactory {
    NCallableSupport<NDocGraphicsImageDrawer> resolveRenderer(NPath path, NDocImageOptions options, NDocGraphics graphics);
}
