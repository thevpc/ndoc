package net.thevpc.ntexup.api.renderer.text;

public abstract class NTxTextTokenBase implements NTxTextToken {
    private NTxTextOptions options = new NTxTextOptions();

    public NTxTextOptions options() {
        return options;
    }
}
