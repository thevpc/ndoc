package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;

public abstract class NDocTextTokenBase implements NDocTextToken {
    private NDocTextOptions options = new NDocTextOptions();

    public NDocTextOptions options() {
        return options;
    }
}
