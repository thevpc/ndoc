package net.thevpc.halfa.spi;

import net.thevpc.halfa.api.model.elem2d.HImageOptions;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;

public interface HImageTypeRendererFactory {
    NCallableSupport<HGraphicsImageDrawer> resolveRenderer(NPath path, HImageOptions options, HGraphics graphics);
}
