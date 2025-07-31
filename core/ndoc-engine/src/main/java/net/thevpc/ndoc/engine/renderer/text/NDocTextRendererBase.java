package net.thevpc.ndoc.engine.renderer.text;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.renderer.text.NDocTextBaseRenderer;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NDocTextRendererBase extends NDocTextBaseRenderer {
    private String flavor;

    public NDocTextRendererBase(String type,String flavor) {
        super(type);
        this.flavor=flavor;
    }

    public NDocTextRendererBuilder createRichTextHelper(NDocNode p, NDocNodeRendererContext ctx) {
        NDocTextRendererFlavor f = ctx.engine().textRendererFlavor(flavor).orNull();
        if(f==null){
            ctx.engine().log().log(NMsg.ofC("TextRendererFlavor not found %s",flavor));
            f=ctx.engine().textRendererFlavor("text").get();
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<NDocNode> all=new ArrayList<>();
        String text = NDocObjEx.of(p.getPropertyValue(NDocPropName.VALUE).orNull()).asStringOrName().orElse("");
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl builder = new NDocTextRendererBuilderImpl(ctx.engine(),fg);
        f.buildText(text, new NDocTextOptions(), p, ctx, builder);
        return builder;
    }


}
