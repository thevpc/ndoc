package net.thevpc.ndoc.api.engine;

import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererConfig;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface NDocEngineTools {
    BufferedImage toBufferedImage(Image originalImage);

    Image resizeImage(Image originalImage, int targetWidth, int targetHeight);

    BufferedImage resizeBufferedImage(BufferedImage img, Dimension size);

    BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH);

    NDocDocumentStreamRendererConfig validateDocumentStreamRendererConfig(NDocDocumentStreamRendererConfig config);
}
