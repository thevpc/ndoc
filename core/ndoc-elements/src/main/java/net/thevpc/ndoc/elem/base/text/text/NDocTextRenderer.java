package net.thevpc.ndoc.elem.base.text.text;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.renderer.text.NDocTextBaseRenderer;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.renderer.NDocTextRendererFlavor;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class NDocTextRenderer extends NDocTextBaseRenderer {
    private Map<String, NDocTextRendererFlavor> flavors = new HashMap<>();
    private NDocTextRendererFlavor defaultFlavor = new NDocTextRendererFlavorDefault();

    public NDocTextRenderer() {
        super(NDocNodeType.TEXT);
        ServiceLoader<NDocTextRendererFlavor> all=ServiceLoader.load(NDocTextRendererFlavor.class);
        for (NDocTextRendererFlavor h : all) {
            String t = h.type();
            if(flavors.containsKey(t)){
                throw new IllegalArgumentException("already registered text flavor "+t);
            }
            flavors.put(t, h);
        }
        flavors.put("ntf", new NDocTextRendererFlavorNTF());
    }

    public NDocTextRendererBuilder createRichTextHelper(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<NDocNode> all=new ArrayList<>();
        String lang = NStringUtils.trim(NDocObjEx.of(p.getPropertyValue(NDocPropName.LANG).orNull()).asStringOrName().orElse(""));
        String text = NDocObjEx.of(p.getPropertyValue(NDocPropName.VALUE).orNull()).asStringOrName().orElse("");
        NDocTextRendererFlavor f = flavors.get(lang);
        if (f == null) {
            f = defaultFlavor;
        }
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl builder = new NDocTextRendererBuilderImpl(flavors,fg);
        f.buildText(text, new NDocTextOptions(), p, ctx, builder);
        return builder;
    }


}
