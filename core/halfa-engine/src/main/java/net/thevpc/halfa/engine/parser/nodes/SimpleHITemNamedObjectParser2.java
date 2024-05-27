//package net.thevpc.halfa.engine.parser.nodes;
//
//import net.thevpc.halfa.HDocumentFactory;
//import net.thevpc.halfa.api.node.HNode;
//import net.thevpc.nuts.util.NNameFormat;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//
//public class SimpleHITemNamedObjectParser2 extends SimpleHITemNamedObjectParser {
//    private String node;
//    private static String[] _id(String n,String... ids){
//        Set<String> ss=new HashSet<>();
//        ss.addAll(Arrays.asList(ids));
//        ss.add(NNameFormat.LOWER_KEBAB_CASE.format(n));
//        return ss.toArray(new String[0]);
//    }
//    public SimpleHITemNamedObjectParser2(String node, String... ids) {
//        super(_id(node,ids));
//        this.node=node;
//    }
//
//    protected HNode node(HDocumentFactory f){
//        return f.of(node);
//    }
//
//
//}
