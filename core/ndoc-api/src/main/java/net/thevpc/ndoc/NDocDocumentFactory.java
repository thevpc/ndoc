package net.thevpc.ndoc;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.nuts.elem.NElement;

public interface NDocDocumentFactory {

    NDocument ofDocument(NDocResource source);

    NDocNode ofPage();

    NDocNode ofPageGroup();

    NDocNode of(String type);

    NDocNode ofPlain(String text);

    NDocNode ofText(String text);

    NDocNode ofText(NElement text);

    NDocNode ofPlain();

    NDocNode ofVoid();

    NDocNode ofGlue();

    NDocNode ofGlueV();

    NDocNode ofGlueH();

    NDocNode ofStrutV(double w);

    NDocNode ofStrutH(double w);

    NDocNode ofStrut(double w, double h);

    NDocNode ofArc(double from, double to);

    NDocNode ofArc();

    NDocNode ofAssign();

    NDocNode ofAssign(String name, NElement value);

    NDocNode ofFlow();

    NDocNode ofStack();

    NDocNode ofUnorderedList();

    NDocNode ofOrderedList();

    NDocNode ofGrid(int cols, int rows);

    NDocNode ofGrid();

    NDocNode ofGridV();

    NDocNode ofGridH();

    NDocNode ofSphere();

    NDocNode ofEllipsoid();

    NDocNode ofRectangle();

    NDocNode ofSquare();

    NDocNode ofEllipse();

    NDocNode ofCircle();

    NDocNode ofTriangle();

    NDocNode ofHexagon();

    NDocNode ofOctagon();

    NDocNode ofPentagon();

    NDocNode ofPolygon();

    NDocNode ofPolyline();

    NDocNode ofLine();

    NDocNode ofImage();

    NDocNode ofEquation();

    NDocNode ofEquation(String value);

    NDocNode ofText();

    NDocNode ofSource();
}
