//package net.thevpc.halfa.engine.parser.nodes;
//
//import net.thevpc.halfa.HDocumentFactory;
//import net.thevpc.halfa.api.model.Int2;
//import net.thevpc.halfa.api.node.HNode;
//import net.thevpc.halfa.api.style.HPropName;
//import net.thevpc.halfa.api.style.HProps;
//import net.thevpc.halfa.spi.util.ObjEx;
//import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
//import net.thevpc.nuts.util.NOptional;
//import net.thevpc.tson.TsonElement;
//
//public class GridHITemNamedObjectParser extends SimpleHITemNamedObjectParser {
//    public GridHITemNamedObjectParser() {
//        super("grid");
//    }
//
//    @Override
//    protected HNode node(HDocumentFactory f) {
//        return f.ofGrid();
//    }
//
//    @Override
//    protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
//        if (
//                !p.getProperty(HPropName.COLUMNS).isPresent()
//                        && !p.getProperty(HPropName.ROWS).isPresent()) {
//            NOptional<Int2> dp = new ObjEx(e).asInt2();
//            if (dp.isPresent()) {
//                p.setProperty(HProps.columns(dp.get().getX()));
//                p.setProperty(HProps.rows(dp.get().getY()));
//                return true;
//            }
//        } else if (!p.getProperty(HPropName.COLUMNS).isPresent()) {
//            NOptional<Integer> dp = new ObjEx(e).asInt();
//            p.setProperty(HProps.columns(dp.get()));
//            return true;
//        } else if (!p.getProperty(HPropName.ROWS).isPresent()) {
//            NOptional<Integer> dp = new ObjEx(e).asInt();
//            p.setProperty(HProps.rows(dp.get()));
//            return true;
//        }
//        return false;
//    }
//}
