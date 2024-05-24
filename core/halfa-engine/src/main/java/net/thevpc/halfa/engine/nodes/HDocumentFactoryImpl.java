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
import net.thevpc.halfa.engine.nodes.filler.HVoidImpl;
import net.thevpc.halfa.engine.nodes.image.HImageImpl;
import net.thevpc.halfa.engine.nodes.shape.*;
import net.thevpc.halfa.engine.nodes.text.HTextImpl;
import net.thevpc.halfa.engine.nodes.text.HLatexEquationImpl;
import net.thevpc.halfa.engine.styles.HDocumentRootRules;
import net.thevpc.nuts.util.NAssert;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HDocumentFactoryImpl implements HDocumentFactory {
    @Override
    public HDocument ofDocument() {
        DefaultHDocument d = new DefaultHDocument();
        d.root().addRules(HDocumentRootRules.DEFAULT);
        return d;
    }

    @Override
    public HNode create(HNodeType type) {
        NAssert.requireNonNull(type,"type");
        switch (type) {
            case ORDERED_LIST:
                return ofOrderedList();
            case UNORDERED_LIST:
                return ofUnorderedList();
            case FLOW:
                return ofFlow();
            case FILLER:
                return ofGlue();
            case IMAGE:
                return ofImage();
            case LINE:
                return ofLine();
            case GRID:
                return ofGrid();
            case POLYGON:
                return ofPolygon();
            case POLYLINE:
                return ofPolyline();
            case PAGE:
                return ofPage();
            case PAGE_GROUP:
                return ofPageGroup();
            case TEXT:
                return ofText();
            case STACK:
                return ofStack();
            case RECTANGLE:
                return ofRectangle();
            case EQUATION:
                return ofEquation();
            case ELLIPSE:
                return ofEllipse();
            case ARC:
                return ofArc();
            case CUSTOM:
                return ofText();
        }
        throw new IllegalArgumentException("not supported "+type);
    }

    @Override
    public HPage ofPage() {
        return new HPageImpl();
    }

    @Override
    public HPageGroup ofPageGroup() {
        return new HPageGroupImpl();
    }


    @Override
    public HVoid ofVoid() {
        return new HVoidImpl();
    }
    @Override
    public HFiller ofGlue() {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMaxParent()),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMaxParent())
                )
        );
    }

    @Override
    public HFiller ofGlueV() {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMin()),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMaxParent())
                )
        );
    }

    @Override
    public HFiller ofGlueH() {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMaxParent()),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMin())
                )
        );
    }

    @Override
    public HFiller ofStrutV(double w) {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofParent(w), XLen.ofParent(w), XLen.ofParent(w)),
                        new YLenConstraints(YLen.ofMin(), YLen.ofMin(), YLen.ofMaxParent())
                )
        );
    }

    @Override
    public HFiller ofStrutH(double w) {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofMin(), XLen.ofMin(), XLen.ofMaxParent()),
                        new YLenConstraints(YLen.ofParent(w), YLen.ofParent(w), YLen.ofParent(w))
                )
        );
    }

    @Override
    public HFiller ofStrut(double w, double h) {
        return new HFillerImpl(
                new XYConstraints(
                        new XLenConstraints(XLen.ofParent(w), XLen.ofParent(w), XLen.ofParent(w)),
                        new YLenConstraints(YLen.ofParent(h), YLen.ofParent(h), YLen.ofParent(h))
                )
        );
    }

    @Override
    public HArc ofArc(double from, double to) {
        return new HArcImpl(
                from, to
        );
    }

    @Override
    public HArc ofArc() {
        return new HArcImpl(
                null, null
        );
    }

    @Override
    public HFlowContainer ofFlow(double x, double y, HNode... children) {
        return (HFlowContainer) ofFlow(children)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HCtrlAssign ofAssign() {
        return new HCtrlAssignImpl();
    }

    @Override
    public HFlowContainer ofFlow(HNode... children) {
        return (HFlowContainer) new HFlowContainerImpl(new ArrayList<>(Arrays.asList(children)))
                ;
    }

    @Override
    public HStackContainer ofStack(HNode... children) {
        return new HStackContainerImpl(new ArrayList<>(Arrays.asList(children)))
                ;
    }

    @Override
    public HStackContainer ofStack(double x, double y, HNode... children) {
        return (HStackContainer) ofStack(children)
                .set(HStyles.position(x, y));
    }

    @Override
    public HUnorderedList ofUnorderedList(double x, double y, HNode... children) {
        return (HUnorderedList) ofUnorderedList(children)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HOrderedList ofOrderedList(double x, double y, HNode... children) {
        return (HOrderedList) ofOrderedList(children)
                .set(HStyles.position(x, y));
    }

    @Override
    public HUnorderedList ofUnorderedList(HNode... children) {
        return new HUnorderedListImpl(new ArrayList<>(Arrays.asList(children)));
    }

    @Override
    public HOrderedList ofOrderedList(HNode... children) {
        return new HOrderedListImpl(new ArrayList<>(Arrays.asList(children)));
    }


    @Override
    public HGridContainer ofGrid(int cols, int rows, HNode... children) {
        return (HGridContainer) ofGrid(children)
                .set(HStyles.columns(cols))
                .set(HStyles.rows(rows))
                ;
    }

    @Override
    public HGridContainer ofGrid(HNode... children) {
        return new HGridContainerImpl(new ArrayList<>(Arrays.asList(children)))
                ;
    }

    @Override
    public HGridContainer ofGridV(HNode... children) {
        return ofGrid(1, -1, children)
                ;
    }

    @Override
    public HGridContainer ofGridH(HNode... children) {
        return ofGrid(-1, 1, children);
    }

    @Override
    public HText ofText(double x, double y, String hello) {
        return (HText) ofText(hello)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HText ofText() {
        return new HTextImpl(null);
    }

    @Override
    public HText ofText(String hello) {
        return new HTextImpl(hello);
    }

    @Override
    public HRectangle ofRectangle(double x, double y, double width, double height) {
        return (HRectangle) new HRectangleImpl()
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HRectangle ofRectangle() {
        return new HRectangleImpl();
    }

    @Override
    public HRectangle ofSquare(double x, double y, double width) {
        return (HRectangle) ofRectangle(x, y, width, width)
                .set(HStyles.preserveShapeRatio(true))
                ;
    }

    @Override
    public HRectangle ofSquare(double width) {
        return (HRectangle) ofSquare()
                .set(HStyles.size(width, width))
                ;
    }

    @Override
    public HRectangle ofSquare() {
        return (HRectangle) ofRectangle()
                .set(HStyles.preserveShapeRatio(true))
                ;
    }

    @Override
    public HEllipse ofEllipse(double x, double y, double width, double height) {
        return (HEllipse) ofEllipse()
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height));
    }

    @Override
    public HEllipse ofEllipse() {
        return (HEllipse) new HEllipseImpl()
                ;
    }


    @Override
    public HEllipse ofCircle() {
        return (HEllipse) new HEllipseImpl()
                .set(HStyles.preserveShapeRatio(true))
                ;
    }

    @Override
    public HPolygon ofTriangle() {
        return ofPolygon(3);
    }

    @Override
    public HPolygon ofHexagon() {
        return ofPolygon(6);
    }

    @Override
    public HPolygon ofOctagon() {
        return ofPolygon(8);
    }

    @Override
    public HPolygon ofPentagon() {
        return ofPolygon(5);
    }

    @Override
    public HPolygon ofPolygon(int edges) {
        if(edges<=2){
            throw new IllegalArgumentException("invalid edges "+edges+". must be >2");
        }
        switch (edges){
            case 3:{
                return ofPolygon(
                        new Double2(0,0),
                        new Double2(0,100),
                        new Double2(50,0)
                );
            }
            case 4:{
                return ofPolygon(
                        new Double2(0,0),
                        new Double2(0,100),
                        new Double2(100,100),
                        new Double2(100,0)
                );
            }
            default:{
                java.util.List<Double2> all=new ArrayList<>();
                    for (int i = 0; i <edges ; i++) {
                        all.add(
                                new Double2(
                                        50 + Math.sin(i*Math.PI*2/edges)*50,
                                        50 + Math.cos(i*Math.PI*2/edges)*50
                                )
                        );
                    }
                    return ofPolygon(all.toArray(new Double2[0]));
                }
        }
    }

    @Override
    public HEllipse ofCircle(double width) {
        return (HEllipse) ofCircle()
                .set(HStyles.size(width, width))
                ;
    }

    @Override
    public HEllipse ofCircle(double x, double y, double width) {
        return (HEllipse) ofCircle(width)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HPolygon ofPolygon(Double2... points) {
        return (HPolygon) new HPolygonImpl(points)
                .set(HStyles.preserveShapeRatio(true))
                ;
    }

    @Override
    public HPolygon ofPolygon(double x, double y, double width, double height, Double2... points) {
        return (HPolygon) ofPolygon(points)
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HPolyline ofPolyline(double x, double y, double width, double height, Double2... points) {
        return (HPolyline) ofPolyline(points)
                .set(HStyles.position(x, y))
                .set(HStyles.size(width, height))
                ;
    }

    @Override
    public HPolyline ofPolyline(Double2... points) {
        return new HPolylineImpl(points);
    }

    @Override
    public HLine ofLine(double x, double y, double maxx, double maxy) {
        return (HLine) new HLineImpl(new Double2(x, y), new Double2(maxx, maxy))
                .set(HStyles.position(0, 0))
                .set(HStyles.size(maxx - x, maxy - y))
                ;
    }

    @Override
    public HLine ofLine() {
        return (HLine) new HLineImpl()
                ;
    }

    @Override
    public HImage ofImage() {
        return (HImage) new HImageImpl()
                ;
    }

    @Override
    public HImage ofImage(double x, double y, Image image) {
        return (HImage) new HImageImpl(image)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HLatexEquation ofEquation(double x, double y, String latex) {
        return (HLatexEquation) new HLatexEquationImpl(latex)
                .set(HStyles.position(x, y))
                ;
    }

    @Override
    public HLatexEquation ofEquation() {
        return new HLatexEquationImpl();
    }
}
