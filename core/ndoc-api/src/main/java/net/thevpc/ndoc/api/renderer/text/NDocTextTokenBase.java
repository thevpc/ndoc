package net.thevpc.ndoc.api.renderer.text;

public abstract class NDocTextTokenBase implements NDocTextToken {
    private NDocTextOptions options = new NDocTextOptions();

    public NDocTextOptions options() {
        return options;
    }
}
