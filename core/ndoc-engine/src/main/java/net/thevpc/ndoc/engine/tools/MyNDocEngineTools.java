package net.thevpc.ndoc.engine.tools;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocEngineTools;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererConfig;
import net.thevpc.ndoc.engine.renderer.HImageUtils;
import net.thevpc.ndoc.engine.renderer.HSpiUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MyNDocEngineTools implements NDocEngineTools {
    private NDocEngine engine;

    public MyNDocEngineTools(NDocEngine engine) {
        this.engine = engine;
    }

    @Override
    public BufferedImage toBufferedImage(Image originalImage) {
        return HImageUtils.toBufferedImage(originalImage);
    }

    @Override
    public Image resizeImage(Image originalImage, int targetWidth, int targetHeight) {
        return HImageUtils.resizeImage(originalImage, targetWidth, targetHeight);
    }

    @Override
    public BufferedImage resizeBufferedImage(BufferedImage img, Dimension size) {
        return HImageUtils.resize(img, size);
    }

    @Override
    public BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH) {
        return HImageUtils.resize(img, newW, newH);
    }

    @Override
    public NDocDocumentStreamRendererConfig validateDocumentStreamRendererConfig(NDocDocumentStreamRendererConfig config) {
        return HSpiUtils.validateDocumentStreamRendererConfig(config);
    }
}
