package net.thevpc.ntexup.engine.tools;

import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.document.elem2d.NDocPoint2D;
import net.thevpc.ntexup.api.document.elem3d.NDocPoint3D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.engine.NDocEngineTools;
import net.thevpc.ntexup.api.renderer.NDocDocumentStreamRendererConfig;
import net.thevpc.ntexup.engine.renderer.HSpiUtils;
import net.thevpc.ntexup.engine.util.NDocUtilsImages;
import net.thevpc.ntexup.engine.util.NDocUtilsPages;
import net.thevpc.ntexup.engine.util.NDocUtilsPoints;
import net.thevpc.ntexup.engine.util.NDocUtilsText;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class MyNDocEngineTools implements NDocEngineTools {
    private NDocEngine engine;

    public MyNDocEngineTools(NDocEngine engine) {
        this.engine = engine;
    }

    @Override
    public BufferedImage toBufferedImage(Image originalImage) {
        return NDocUtilsImages.toBufferedImage(originalImage);
    }

    @Override
    public Image resizeImage(Image originalImage, int targetWidth, int targetHeight) {
        return NDocUtilsImages.resizeImage(originalImage, targetWidth, targetHeight);
    }

    @Override
    public BufferedImage resizeBufferedImage(BufferedImage img, Dimension size) {
        return NDocUtilsImages.resizeImage(img, size);
    }

    @Override
    public BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH) {
        return NDocUtilsImages.resizeImage(img, newW, newH);
    }

    @Override
    public NDocDocumentStreamRendererConfig validateDocumentStreamRendererConfig(NDocDocumentStreamRendererConfig config) {
        return HSpiUtils.validateDocumentStreamRendererConfig(config);
    }

    public String trimBloc(String code) {
        return NDocUtilsText.trimBloc(code);
    }

    @Override
    public boolean addPoints(NTxNode line, NDocPoint2D[] points) {
        return NDocUtilsPoints.addPoints(line, points);
    }

    @Override
    public boolean addPoints(NTxNode line, NDocPoint3D[] points) {
        return NDocUtilsPoints.addPoints(line, points);
    }

    @Override
    public boolean addPoint(NTxNode line, NDocPoint2D point) {
        return NDocUtilsPoints.addPoint(line, point);
    }

    @Override
    public boolean addPoint(NTxNode line, NDocPoint3D point) {
        return NDocUtilsPoints.addPoint(line, point);
    }

    @Override
    public java.util.List<NTxNode> resolvePages(NDocument document) {
        return NDocUtilsPages.resolvePages(document);
    }

    @Override
    public java.util.List<NTxNode> resolvePages(NTxNode part) {
        return NDocUtilsPages.resolvePages(part);
    }

    @Override
    public void fillPages(NTxNode part, List<NTxNode> all) {
        NDocUtilsPages.fillPages(part, all);
    }




}
