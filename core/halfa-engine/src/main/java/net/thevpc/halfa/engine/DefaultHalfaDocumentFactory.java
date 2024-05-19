package net.thevpc.halfa.engine;

import net.thevpc.halfa.HalfaDocumentFactory;
import net.thevpc.halfa.api.HStyles;
import net.thevpc.halfa.api.model.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class DefaultHalfaDocumentFactory implements HalfaDocumentFactory {
    @Override
    public HDocument document() {
        return new DefaultHDocument();
    }

    @Override
    public HPage page() {
        return new DefaultHPage();
    }

    @Override
    public HText text(String hello) {
        return text(0, 0, hello);
    }


    @Override
    public HContainer paragraph(double x, double y, HPagePart... children) {
        return (HContainer) new DefaultHContainer(new ArrayList<>(Arrays.asList(children)))
                .set(HStyles.layout(HLayout.FLOW))
                .set(HStyles.position(x, y))
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.size(100, 100))
                ;
    }

    @Override
    public HContainer container(double x, double y, HPagePart... children) {
        return (HContainer) new DefaultHContainer(new ArrayList<>(Arrays.asList(children)))
                .set(HStyles.layout(HLayout.ABSOLUTE))
                .set(HStyles.position(x, y))
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.size(100, 100))
                ;
    }

    @Override
    public HContainer container(HPagePart... children) {
        return container(0, 0, children);
    }

    @Override
    public HText text(double x, double y, String hello) {
        return new DefaultHText(hello);
    }

    @Override
    public HRectangle rectangle(double x, double y, double width, double height) {
        return (HRectangle) new DefaultHRectangle()
                .set(HStyles.position(x, y))
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HRectangle square(double x, double y, double width) {
        return rectangle(x, y, width, width);
    }

    @Override
    public HEllipse ellipse(double x, double y, double width, double height) {
        return (HEllipse) new DefaultHEllipse()
                .set(HStyles.anchor(HAnchor.CENTER))
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height));
    }

    @Override
    public HEllipse circle(double x, double y, double width) {
        return ellipse(x, y, width, width);
    }

    @Override
    public HPolygon polygon(double x, double y, double width, double height, Point2D.Double... points) {
        return (HPolygon) new DefaultHPolygon(points)
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HPolyline polyline(double x, double y, double width, double height, Point2D.Double... points) {
        return (HPolyline) new DefaultHPolyline(points)
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HLine line(double x, double y, double maxx, double maxy) {
        return (HLine) new DefaultHLine()
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.position(x, y))
                .set(HStyles.size(maxx - x, maxy - y))
                ;
    }

    @Override
    public HImage image(double x, double y, Image image) {
        return (HImage) new DefaultHImage(image)
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HLatexEquation latexEquation(double x, double y, String latex) {
        return (HLatexEquation) new DefaultHLatexEquation(latex)
                .set(HStyles.anchor(HAnchor.NORTH_WEST))
                .set(HStyles.position(x, y))
                ;
    }
}
