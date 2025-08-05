package net.thevpc.ntexup.engine.parser.nodeparsers;

import net.thevpc.ntexup.engine.parser.NDocNodeParserBase;
import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.ntexup.engine.parser.ctrlnodes.CtrlNTxNodeInclude;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.elem.NElement;

public class IncludeSpecialParser extends NDocNodeParserBase {
    public IncludeSpecialParser() {
        super(true, NTxNodeType.CTRL_INCLUDE);
    }


    @Override
    public NCallableSupport<NTxItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement tsonElement = context.element();
        switch (tsonElement.type()) {
            case NAMED_UPLET: {
                NUpletElement uplet = tsonElement.asUplet().get();
                if (uplet.isNamed("include")) {
                    return NCallableSupport.ofValid( () -> new CtrlNTxNodeInclude(context.source(),uplet.params()));
                }
                break;
            }
        }
        return NCallableSupport.ofInvalid(NMsg.ofC("missing include from %s", NDocUtils.snippet(tsonElement)).asError());
    }



}

