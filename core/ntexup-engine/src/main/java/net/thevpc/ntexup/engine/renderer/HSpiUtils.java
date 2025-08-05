package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.renderer.NTxDocumentStreamRendererConfig;
import net.thevpc.ntexup.api.renderer.NTxPageOrientation;

public class HSpiUtils {
    public static NTxDocumentStreamRendererConfig validateDocumentStreamRendererConfig(NTxDocumentStreamRendererConfig config0) {
        NTxDocumentStreamRendererConfig config = config0 == null ? new NTxDocumentStreamRendererConfig() : config0.copy();
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
            config.setOrientation(NTxPageOrientation.LANDSCAPE);
        }
        return config;
    }
}
