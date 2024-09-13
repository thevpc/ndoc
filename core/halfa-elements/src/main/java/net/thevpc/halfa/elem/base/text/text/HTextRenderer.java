package net.thevpc.halfa.elem.base.text.text;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.text.HTextBaseRenderer;
import net.thevpc.halfa.spi.renderer.text.HTextRendererBuilder;
import net.thevpc.halfa.spi.HTextRendererFlavor;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class HTextRenderer extends HTextBaseRenderer {
    private Map<String, HTextRendererFlavor> flavors = new HashMap<>();
    private HTextRendererFlavor defaultFlavor = new HTextRendererFlavorDefault();

    public HTextRenderer() {
        super(HNodeType.TEXT);
        ServiceLoader<HTextRendererFlavor> all=ServiceLoader.load(HTextRendererFlavor.class);
        for (HTextRendererFlavor h : all) {
            String t = h.type();
            if(flavors.containsKey(t)){
                throw new IllegalArgumentException("already registered text flavor "+t);
            }
            flavors.put(t, h);
        }
        flavors.put("ntf", new HTextRendererFlavorNTF());
    }

    public HTextRendererBuilder createRichTextHelper(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<HNode> all=new ArrayList<>();
        String lang = NStringUtils.trim(ObjEx.of(p.getPropertyValue(HPropName.LANG).orNull()).asStringOrName().orElse(""));
        String text = ObjEx.of(p.getPropertyValue(HPropName.VALUE).orNull()).asStringOrName().orElse("");
        HTextRendererFlavor f = flavors.get(lang);
        if (f == null) {
            f = defaultFlavor;
        }
        Paint fg = HValueByName.getForegroundColor(p, ctx,true);
        HTextRendererBuilderImpl builder = new HTextRendererBuilderImpl(flavors,fg);
        f.buildText(text, p, ctx, builder);
        return builder;
    }


}
