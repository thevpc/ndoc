package net.thevpc.halfa;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.node.container.HFlowContainer;
import net.thevpc.halfa.api.node.container.HGridContainer;
import net.thevpc.halfa.api.node.container.HStackContainer;
import net.thevpc.halfa.api.node.container.HUnorderedList;

import java.awt.*;
import java.awt.geom.Point2D;

public interface HDocumentFactory {
    HDocument document();

    HPage page();

    HPageGroup pageGroup();

    HNode create(HNodeType type);

    HText text(double x, double y, String hello);

    HText text(String hello);

    HText text();

    HFiller glue();

    HFiller vglue();

    HFiller hglue();

    HFiller vstrut(double w);

    HFiller hstrut(double w);

    HFiller strut(double w, double h);

    HArc arc(double from, double to);

    HArc arc();

    HCtrlAssign assign();
    HFlowContainer flow(HNode... children);

    HFlowContainer flow(double x, double y, HNode... children);

    HStackContainer stack(double x, double y, HNode... children);

    HUnorderedList unorderedList(double x, double y, HNode... children);

    HOrderedList orderedList(double x, double y, HNode... children);

    HUnorderedList unorderedList(HNode... children);

    HOrderedList orderedList(HNode... children);

    HStackContainer stack(HNode... children);

    HGridContainer grid(HNode... children);

    HGridContainer grid(int cols, int rows, HNode... children);

    HGridContainer vgrid(HNode... children);

    HGridContainer hgrid(HNode... children);

    HRectangle rectangle(double x, double y, double width, double height);

    HRectangle rectangle();

    HRectangle square();

    HRectangle square(double width);

    HRectangle square(double x, double y, double width);

    HEllipse ellipse(double x, double y, double width, double height);

    HEllipse ellipse();

    HEllipse circle();

    HEllipse circle(double width);

    HEllipse circle(double x, double y, double width);

    HPolygon polygon(double x, double y, double width, double height, Point2D.Double... points);

    HPolygon polygon(Point2D.Double... points);

    HPolyline polyline(double x, double y, double width, double height, Point2D.Double... points);

    HPolyline polyline(Point2D.Double... points);

    HLine line(double x, double y, double maxx, double maxy);

    HLine line();

    HImage image();

    HImage image(double x, double y, Image image);

    HLatexEquation equation(double x, double y, String latex);

    HLatexEquation equation();
}
