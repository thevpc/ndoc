package net.thevpc.halfa;

import net.thevpc.halfa.api.model.*;

import java.awt.*;
import java.awt.geom.Point2D;

public interface HalfaDocumentFactory {
    HDocument document();

    HPage page();

    HText text(double x, double y, String hello);

    HText text(String hello);

    HContainer paragraph(double x, double y, HPagePart... children);

    HContainer container(double x, double y, HPagePart... children);

    HContainer container(HPagePart... children);

    HRectangle rectangle(double x, double y, double width, double height);

    HRectangle square(double x, double y, double width);

    HEllipse ellipse(double x, double y, double width, double height);

    HEllipse circle(double x, double y, double width);

    HPolygon polygon(double x, double y, double width, double height, Point2D.Double... points);

    HPolyline polyline(double x, double y, double width, double height, Point2D.Double... points);

    HLine line(double x, double y, double maxx, double maxy);


    HImage image(double x, double y, Image image);

    HLatexEquation latexEquation(double x, double y, String latex);
}
