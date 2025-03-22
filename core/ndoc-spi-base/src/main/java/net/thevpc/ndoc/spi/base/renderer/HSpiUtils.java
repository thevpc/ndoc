package net.thevpc.ndoc.spi.base.renderer;

import net.thevpc.ndoc.spi.renderer.NDocDocumentStreamRendererConfig;
import net.thevpc.ndoc.spi.renderer.PageOrientation;

public class HSpiUtils {
    public static NDocDocumentStreamRendererConfig validateConfig(NDocDocumentStreamRendererConfig config0) {
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
            config.setOrientation(PageOrientation.LANDSCAPE);
        }
        return config;
    }
}
