package net.thevpc.ndoc.extension.animatedgif;

import net.thevpc.ndoc.api.model.elem2d.HImageOptions;
import net.thevpc.ndoc.spi.NDocImageTypeRendererFactory;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class NDocImageTypeRendererFactoryForGif implements NDocImageTypeRendererFactory {
    private Map<String, FutureTask<NPath>> pendingCache = new HashMap<>();

    @Override
    public NCallableSupport<NDocGraphicsImageDrawer> resolveRenderer(NPath path, HImageOptions options, NDocGraphics graphics) {
        if (!options.isDisableAnimation()) {
            if (path.getName().toLowerCase().endsWith(".gif")) {
                return NCallableSupport.of(10,
                        () -> {
                            byte[] b = path.readBytes();
                            return new GifNDocImageDrawer(b, pendingCache);
                        }
                );
            }
        }
        return NCallableSupport.invalid(() -> NMsg.ofC("not supported %s", path));
    }
}
