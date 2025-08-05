package net.thevpc.ntexup.engine.document;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.ntexup.api.document.style.NTxProp;
import  net.thevpc.ntexup.api.document.style.NDocPropName;
import  net.thevpc.ntexup.api.document.style.NDocProps;
import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.engine.DefaultNDocEngine;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NAssert;


public class NTxDocumentFactoryImpl implements NTxDocumentFactory {
    private DefaultNDocEngine engine;

    public NTxDocumentFactoryImpl(DefaultNDocEngine engine) {
        this.engine = engine;
    }

    @Override
    public NDocument ofDocument(NDocResource source) {
        DefaultNDocument d = new DefaultNDocument(source);
        d.root().addRules(NDocDocumentRootRules.DEFAULT);
        return d;
    }

    @Override
    public NTxNode of(String type) {
        NAssert.requireNonNull(type, "type");
        return engine.nodeTypeParser(type)
                .get().newNode();
    }

    @Override
    public NTxNode ofPage() {
        return of(NDocNodeType.PAGE);
    }

    @Override
    public NTxNode ofPageGroup() {
        return of(NDocNodeType.PAGE_GROUP);
    }


    @Override
    public NTxNode ofVoid() {
        return of(NDocNodeType.VOID);
    }

    @Override
    public NTxNode ofGlue() {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.maxX(100))
                .setProperty(NDocProps.maxY(100))
                ;
    }

    @Override
    public NTxNode ofGlueV() {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.maxX(0))
                .setProperty(NDocProps.maxY(100))
                ;
    }

    @Override
    public NTxNode ofGlueH() {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.maxX(100))
                .setProperty(NDocProps.maxY(0))
                ;
    }

    @Override
    public NTxNode ofStrutV(double w) {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.size(0, 100))
                ;
    }

    @Override
    public NTxNode ofStrutH(double w) {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.size(100, 0))
                ;
    }

    @Override
    public NTxNode ofStrut(double w, double h) {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.size(w, 0))
                ;
    }

    @Override
    public NTxNode ofArc(double from, double to) {
        return of(NDocNodeType.ARC)
                .setProperty(NTxProp.ofDouble(NDocPropName.FROM, from))
                .setProperty(NTxProp.ofDouble(NDocPropName.FROM, to))
                ;
    }

    @Override
    public NTxNode ofArc() {
        return of(NDocNodeType.ARC);
    }

    @Override
    public NTxNode ofAssign() {
        return of(NDocNodeType.CTRL_ASSIGN);
    }

    @Override
    public NTxNode ofAssign(String name, NElement value) {
        return ofAssign()
                .setProperty(NDocPropName.NAME, NElement.ofString(name))
                .setProperty(NDocPropName.VALUE, value)
                ;
    }

    @Override
    public NTxNode ofFlow() {
        return of(NDocNodeType.FLOW);
    }

    @Override
    public NTxNode ofGroup() {
        return of(NDocNodeType.GROUP);
    }

    @Override
    public NTxNode ofUnorderedList() {
        return of(NDocNodeType.UNORDERED_LIST);
    }

    @Override
    public NTxNode ofOrderedList() {
        return of(NDocNodeType.ORDERED_LIST);
    }

    @Override
    public NTxNode ofGrid(int cols, int rows) {
        return ofGrid()
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(cols))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(rows))
                ;
    }

    public NTxNode ofGrid() {
        return of(NDocNodeType.GRID);
    }

    @Override
    public NTxNode ofGridV() {
        return ofGrid()
                .setProperty(NDocProps.columns(1))
                .setProperty(NDocProps.rows(-1))
                ;
    }

    @Override
    public NTxNode ofGridH() {
        return ofGrid()
                .setProperty(NDocProps.columns(-1))
                .setProperty(NDocProps.rows(1))
                ;
    }

    @Override
    public NTxNode ofPlain(String text) {
        return ofPlain().setProperty(NDocPropName.VALUE, NElement.ofString(text));
    }

    @Override
    public NTxNode ofText(String text) {
        return ofText().setProperty(NDocPropName.VALUE, NElement.ofString(text));
    }

    @Override
    public NTxNode ofText(NElement text) {
        return ofText().setProperty(NDocPropName.VALUE, text==null?NElement.ofString(""):text);
    }

    @Override
    public NTxNode ofPlain() {
        return of(NDocNodeType.PLAIN);
    }

    @Override
    public NTxNode ofRectangle() {
        return of(NDocNodeType.RECTANGLE);
    }

    @Override
    public NTxNode ofSphere() {
        return of(NDocNodeType.SPHERE);
    }

    @Override
    public NTxNode ofEllipsoid() {
        return of(NDocNodeType.ELLIPSOID);
    }

    @Override
    public NTxNode ofSquare() {
        return of(NDocNodeType.SQUARE);
    }


    @Override
    public NTxNode ofEllipse() {
        return of(NDocNodeType.ELLIPSE);
    }

    @Override
    public NTxNode ofSource() {
        return of(NDocNodeType.SOURCE);
    }


    @Override
    public NTxNode ofCircle() {
        return of(NDocNodeType.CIRCLE);
    }

    @Override
    public NTxNode ofTriangle() {
        return of(NDocNodeType.TRIANGLE);
    }

    @Override
    public NTxNode ofHexagon() {
        return of(NDocNodeType.HEXAGON);
    }

    @Override
    public NTxNode ofOctagon() {
        return of(NDocNodeType.OCTAGON);
    }

    @Override
    public NTxNode ofPentagon() {
        return of(NDocNodeType.PENTAGON);
    }

    public NTxNode ofPolygon() {
        return of(NDocNodeType.POLYGON);
    }
//    @Override
//    public NDocNode ofPolygon(int edges) {
//        if(edges<=2){
//            throw new IllegalArgumentException("invalid edges "+edges+". must be >2");
//        }
//        switch (edges){
//            case 3:{
//                return ofPolygon(
//                        new Double2(0,0),
//                        new Double2(0,100),
//                        new Double2(50,0)
//                );
//            }
//            case 4:{
//                return ofPolygon(
//                        new Double2(0,0),
//                        new Double2(0,100),
//                        new Double2(100,100),
//                        new Double2(100,0)
//                );
//            }
//            default:{
//                java.util.List<Double2> all=new ArrayList<>();
//                    for (int i = 0; i <edges ; i++) {
//                        all.add(
//                                new Double2(
//                                        50 + Math.sin(i*Math.PI*2/edges)*50,
//                                        50 + Math.cos(i*Math.PI*2/edges)*50
//                                )
//                        );
//                    }
//                    return ofPolygon(all.toArray(new Double2[0]));
//                }
//        }
//    }

    @Override
    public NTxNode ofPolyline() {
        return of(NDocNodeType.POLYLINE);
    }

    @Override
    public NTxNode ofLine() {
        return of(NDocNodeType.LINE);
    }

    @Override
    public NTxNode ofImage() {
        return of(NDocNodeType.IMAGE);
    }

    @Override
    public NTxNode ofEquation() {
        return of(NDocNodeType.EQUATION);
    }

    @Override
    public NTxNode ofEquation(String value) {
        return ofEquation().setProperty(NDocPropName.VALUE, NElement.ofString(value));
    }

    @Override
    public NTxNode ofText() {
        return of(NDocNodeType.TEXT);
    }
}
