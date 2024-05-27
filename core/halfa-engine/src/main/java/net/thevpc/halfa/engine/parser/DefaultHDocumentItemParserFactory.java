package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HItemList;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.parser.nodes.*;
import net.thevpc.halfa.engine.parser.styles.StylesHITemNamedObjectParser;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.nodes.HNodeTypeFactory;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.halfa.spi.nodes.HNodeParserFactory;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author vpc
 */
public class DefaultHDocumentItemParserFactory
        implements HNodeParserFactory {
    static Map<String, HITemNamedObjectParser> allParsers = new HashMap<>();

    static {
        register(new ImportHITemNamedObjectParser());
        register(new StylesHITemNamedObjectParser());
    }

    private static void register(HITemNamedObjectParser s) {
        for (String id : s.ids()) {
            String uid = HUtils.uid(id);
            HITemNamedObjectParser o = allParsers.get(id);
            if (o != null) {
                throw new IllegalArgumentException("clash : " + uid + " is already registered as item parser");
            }
            allParsers.put(uid, s);
        }
    }


    @Override
    public NCallableSupport<HItem> parseNode(HNodeFactoryParseContext context) {
        TsonElement c = context.element();
        HEngine engine = context.engine();
        HDocumentFactory f = engine.documentFactory();
        NSession session = context.session();
        switch (c.type()) {
            case STRING:
            case BIG_COMPLEX:
            case BIG_INT:
            case BYTE:
            case CHAR:
            case DATE:
            case DATETIME:
            case DOUBLE:
            case DOUBLE_COMPLEX:
            case FLOAT:
            case FLOAT_COMPLEX:
            case INT:
            case BIG_DECIMAL:
            case BOOLEAN:
            case LONG:
            case NULL:
            case REGEX:
            case SHORT:
            case TIME:
            case MATRIX:
            case UPLET:
            case ALIAS: {
                HNodeTypeFactory p = engine.nodeTypeFactory(HNodeType.TEXT).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                break;
            }
            case NAME: {
                String name = c.toName().getName();
                String uid = HUtils.uid(name);
                HNodeTypeFactory p = engine.nodeTypeFactory(uid).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                p = engine.nodeTypeFactory(HNodeType.TEXT).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                break;
            }
            case PAIR: {
                TsonPair p = c.toPair();
                TsonElement k = p.getKey();
                TsonElement v = p.getValue();
                ObjEx kh = new ObjEx(k);
                NOptional<String> nn = kh.asString();
                if (nn.isPresent()) {
                    String nnn = NStringUtils.trim(nn.get());
                    if (nnn.equals("styles")) {
                        return NCallableSupport.of(10, () -> {
                            HITemNamedObjectParser pp = allParsers.get("styles");
                            NOptional<HItem> styles = pp.parseItem("styles", v, context);
                            return styles.get();
                        });
                    }
                }
                for (HNodeTypeFactory ff : engine.nodeTypeFactories()) {
                    NCallableSupport<HItem> uu = ff.parseNode(context);
                    if (uu.isValid()) {
                        return uu;
                    }
                }
                break;
            }
            case OBJECT:
            case FUNCTION:
            case ARRAY: {
                ObjEx ee = new ObjEx(c);
                if (NBlankable.isBlank(ee.name())) {
                    return NCallableSupport.of(10, new Supplier<HItem>() {
                        @Override
                        public HItem get() {
                            HItemList pg = new HItemList();
                            for (TsonElement child : ee.children()) {
                                NOptional<HItem> u = context.engine().newNode(child, context);
                                if (u.isPresent()) {
                                    pg.add(u.get());
                                } else {
                                    HNode node = context.node();
                                    throw new IllegalArgumentException(NMsg.ofC("invalid %s for %s",child,
                                            node==null?"document":node.type()
                                    ).toString());
                                }
                            }

//                            switch (c.type()) {
//                                case FUNCTION:
//                                case OBJECT:
//                                case ARRAY: {
//                                    for (TsonElement e : ee.args()) {
//                                        NOptional<HProp[]> u = HStyleParser.parseStyle(e, f, context);
//                                        if (u.isPresent()) {
//                                            for (HProp s : u.get()) {
//                                                pg.add(s);
//                                            }
//                                        } else {
////                                            return NOptional.ofError(NMsg.ofC("invalid %s for %s",e,context.node().type())).get();
//                                            throw new IllegalArgumentException(NMsg.ofC("invalid %s for %s",e,context.node().type()).toString());
//                                        }
//                                    }
//                                    for (TsonElement e : ee.children()) {
//                                        NOptional<HItem> u = context.engine().newNode(e, context);
//                                        if (u.isPresent()) {
//                                            pg.add(u.get());
//                                        } else {
////                                            return NOptional.ofError(NMsg.ofC("invalid %s for %s",e,context.node().type())).get();
//                                            throw new IllegalArgumentException(NMsg.ofC("invalid %s for %s",e,context.node().type()).toString());
//                                       }
//                                    }
//                                }
//                            }
                            return pg;
                        }
                    });
                } else {
                    String uid = HUtils.uid(ee.name());
                    HNodeTypeFactory p = engine.nodeTypeFactory(uid).orNull();
                    if (p != null) {
                        return p.parseNode(context);
                    }
                    HITemNamedObjectParser pp = allParsers.get(uid);
                    if (pp != null) {
                        return NCallableSupport.of(10, () -> {
                            NOptional<HItem> styles = pp.parseItem(uid, c, context);
                            if(!styles.isPresent()){
                                pp.parseItem(uid, c, context).get();
                            }
                            return styles.get();
                        });
                    }
                }
                break;
            }
        }
        throw new NIllegalArgumentException(session, NMsg.ofC("[%s] unable to resolve node : %s", context.source(), c));
    }

}
