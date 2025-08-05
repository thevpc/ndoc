package net.thevpc.ntexup.api.engine;

import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.document.elem2d.NDocPoint2D;
import net.thevpc.ntexup.api.document.elem3d.NDocPoint3D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocDocumentStreamRendererConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public interface NDocEngineTools {
    BufferedImage toBufferedImage(Image originalImage);

    Image resizeImage(Image originalImage, int targetWidth, int targetHeight);

    BufferedImage resizeBufferedImage(BufferedImage img, Dimension size);

    BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH);

    NDocDocumentStreamRendererConfig validateDocumentStreamRendererConfig(NDocDocumentStreamRendererConfig config);

    String trimBloc(String code);
    boolean addPoints(NTxNode line, NDocPoint2D[] points);

    boolean addPoints(NTxNode line, NDocPoint3D[] points);

    boolean addPoint(NTxNode line, NDocPoint2D point);

    boolean addPoint(NTxNode line, NDocPoint3D point);

    java.util.List<NTxNode> resolvePages(NDocument document);

    java.util.List<NTxNode> resolvePages(NTxNode part);

    void fillPages(NTxNode part, List<NTxNode> all);
}
