package net.thevpc.ndoc.api.renderer.text;


public class NDocTextTokenFlavored extends NDocTextTokenBase {
    private String flavor;
    private String value;

    public NDocTextTokenFlavored(String flavor, String value) {
        this.flavor = flavor;
        this.value = value;
    }

    public String flavor() {
        return flavor;
    }

    public String value() {
        return value;
    }

}
