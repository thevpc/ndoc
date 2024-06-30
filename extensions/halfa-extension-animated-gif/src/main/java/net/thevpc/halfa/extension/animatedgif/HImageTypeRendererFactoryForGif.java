package net.thevpc.halfa.extension.animatedgif;

import net.thevpc.halfa.api.model.elem2d.HImageOptions;
import net.thevpc.halfa.spi.HImageTypeRendererFactory;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class HImageTypeRendererFactoryForGif implements HImageTypeRendererFactory {
    private Map<String, FutureTask<NPath>> pendingCache = new HashMap<>();

    @Override
    public NCallableSupport<HGraphicsImageDrawer> resolveRenderer(NPath path, HImageOptions options, HGraphics graphics) {
        if (!options.isDisableAnimation()) {
            if (path.getName().toLowerCase().endsWith(".gif")) {
                return NCallableSupport.of(10,
                        () -> {
                            byte[] b = path.readBytes();
                            return new GifHImageDrawer(b, pendingCache);
                        }
                );
            }
        }
        return NCallableSupport.invalid(session -> NMsg.ofC("not supported %s", path));
    }
}
