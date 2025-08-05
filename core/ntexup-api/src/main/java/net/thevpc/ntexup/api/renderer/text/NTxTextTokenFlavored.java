package net.thevpc.ntexup.api.renderer.text;


public class NTxTextTokenFlavored extends NTxTextTokenBase {
    private String flavor;
    private String value;

    public NTxTextTokenFlavored(String flavor, String value) {
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
