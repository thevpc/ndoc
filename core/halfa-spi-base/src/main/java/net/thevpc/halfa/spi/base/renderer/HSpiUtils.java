package net.thevpc.halfa.spi.base.renderer;

import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererConfig;
import net.thevpc.halfa.spi.renderer.PageOrientation;

public class HSpiUtils {
    public static HDocumentStreamRendererConfig validateConfig(HDocumentStreamRendererConfig config0) {
        HDocumentStreamRendererConfig config = config0 == null ? new HDocumentStreamRendererConfig() : config0.copy();
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
