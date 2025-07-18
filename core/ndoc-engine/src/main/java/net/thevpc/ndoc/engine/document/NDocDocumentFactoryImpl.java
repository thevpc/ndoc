package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.style.NDocProps;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NAssert;


public class NDocDocumentFactoryImpl implements NDocDocumentFactory {
    private DefaultNDocEngine engine;

    public NDocDocumentFactoryImpl(DefaultNDocEngine engine) {
        this.engine = engine;
    }

    @Override
    public NDocument ofDocument() {
        DefaultNDocument d = new DefaultNDocument();
        d.root().addRules(NDocDocumentRootRules.DEFAULT);
        return d;
    }

    @Override
    public NDocNode of(String type) {
        NAssert.requireNonNull(type, "type");
        return engine.nodeTypeFactory(type)
                .get().newNode();
    }

    @Override
    public NDocNode ofPage() {
        return of(NDocNodeType.PAGE);
    }

    @Override
    public NDocNode ofPageGroup() {
        return of(NDocNodeType.PAGE_GROUP);
    }


    @Override
    public NDocNode ofVoid() {
        return of(NDocNodeType.VOID);
    }

    @Override
    public NDocNode ofGlue() {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.maxX(100))
                .setProperty(NDocProps.maxY(100))
                ;
    }

    @Override
    public NDocNode ofGlueV() {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.maxX(0))
                .setProperty(NDocProps.maxY(100))
                ;
    }

    @Override
    public NDocNode ofGlueH() {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.maxX(100))
                .setProperty(NDocProps.maxY(0))
                ;
    }

    @Override
    public NDocNode ofStrutV(double w) {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.size(0, 100))
                ;
    }

    @Override
    public NDocNode ofStrutH(double w) {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.size(100, 0))
                ;
    }

    @Override
    public NDocNode ofStrut(double w, double h) {
        return of(NDocNodeType.FILLER)
                .setProperty(NDocProps.size(w, 0))
                ;
    }

    @Override
    public NDocNode ofArc(double from, double to) {
        return of(NDocNodeType.ARC)
                .setProperty(NDocProp.ofDouble(NDocPropName.FROM, from))
                .setProperty(NDocProp.ofDouble(NDocPropName.FROM, to))
                ;
    }

    @Override
    public NDocNode ofArc() {
        return of(NDocNodeType.ARC);
    }

    @Override
    public NDocNode ofAssign() {
        return of(NDocNodeType.ASSIGN);
    }

    @Override
    public NDocNode ofAssign(String name, NElement value) {
        return ofAssign()
                .setProperty(NDocPropName.NAME, NElement.ofString(name))
                .setProperty(NDocPropName.VALUE, value)
                ;
    }

    @Override
    public NDocNode ofFlow() {
        return of(NDocNodeType.FLOW);
    }

    @Override
    public NDocNode ofStack() {
        return of(NDocNodeType.STACK);
    }

    @Override
    public NDocNode ofUnorderedList() {
        return of(NDocNodeType.UNORDERED_LIST);
    }

    @Override
    public NDocNode ofOrderedList() {
        return of(NDocNodeType.ORDERED_LIST);
    }

    @Override
    public NDocNode ofGrid(int cols, int rows) {
        return ofGrid()
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(cols))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(rows))
                ;
    }

    public NDocNode ofGrid() {
        return of(NDocNodeType.GRID);
    }

    @Override
    public NDocNode ofGridV() {
        return ofGrid()
                .setProperty(NDocProps.columns(1))
                .setProperty(NDocProps.rows(-1))
                ;
    }

    @Override
    public NDocNode ofGridH() {
        return ofGrid()
                .setProperty(NDocProps.columns(-1))
                .setProperty(NDocProps.rows(1))
                ;
    }

    @Override
    public NDocNode ofPlain(String text) {
        return ofPlain().setProperty(NDocPropName.VALUE, NElement.ofString(text));
    }

    @Override
    public NDocNode ofText(String text) {
        return ofText().setProperty(NDocPropName.VALUE, NElement.ofString(text));
    }

    @Override
    public NDocNode ofPlain() {
        return of(NDocNodeType.PLAIN);
    }

    @Override
    public NDocNode ofRectangle() {
        return of(NDocNodeType.RECTANGLE);
    }

    @Override
    public NDocNode ofSphere() {
        return of(NDocNodeType.SPHERE);
    }

    @Override
    public NDocNode ofEllipsoid() {
        return of(NDocNodeType.ELLIPSOID);
    }

    @Override
    public NDocNode ofSquare() {
        return of(NDocNodeType.SQUARE);
    }


    @Override
    public NDocNode ofEllipse() {
        return of(NDocNodeType.ELLIPSE);
    }

    @Override
    public NDocNode ofSource() {
        return of(NDocNodeType.SOURCE);
    }


    @Override
    public NDocNode ofCircle() {
        return of(NDocNodeType.CIRCLE);
    }

    @Override
    public NDocNode ofTriangle() {
        return of(NDocNodeType.TRIANGLE);
    }

    @Override
    public NDocNode ofHexagon() {
        return of(NDocNodeType.HEXAGON);
    }

    @Override
    public NDocNode ofOctagon() {
        return of(NDocNodeType.OCTAGON);
    }

    @Override
    public NDocNode ofPentagon() {
        return of(NDocNodeType.PENTAGON);
    }

    public NDocNode ofPolygon() {
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
    public NDocNode ofPolyline() {
        return of(NDocNodeType.POLYLINE);
    }

    @Override
    public NDocNode ofLine() {
        return of(NDocNodeType.LINE);
    }

    @Override
    public NDocNode ofImage() {
        return of(NDocNodeType.IMAGE);
    }

    @Override
    public NDocNode ofEquation() {
        return of(NDocNodeType.EQUATION);
    }

    @Override
    public NDocNode ofEquation(String value) {
        return ofEquation().setProperty(NDocPropName.VALUE, NElement.ofString(value));
    }

    @Override
    public NDocNode ofText() {
        return of(NDocNodeType.TEXT);
    }
}
