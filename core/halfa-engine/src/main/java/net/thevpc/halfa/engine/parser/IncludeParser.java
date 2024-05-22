package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPageGroup;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IncludeParser {

    public static NOptional<HNode> parseInclude(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case FUNCTION: {
                List<TsonElement> u = e.toFunction().all();
                if (u.size() == 1) {
                    return NOptional.ofError(s -> NMsg.ofC("invalid arg count for include. Expected a single path argument : %s", e));
                }
                for (TsonElement ee : u) {
                    TsonElementParseHelper t=new TsonElementParseHelper(ee);
                    NOptional<String[]> p = t.asStringOrNameArray();
                    if(p.isPresent()){
                        for (String sp : p.get()) {
                            HNode[] parents = context.parents();
                            HNode putInto = parents.length == 0 ? null : parents[parents.length - 1];
                            for (NPath nPath : calculatePath(sp, putInto,context.session())) {
                                if(nPath.isRegularFile()) {
                                    NOptional<HDocument> se = context.engine().loadDocument(nPath);
                                    if(se.isPresent()){
                                        HDocument d = se.get();
                                        HPageGroup r = d.root();
                                        putInto.addClasses(r.classes().toArray(new String[0]));
                                        for (HStyle style : r.styles()) {
                                            putInto.set(style);
                                        }
                                        if(putInto instanceof HContainer) {
                                            for (HStyleRule rule : ((HContainer) putInto).rules()) {
                                                ((HContainer) putInto).addRule(rule);
                                            }
                                            for (HNode n : r.children()) {
                                                ((HContainer) putInto).add(n);
                                            }
                                        }
                                    }else{
                                        return NOptional.ofError(s -> NMsg.ofC("invalid include. error loading : %s", nPath));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return NOptional.ofNamedEmpty("not include");
    }


    private static List<NPath> calculatePath(String s, HNode n, NSession session){
        Object src=null;
        HNode p=n;
        while(p!=null){
            Object ss = p.getSource();
            if(ss!=null){
                src=ss;
                break;
            }
            p=p.parent();
        }
        //TODO process glob patterns (*)
        if(src instanceof NPath){
            return new ArrayList<>(Arrays.asList(((NPath)src).resolve(s)));
        }else{
            return new ArrayList<>(Arrays.asList(NPath.of(s,session)));
        }
    }
}
