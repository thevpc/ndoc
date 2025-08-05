package net.thevpc.ntexup.api.document;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.nuts.elem.NElement;

public interface NTxDocumentFactory {

    NTxDocument ofDocument(NTxSource source);

    NTxNode ofPage();

    NTxNode ofPageGroup();

    NTxNode of(String type);

    NTxNode ofPlain(String text);

    NTxNode ofText(String text);

    NTxNode ofText(NElement text);

    NTxNode ofPlain();

    NTxNode ofVoid();

    NTxNode ofGlue();

    NTxNode ofGlueV();

    NTxNode ofGlueH();

    NTxNode ofStrutV(double w);

    NTxNode ofStrutH(double w);

    NTxNode ofStrut(double w, double h);

    NTxNode ofArc(double from, double to);

    NTxNode ofArc();

    NTxNode ofAssign();

    NTxNode ofAssign(String name, NElement value);

    NTxNode ofFlow();

    NTxNode ofGroup();

    NTxNode ofUnorderedList();

    NTxNode ofOrderedList();

    NTxNode ofGrid(int cols, int rows);

    NTxNode ofGrid();

    NTxNode ofGridV();

    NTxNode ofGridH();

    NTxNode ofSphere();

    NTxNode ofEllipsoid();

    NTxNode ofRectangle();

    NTxNode ofSquare();

    NTxNode ofEllipse();

    NTxNode ofCircle();

    NTxNode ofTriangle();

    NTxNode ofHexagon();

    NTxNode ofOctagon();

    NTxNode ofPentagon();

    NTxNode ofPolygon();

    NTxNode ofPolyline();

    NTxNode ofLine();

    NTxNode ofImage();

    NTxNode ofEquation();

    NTxNode ofEquation(String value);

    NTxNode ofText();

    NTxNode ofSource();
}
