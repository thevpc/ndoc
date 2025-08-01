package net.thevpc.ndoc.api.renderer.text;

public class NDocTextTokenText extends NDocTextTokenBase {
    private String value;

    public NDocTextTokenText(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
