package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HLineImpl extends AbstractHNodeTypeFactory {
    public HLineImpl() {
        super(false, HNodeType.LINE);
    }

    @Override
    protected boolean processArg(String id, HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()){
            case PAIR:{
                TsonPair pp = e.toPair();
                TsonElement k = pp.getKey();
                TsonElement v = pp.getValue();
                ObjEx ph=new ObjEx(k);
                NOptional<String> n = ph.asString();
                if(n.isPresent()){
                    switch (HUtils.uid(n.get())){
                        case "from":{
                            p.setProperty(HProp.ofDouble2(HPropName.FROM, new ObjEx(v).asDouble2().get()));
                            return true;
                        }
                        case "to":{
                            p.setProperty(HProp.ofDouble2(HPropName.FROM, new ObjEx(v).asDouble2().get()));
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public TsonElement toTson(HNode item) {
        HNode node=(HNode) item;
        HProp from = node.getProperty(HPropName.FROM).orElse(HProp.ofDouble2(HPropName.TO, 0,0));
        HProp to = node.getProperty(HPropName.TO).orElse(HProp.ofDouble2(HPropName.TO, 100,100));
        return ToTsonHelper.of(
                        node, engine()
                ).addChildren(
                        from==null?null:Tson.pair("from", HUtils.toTson(from)),
                        to==null?null:Tson.pair("to", HUtils.toTson(to))
                )
                .build();
    }

}
