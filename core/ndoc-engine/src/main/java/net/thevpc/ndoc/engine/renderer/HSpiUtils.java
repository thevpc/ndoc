package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererConfig;
import net.thevpc.ndoc.api.renderer.NDocPageOrientation;

public class HSpiUtils {
    public static NDocDocumentStreamRendererConfig validateDocumentStreamRendererConfig(NDocDocumentStreamRendererConfig config0) {
        NDocDocumentStreamRendererConfig config = config0 == null ? new NDocDocumentStreamRendererConfig() : config0.copy();
        int imagesPerRow = config.getGridX();
        if (imagesPerRow <= 0) {
            imagesPerRow = 1;
        }
        int imagesPerColumn = config.getGridY();
        if (imagesPerColumn <= 0) {
            imagesPerColumn = 1;
        }
        config.setGridX(imagesPerRow);
        config.setGridY(imagesPerColumn);
        if (config.getOrientation() == null) {
            config.setOrientation(NDocPageOrientation.LANDSCAPE);
        }
        return config;
    }
}
