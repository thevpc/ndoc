package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.node.container.HFlowContainer;
import net.thevpc.halfa.api.node.container.HGridContainer;
import net.thevpc.halfa.api.node.container.HStackContainer;
import net.thevpc.halfa.api.node.container.HUnorderedList;
import net.thevpc.halfa.engine.document.DefaultHDocument;
import net.thevpc.halfa.engine.document.HPageGroupImpl;
import net.thevpc.halfa.engine.document.HPageImpl;
import net.thevpc.halfa.engine.nodes.container.*;
import net.thevpc.halfa.engine.nodes.filler.HFillerImpl;
import net.thevpc.halfa.engine.nodes.image.DefaultHImage;
import net.thevpc.halfa.engine.nodes.shape.*;
import net.thevpc.halfa.engine.nodes.text.DefaultHText;
import net.thevpc.halfa.engine.nodes.text.HLatexEquationImpl;
import net.thevpc.halfa.engine.styles.HDocumentRootRules;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class HDocumentFactoryImpl implements HDocumentFactory {
    @Override
    public HDocument document() {
        DefaultHDocument d = new DefaultHDocument();
        d.root().addRules(HDocumentRootRules.DEFAULT);
        return d;
    }

    @Override
    public HPage page() {
        return new HPageImpl();
    }

    @Override
    public HPageGroup pageGroup() {
        return new HPageGroupImpl();
    }


    @Override
    public HFiller glue() {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMaxParent()),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMaxParent())
                )
        );
    }

    @Override
    public HFiller vglue() {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMin()),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMaxParent())
                )
        );
    }

    @Override
    public HFiller hglue() {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMaxParent()),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMin())
                )
        );
    }

    @Override
    public HFiller vstrut(double w) {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofParent(w), XLen.ofParent(w), XLen.ofParent(w)),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMaxParent())
                )
        );
    }

    @Override
    public HFiller hstrut(double w) {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMaxParent()),
                        new YLenConstraints(YLen.ofParent(w), YLen.ofParent(w), YLen.ofParent(w))
                )
        );
    }

    @Override
    public HFiller strut(double w, double h) {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofParent(w), XLen.ofParent(w), XLen.ofParent(w)),
                        new YLenConstraints(YLen.ofParent(h), YLen.ofParent(h), YLen.ofParent(h))
                )
        );
    }

    @Override
    public HArc arc(double from, double to) {
        return new HArcImpl(
                from, to
        );
    }

    @Override
    public HFlowContainer flow(double x, double y, HNode... children) {
        return (HFlowContainer) flow(children)
                .set(HStyles.position(x, y))
                ;
    }
    @Override
    public HFlowContainer flow(HNode... children) {
        return (HFlowContainer) new HFlowContainerImpl(new ArrayList<>(Arrays.asList(children)))
                ;
    }

    @Override
    public HStackContainer stack(HNode... children) {
        return new HStackContainerImpl(new ArrayList<>(Arrays.asList(children)))
                ;
    }

    @Override
    public HStackContainer stack(double x, double y, HNode... children) {
        return (HStackContainer) stack(children)
                .set(HStyles.position(x, y));
    }

    @Override
    public HUnorderedList unorderedList(double x, double y, HNode... children) {
        return (HUnorderedList) unorderedList(children)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HOrderedList orderedList(double x, double y, HNode... children) {
        return (HOrderedList) orderedList(children)
                .set(HStyles.position(x, y));
    }

    @Override
    public HUnorderedList unorderedList(HNode... children) {
        return new HUnorderedListImpl(new ArrayList<>(Arrays.asList(children)));
    }

    @Override
    public HOrderedList orderedList(HNode... children) {
        return new HOrderedListImpl(new ArrayList<>(Arrays.asList(children)));
    }


    @Override
    public HGridContainer grid(int cols, int rows, HNode... children) {
        return (HGridContainer) grid(children)
                .set(HStyles.columns(cols))
                .set(HStyles.rows(rows))
                ;
    }
    @Override
    public HGridContainer grid(HNode... children) {
        return new HGridContainerImpl(new ArrayList<>(Arrays.asList(children)))
                ;
    }

    @Override
    public HGridContainer vgrid(HNode... children) {
        return grid(1, -1, children)
                ;
    }

    @Override
    public HGridContainer hgrid(HNode... children) {
        return grid(-1, 1, children);
    }

    @Override
    public HText text(double x, double y, String hello) {
        return (HText) text(hello)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HText text(String hello) {
        return new DefaultHText(hello);
    }

    @Override
    public HRectangle rectangle(double x, double y, double width, double height) {
        return (HRectangle) new HRectangleImpl()
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HRectangle rectangle() {
        return new HRectangleImpl();
    }

    @Override
    public HRectangle square(double x, double y, double width) {
        return (HRectangle) rectangle(x, y, width, width)
                .set(HStyles.preserveShapeRatio(true))
                ;
    }

    @Override
    public HRectangle square(double width) {
        return (HRectangle) square()
                .set(HStyles.size(width, width))
                ;
    }

    @Override
    public HRectangle square() {
        return (HRectangle) rectangle()
                .set(HStyles.preserveShapeRatio(true))
                ;
    }

    @Override
    public HEllipse ellipse(double x, double y, double width, double height) {
        return (HEllipse) ellipse()
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height));
    }

    @Override
    public HEllipse ellipse() {
        return (HEllipse) new HEllipseImpl()
                ;
    }


    @Override
    public HEllipse circle() {
        return (HEllipse) new HEllipseImpl()
                .set(HStyles.preserveShapeRatio(true))
                ;
    }

    @Override
    public HEllipse circle(double width) {
        return (HEllipse) circle()
                .set(HStyles.size(width, width))
                ;
    }

    @Override
    public HEllipse circle(double x, double y, double width) {
        return (HEllipse) circle(width)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HPolygon polygon(Point2D.Double... points) {
        return new HPolygonImpl(points)
                ;
    }

    @Override
    public HPolygon polygon(double x, double y, double width, double height, Point2D.Double... points) {
        return (HPolygon) polygon(points)
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HPolyline polyline(double x, double y, double width, double height, Point2D.Double... points) {
        return (HPolyline) polyline(points)
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HPolyline polyline(Point2D.Double... points) {
        return new HPolylineImpl(points);
    }

    @Override
    public HLine line(double x, double y, double maxx, double maxy) {
        return (HLine) new HLineImpl(new Point2D.Double(x, y), new Point2D.Double(maxx, maxy))
                .set(HStyles.position(0, 0))
                .set(HStyles.size(maxx - x, maxy - y))
                ;
    }

    @Override
    public HLine line() {
        return (HLine) new HLineImpl()
                ;
    }

    @Override
    public HImage image(double x, double y, Image image) {
        return (HImage) new DefaultHImage(image)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HLatexEquation equation(double x, double y, String latex) {
        return (HLatexEquation) new HLatexEquationImpl(latex)
                .set(HStyles.position(x, y))
                ;
    }
}
