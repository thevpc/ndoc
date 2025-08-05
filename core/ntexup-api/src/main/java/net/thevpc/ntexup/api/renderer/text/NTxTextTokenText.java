package net.thevpc.ntexup.api.renderer.text;

public class NTxTextTokenText extends NTxTextTokenBase {
    private String value;

    public NTxTextTokenText(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
