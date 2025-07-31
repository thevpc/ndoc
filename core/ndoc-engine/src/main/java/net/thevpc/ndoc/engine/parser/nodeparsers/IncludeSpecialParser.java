package net.thevpc.ndoc.engine.parser.nodeparsers;

import net.thevpc.ndoc.engine.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.parser.ctrlnodes.CtrlNDocNodeInclude;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.elem.NElement;

public class IncludeSpecialParser extends NDocNodeParserBase {
    public IncludeSpecialParser() {
        super(true, NDocNodeType.CTRL_INCLUDE);
    }


    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement tsonElement = context.element();
        switch (tsonElement.type()) {
            case NAMED_UPLET: {
                NUpletElement uplet = tsonElement.asUplet().get();
                if (uplet.isNamed("include")) {
                    return NCallableSupport.valid( () -> new CtrlNDocNodeInclude(context.source(),uplet.params()));
                }
                break;
            }
        }
        return NCallableSupport.invalid(NMsg.ofC("missing include from %s", NDocUtils.snippet(tsonElement)).asError());
    }



}

