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
import net.thevpc.nuts.util.*;
import net.thevpc.tson.*;

import java.math.BigDecimal;
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
                ObjEx kh = ObjEx.of(k);
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
                ObjEx ee = ObjEx.of(c);
                if (NBlankable.isBlank(ee.name())) {
                    return NCallableSupport.of(10, new Supplier<HItem>() {
                        @Override
                        public HItem get() {
                            return parsNoNameBloc(context);
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
                            if (!styles.isPresent()) {
                                pp.parseItem(uid, c, context).get();
                            }
                            return styles.get();
                        });
                    }
                }
                break;
            }
        }
        context.messages().addError(NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c),context.source());
        throw new NIllegalArgumentException(session, NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c));
    }

    private boolean isRootBloc(HNodeFactoryParseContext context){
        HNode[] nodes = context.nodePath();
        if(nodes.length>1){
            return false;
        }
        if(nodes.length==1){
            if(!Objects.equals(nodes[0].type(),HNodeType.PAGE_GROUP)){
                return false;
            }
        }
        TsonElement c = context.element();
//        HEngine engine = context.engine();
        for (TsonAnnotation a : c.getAnnotations()) {
            String nn = a.getName();
            if (!NBlankable.isBlank(nn)) {
                return false;
            }
            boolean foundHalfa=false;
            boolean foundVersion=false;
            boolean foundOther=false;
            for (TsonElement cls : a.all()) {
                switch (cls.type()){
                    case STRING:{
                        if(cls.toStr().getValue().equalsIgnoreCase("halfa")){
                            foundHalfa=true;
                        }else if(isVersionString(cls.toStr().getValue())){
                            foundVersion=true;
                        }else{
                            foundOther=true;
                        }
                        break;
                    }
                    case NAME:{
                        if(cls.toName().getName().equalsIgnoreCase("halfa")){
                            foundHalfa=true;
                        }else if(isVersionString(cls.toStr().getValue())){
                            foundVersion=true;
                        }else{
                            foundOther=true;
                        }
                        break;
                    }
                    default:{
                        if(cls.type().isNumber()){
                            BigDecimal bi = cls.toNumber().getBigDecimal();
                            foundVersion=true;
                        }else{
                            foundOther=true;
                        }
                        break;
                    }
                }
            }
            if(foundHalfa){
                return true;
            }
        }
        return false;
    }

    private boolean isVersionString(String value) {
        if(value!=null){
            value=value.trim();
            if(value.length()>0){
                if(value.charAt(0)>='0' && value.charAt(0)>='9'){
                    for (char c : value.toCharArray()) {
                        if(!Character.isAlphabetic(c) && !Character.isDigit(c)
                                && c!='.' && c!='_' && c!='-'

                        ){
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private HItem parsNoNameBloc(HNodeFactoryParseContext context){
        TsonElement c = context.element();
        HEngine engine = context.engine();
        HDocumentFactory f = engine.documentFactory();
        HashSet<String> allAncestors = null;
        HashSet<String> allStyles = null;
        ObjEx ee = ObjEx.of(c);
        for (TsonAnnotation a : c.getAnnotations()) {
            String nn = a.getName();
            if (!NBlankable.isBlank(nn)) {
                if (allAncestors == null) {
                    allAncestors = new HashSet<>();
                }
                allAncestors.add(HUtils.uid(nn));
            }
            // add classes as well

            for (TsonElement cls : a.all()) {
                if (allStyles == null) {
                    allStyles = new HashSet<>();
                }
                NOptional<String[]> ss = ObjEx.of(cls).asStringArrayOrString();
                if (ss.isPresent()) {
                    allStyles.addAll(Arrays.asList(ss.get()));
                }
            }
        }
        HNode node = context.node();
        if ((allStyles != null || allAncestors != null) && ! isRootBloc(context)) {
            HNode pg = f.ofStack();
            pg.setAncestors(allAncestors==null?null:allAncestors.toArray(new String[0]));
            pg.setStyleClasses(allStyles==null?null:allStyles.toArray(new String[0]));
            for (TsonElement child : ee.body()) {
                NOptional<HItem> u = context.engine().newNode(child, context);
                if (u.isPresent()) {
                    pg.append(u.get());
                } else {
                    throw new IllegalArgumentException(NMsg.ofC("invalid %s for %s", child,
                            node == null ? "document" : node.type()
                    ).toString());
                }
            }
            return pg;
        } else {
            HItemList pg = new HItemList();
            for (TsonElement child : ee.body()) {
                NOptional<HItem> u = context.engine().newNode(child, context);
                if (u.isPresent()) {
                    pg.add(u.get());
                } else {
                    throw new IllegalArgumentException(NMsg.ofC("invalid %s for %s", child,
                            node == null ? "document" : node.type()
                    ).toString());
                }
            }
            return pg;
        }
    }

}
