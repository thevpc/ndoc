package net.thevpc.halfa.spi.renderer;

import net.thevpc.nuts.util.NOptional;

public interface HNodeRendererManager {

    NOptional<HNodeRenderer> getRenderer(String type);

}
