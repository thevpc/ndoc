package net.thevpc.ndoc.elem.base.text.text;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.renderer.text.NDocTextBaseRenderer;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class NDocTextRendererBase extends NDocTextBaseRenderer {
    private NDocTextRendererFlavor defaultFlavor = new NDocTextRendererFlavorDefault();
    private String flavor;

    public NDocTextRendererBase(String type,String flavor) {
        super(type);

    }

    public NDocTextRendererBuilder createRichTextHelper(NDocNode p, NDocNodeRendererContext ctx) {
        NDocTextRendererFlavor f = ctx.textRendererFlavor(flavor).orNull();
        if(f==null){
            ctx.engine().log().log(NMsg.ofC("TextRendererFlavor not found %s",flavor));
            f=defaultFlavor;
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<NDocNode> all=new ArrayList<>();
        String lang = NStringUtils.trim(NDocObjEx.of(p.getPropertyValue(NDocPropName.LANG).orNull()).asStringOrName().orElse(""));
        String text = NDocObjEx.of(p.getPropertyValue(NDocPropName.VALUE).orNull()).asStringOrName().orElse("");
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl builder = new NDocTextRendererBuilderImpl(flavors,fg);
        f.buildText(text, new NDocTextOptions(), p, ctx, builder);
        return builder;
    }


}
