package net.thevpc.ndoc.api.renderer;

public class NDocTextTokenCustom implements NDocTextToken {
    private String flavor;
    private String value;

    public NDocTextTokenCustom(String flavor, String value) {
        this.flavor = flavor;
        this.value = value;
    }

    public String getFlavor() {
        return flavor;
    }

    public String getValue() {
        return value;
    }
}
