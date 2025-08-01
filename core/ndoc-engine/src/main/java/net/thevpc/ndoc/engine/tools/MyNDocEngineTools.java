package net.thevpc.ndoc.engine.tools;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem2d.SizeD;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocEngineTools;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererConfig;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.tools.util.*;
import net.thevpc.ndoc.engine.renderer.HSpiUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

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
        return NDocUtilsImages.resize(img, size);
    }

    @Override
    public BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH) {
        return NDocUtilsImages.resize(img, newW, newH);
    }

    @Override
    public NDocDocumentStreamRendererConfig validateDocumentStreamRendererConfig(NDocDocumentStreamRendererConfig config) {
        return HSpiUtils.validateDocumentStreamRendererConfig(config);
    }

    public String trimBloc(String code) {
        return NDocUtilsText.trimBloc(code);
    }

    @Override
    public boolean addPoints(NDocNode line, NDocPoint2D[] points) {
        return NDocUtilsPoints.addPoints(line, points);
    }

    @Override
    public boolean addPoints(NDocNode line, NDocPoint3D[] points) {
        return NDocUtilsPoints.addPoints(line, points);
    }

    @Override
    public boolean addPoint(NDocNode line, NDocPoint2D point) {
        return NDocUtilsPoints.addPoint(line, point);
    }

    @Override
    public boolean addPoint(NDocNode line, NDocPoint3D point) {
        return NDocUtilsPoints.addPoint(line, point);
    }

    @Override
    public java.util.List<NDocNode> resolvePages(NDocument document) {
        return NDocUtilsPages.resolvePages(document);
    }

    @Override
    public java.util.List<NDocNode> resolvePages(NDocNode part) {
        return NDocUtilsPages.resolvePages(part);
    }

    @Override
    public void fillPages(NDocNode part, List<NDocNode> all) {
        NDocUtilsPages.fillPages(part, all);
    }




}
