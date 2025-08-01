package net.thevpc.ndoc.api.engine;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererConfig;

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
    boolean addPoints(NDocNode line, NDocPoint2D[] points);

    boolean addPoints(NDocNode line, NDocPoint3D[] points);

    boolean addPoint(NDocNode line, NDocPoint2D point);

    boolean addPoint(NDocNode line, NDocPoint3D point);

    java.util.List<NDocNode> resolvePages(NDocument document);

    java.util.List<NDocNode> resolvePages(NDocNode part);

    void fillPages(NDocNode part, List<NDocNode> all);
}
