package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProps;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.DefaultHEngine;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class HDocumentFactoryImpl implements HDocumentFactory {
    private DefaultHEngine engine;

    public HDocumentFactoryImpl(DefaultHEngine engine) {
        this.engine = engine;
    }

    @Override
    public HDocument ofDocument() {
        DefaultHDocument d = new DefaultHDocument();
        d.root().addRules(HDocumentRootRules.DEFAULT);
        return d;
    }

    @Override
    public HNode of(String type) {
        NAssert.requireNonNull(type, "type");
        return engine.nodeTypeFactory(type)
                .get().newNode();
    }

    @Override
    public HNode ofPage() {
        return of(HNodeType.PAGE);
    }

    @Override
    public HNode ofPageGroup() {
        return of(HNodeType.PAGE_GROUP);
    }


    @Override
    public HNode ofVoid() {
        return of(HNodeType.VOID);
    }

    @Override
    public HNode ofGlue() {
        return of(HNodeType.FILLER)
                .setProperty(HProps.maxX(100))
                .setProperty(HProps.maxY(100))
                ;
    }

    @Override
    public HNode ofGlueV() {
        return of(HNodeType.FILLER)
                .setProperty(HProps.maxX(0))
                .setProperty(HProps.maxY(100))
                ;
    }

    @Override
    public HNode ofGlueH() {
        return of(HNodeType.FILLER)
                .setProperty(HProps.maxX(100))
                .setProperty(HProps.maxY(0))
                ;
    }

    @Override
    public HNode ofStrutV(double w) {
        return of(HNodeType.FILLER)
                .setProperty(HProps.size(0, 100))
                ;
    }

    @Override
    public HNode ofStrutH(double w) {
        return of(HNodeType.FILLER)
                .setProperty(HProps.size(100, 0))
                ;
    }

    @Override
    public HNode ofStrut(double w, double h) {
        return of(HNodeType.FILLER)
                .setProperty(HProps.size(w, 0))
                ;
    }

    @Override
    public HNode ofArc(double from, double to) {
        return of(HNodeType.ARC)
                .setProperty(HProp.ofDouble(HPropName.FROM, from))
                .setProperty(HProp.ofDouble(HPropName.FROM, to))
                ;
    }

    @Override
    public HNode ofArc() {
        return of(HNodeType.ARC);
    }

    @Override
    public HNode ofAssign() {
        return of(HNodeType.ASSIGN);
    }

    @Override
    public HNode ofAssign(String name, TsonElement value) {
        return ofAssign()
                .setProperty(HPropName.NAME, Tson.of(name))
                .setProperty(HPropName.VALUE, value)
                ;
    }

    @Override
    public HNode ofFlow() {
        return of(HNodeType.FLOW);
    }

    @Override
    public HNode ofStack() {
        return of(HNodeType.STACK);
    }

    @Override
    public HNode ofUnorderedList() {
        return of(HNodeType.UNORDERED_LIST);
    }

    @Override
    public HNode ofOrderedList() {
        return of(HNodeType.ORDERED_LIST);
    }

    @Override
    public HNode ofGrid(int cols, int rows) {
        return ofGrid()
                .setProperty(HPropName.COLUMNS, Tson.of(cols))
                .setProperty(HPropName.ROWS, Tson.of(rows))
                ;
    }

    public HNode ofGrid() {
        return of(HNodeType.GRID);
    }

    @Override
    public HNode ofGridV() {
        return ofGrid()
                .setProperty(HProps.columns(1))
                .setProperty(HProps.rows(-1))
                ;
    }

    @Override
    public HNode ofGridH() {
        return ofGrid()
                .setProperty(HProps.columns(-1))
                .setProperty(HProps.rows(1))
                ;
    }

    @Override
    public HNode ofPlain(String text) {
        return ofPlain().setProperty(HPropName.VALUE, Tson.of(text));
    }

    @Override
    public HNode ofText(String text) {
        return ofText().setProperty(HPropName.VALUE, Tson.of(text));
    }

    @Override
    public HNode ofPlain() {
        return of(HNodeType.PLAIN);
    }

    @Override
    public HNode ofRectangle() {
        return of(HNodeType.RECTANGLE);
    }

    @Override
    public HNode ofSphere() {
        return of(HNodeType.SPHERE);
    }

    @Override
    public HNode ofEllipsoid() {
        return of(HNodeType.ELLIPSOID);
    }

    @Override
    public HNode ofSquare() {
        return of(HNodeType.SQUARE);
    }


    @Override
    public HNode ofEllipse() {
        return of(HNodeType.ELLIPSE);
    }

    @Override
    public HNode ofSource() {
        return of(HNodeType.SOURCE);
    }


    @Override
    public HNode ofCircle() {
        return of(HNodeType.CIRCLE);
    }

    @Override
    public HNode ofTriangle() {
        return of(HNodeType.TRIANGLE);
    }

    @Override
    public HNode ofHexagon() {
        return of(HNodeType.HEXAGON);
    }

    @Override
    public HNode ofOctagon() {
        return of(HNodeType.OCTAGON);
    }

    @Override
    public HNode ofPentagon() {
        return of(HNodeType.PENTAGON);
    }

    public HNode ofPolygon() {
        return of(HNodeType.POLYGON);
    }
//    @Override
//    public HNode ofPolygon(int edges) {
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
    public HNode ofPolyline() {
        return of(HNodeType.POLYLINE);
    }

    @Override
    public HNode ofLine() {
        return of(HNodeType.LINE);
    }

    @Override
    public HNode ofImage() {
        return of(HNodeType.IMAGE);
    }

    @Override
    public HNode ofEquation() {
        return of(HNodeType.EQUATION);
    }

    @Override
    public HNode ofEquation(String value) {
        return ofEquation().setProperty(HPropName.VALUE, Tson.of(value));
    }

    @Override
    public HNode ofText() {
        return of(HNodeType.TEXT);
    }
}
