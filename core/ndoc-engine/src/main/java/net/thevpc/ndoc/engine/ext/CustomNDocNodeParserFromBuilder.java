package net.thevpc.ndoc.engine;

import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
import net.thevpc.ndoc.api.util.ToElementHelper;
import net.thevpc.nuts.elem.NElement;

class CustomNDocNodeParserFromBuilder extends NDocNodeParserBase {
    private final MyNDocNodeCustomBuilderContext myNDocNodeCustomBuilderContext;

    public CustomNDocNodeParserFromBuilder(MyNDocNodeCustomBuilderContext myNDocNodeCustomBuilderContext) {
        super(true, myNDocNodeCustomBuilderContext.id, myNDocNodeCustomBuilderContext.aliases);
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public NElement toElem(NDocNode item) {
        if (myNDocNodeCustomBuilderContext.toElem != null) {
            NElement u = myNDocNodeCustomBuilderContext.toElem.toElem(item, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        if (myNDocNodeCustomBuilderContext.knownArgNames != null) {
            return ToElementHelper.of(
                            item,
                            engine()
                    )
                    .addChildrenByName(myNDocNodeCustomBuilderContext.knownArgNames.toArray(new String[0]))
                    .build();
        }
        return super.toElem(item);
    }

    @Override
    protected boolean processArgument(NDocArgumentParseInfo info) {
        if (myNDocNodeCustomBuilderContext.processSingleArgumentList != null) {
            for (NDocNodeCustomBuilderContext.ProcessParamAction a : myNDocNodeCustomBuilderContext.processSingleArgumentList) {
                if (a.processParam(info, myNDocNodeCustomBuilderContext)) {
                    return true;
                }
            }
            if (myNDocNodeCustomBuilderContext.createdParser.defaultProcessArgument(info)) {
                return true;
            }
            return false;
        }
        return super.processArgument(info);
    }
}
