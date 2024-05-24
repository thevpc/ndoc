package net.thevpc.halfa;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.node.container.HFlowContainer;
import net.thevpc.halfa.api.node.container.HGridContainer;
import net.thevpc.halfa.api.node.container.HStackContainer;
import net.thevpc.halfa.api.node.container.HUnorderedList;

import java.awt.*;

public interface HDocumentFactory {
    HDocument ofDocument();

    HPage ofPage();

    HPageGroup ofPageGroup();

    HNode create(HNodeType type);

    HText ofText(double x, double y, String hello);

    HText ofText(String hello);

    HText ofText();

    HVoid ofVoid();

    HFiller ofGlue();

    HFiller ofGlueV();

    HFiller ofGlueH();

    HFiller ofStrutV(double w);

    HFiller ofStrutH(double w);

    HFiller ofStrut(double w, double h);

    HArc ofArc(double from, double to);

    HArc ofArc();

    HCtrlAssign ofAssign();
    HFlowContainer ofFlow(HNode... children);

    HFlowContainer ofFlow(double x, double y, HNode... children);

    HStackContainer ofStack(double x, double y, HNode... children);

    HUnorderedList ofUnorderedList(double x, double y, HNode... children);

    HOrderedList ofOrderedList(double x, double y, HNode... children);

    HUnorderedList ofUnorderedList(HNode... children);

    HOrderedList ofOrderedList(HNode... children);

    HStackContainer ofStack(HNode... children);

    HGridContainer ofGrid(HNode... children);

    HGridContainer ofGrid(int cols, int rows, HNode... children);

    HGridContainer ofGridV(HNode... children);

    HGridContainer ofGridH(HNode... children);

    HRectangle ofRectangle(double x, double y, double width, double height);

    HRectangle ofRectangle();

    HRectangle ofSquare();

    HRectangle ofSquare(double width);

    HRectangle ofSquare(double x, double y, double width);

    HEllipse ofEllipse(double x, double y, double width, double height);

    HEllipse ofEllipse();

    HEllipse ofCircle();

    HPolygon ofTriangle();

    HPolygon ofHexagon();

    HPolygon ofOctagon();

    HPolygon ofPentagon();

    HPolygon ofPolygon(int edges);

    HEllipse ofCircle(double width);

    HEllipse ofCircle(double x, double y, double width);

    HPolygon ofPolygon(double x, double y, double width, double height, Double2... points);

    HPolygon ofPolygon(Double2... points);

    HPolyline ofPolyline(double x, double y, double width, double height, Double2... points);

    HPolyline ofPolyline(Double2... points);

    HLine ofLine(double x, double y, double maxx, double maxy);

    HLine ofLine();

    HImage ofImage();

    HImage ofImage(double x, double y, Image image);

    HLatexEquation ofEquation(double x, double y, String latex);

    HLatexEquation ofEquation();

    HLatex ofLatex();
}
