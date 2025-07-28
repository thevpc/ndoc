package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.elem2d.NDocImageOptions;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;

public interface NDocImageTypeRendererFactory {
    NCallableSupport<NDocGraphicsImageDrawer> resolveRenderer(NPath path, NDocImageOptions options, NDocGraphics graphics);
}
