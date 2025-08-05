package net.thevpc.ntexup.extension.animatedgif;

import net.thevpc.ntexup.api.document.elem2d.NTxImageOptions;
import net.thevpc.ntexup.api.renderer.NDocImageTypeRendererFactory;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class NDocImageTypeRendererFactoryForGif implements NDocImageTypeRendererFactory {
    private Map<String, FutureTask<NPath>> pendingCache = new HashMap<>();

    @Override
    public NCallableSupport<NDocGraphicsImageDrawer> resolveRenderer(NPath path, NTxImageOptions options, NDocGraphics graphics) {
        if (!options.isDisableAnimation()) {
            if (path.getName().toLowerCase().endsWith(".gif")) {
                return NCallableSupport.ofValid(
                        () -> {
                            byte[] b = path.readBytes();
                            return new GifNDocImageDrawer(b, pendingCache);
                        }
                );
            }
        }
        return NCallableSupport.ofInvalid(() -> NMsg.ofC("not supported %s", path));
    }
}
