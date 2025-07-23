package net.thevpc.ndoc.engine.parser.nodes;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.parser.NDocNodeParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractNDocItemNamedObjectParser implements NDocNodeParser {
    private String id;
    private String[] aliases;

    public AbstractNDocItemNamedObjectParser(String id,String ...aliases) {
        this.id =id;
        this.aliases =aliases;
    }

    public String[] ids() {
        List<String> a=new ArrayList<>();
        a.add(id);
        a.addAll(Arrays.asList(id));
        return a.toArray(new String[0]);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(NDocEngine engine) {

    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public String[] aliases() {
        return Arrays.copyOf(aliases, aliases.length);
    }

//    @Override
//    public boolean accept(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
//        return true;
//    }


    protected NCallableSupport<NDocItem> _invalidSupport(NMsg msg, NDocNodeFactoryParseContext context) {
        msg = msg.asError();
        context.messages().log(NDocMsg.of(msg.asError()));
        return NCallableSupport.invalid(msg);
    }
    protected void _logError(NMsg nMsg, NDocNodeFactoryParseContext context) {
        context.messages().log(NDocMsg.of(nMsg.asError(), context.source()));
    }

}
