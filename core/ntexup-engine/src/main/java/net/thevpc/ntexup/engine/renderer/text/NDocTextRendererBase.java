package net.thevpc.ntexup.engine.renderer.text;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.eval.NDocValueByName;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererFlavor;
import net.thevpc.ntexup.api.renderer.text.NDocTextOptions;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NDocTextRendererBase extends NDocTextBaseRenderer {
    private String flavor;

    public NDocTextRendererBase(String type,String flavor) {
        super(type);
        this.flavor=flavor;
    }

    public NDocTextRendererBuilder createRichTextHelper(NTxNode p, NDocNodeRendererContext ctx) {
        NDocTextRendererFlavor f = ctx.engine().textRendererFlavor(flavor).orNull();
        if(f==null){
            ctx.engine().log().log(NMsg.ofC("TextRendererFlavor not found %s",flavor));
            f=ctx.engine().textRendererFlavor("text").get();
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<NDocNode> all=new ArrayList<>();
        String text = NDocValue.of(p.getPropertyValue(NDocPropName.VALUE).orNull()).asStringOrName().orElse("");
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl builder = new NDocTextRendererBuilderImpl(ctx.engine(),fg);
        f.buildText(text, new NDocTextOptions(), p, ctx, builder);
        return builder;
    }


}
