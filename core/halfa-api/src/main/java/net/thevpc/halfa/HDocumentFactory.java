package net.thevpc.halfa;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.tson.TsonElement;

public interface HDocumentFactory {

    HDocument ofDocument();

    HNode ofPage();

    HNode ofPageGroup();

    HNode of(String type);

    HNode ofPlain(String text);

    HNode ofText(String text);

    HNode ofPlain();

    HNode ofVoid();

    HNode ofGlue();

    HNode ofGlueV();

    HNode ofGlueH();

    HNode ofStrutV(double w);

    HNode ofStrutH(double w);

    HNode ofStrut(double w, double h);

    HNode ofArc(double from, double to);

    HNode ofArc();

    HNode ofAssign();

    HNode ofAssign(String name, TsonElement value);

    HNode ofFlow();

    HNode ofStack();

    HNode ofUnorderedList();

    HNode ofOrderedList();

    HNode ofGrid(int cols, int rows);

    HNode ofGrid();

    HNode ofGridV();

    HNode ofGridH();

    HNode ofSphere();

    HNode ofEllipsoid();

    HNode ofRectangle();

    HNode ofSquare();

    HNode ofEllipse();

    HNode ofCircle();

    HNode ofTriangle();

    HNode ofHexagon();

    HNode ofOctagon();

    HNode ofPentagon();

    HNode ofPolygon();

    HNode ofPolyline();

    HNode ofLine();

    HNode ofImage();

    HNode ofEquation();

    HNode ofEquation(String value);

    HNode ofText();

    HNode ofSource();
}
