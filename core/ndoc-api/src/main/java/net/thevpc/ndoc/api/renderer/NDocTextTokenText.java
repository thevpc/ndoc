package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;

public class NDocTextTokenPlain implements NDocTextToken {
    private String value;
    private NDocTextOptions options = new NDocTextOptions();

    public NDocTextTokenPlain(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public NDocTextOptions options() {
        return options;
    }
}
