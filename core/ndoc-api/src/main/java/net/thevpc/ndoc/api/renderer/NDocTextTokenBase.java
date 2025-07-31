package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;

public class NDocTextTokenSimple implements NDocTextToken {
    private String value;
    private NDocTextOptions options = new NDocTextOptions();

    public NDocTextTokenSimple(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public NDocTextOptions options() {
        return options;
    }
}
