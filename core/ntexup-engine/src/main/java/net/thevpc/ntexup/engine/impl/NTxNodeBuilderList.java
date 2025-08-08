package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.parser.NTxNodeParser;
import net.thevpc.ntexup.api.renderer.NTxNodeRenderer;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.ntexup.engine.ext.NTxNodeCustomBuilderContextImpl;
import net.thevpc.nuts.NOut;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NNameFormat;

import java.util.*;
import java.util.function.Consumer;

public class NTxNodeBuilderList extends NtxServiceListImpl2<NTxNodeBuilder> {
    private List<NTxNodeCustomBuilderContextImpl> customBuilderContexts = new ArrayList<>();

    public NTxNodeBuilderList(DefaultNTxEngine engine) {
        super("node builder", NTxNodeBuilder.class, engine);
    }

    @Override
    protected String idOf(NTxNodeBuilder v) {
        return v.getClass().getName();
    }

    @Override
    public void dump(Consumer<NMsg> out) {
        super.dump(out);
        out.accept(NMsg.ofC("%s services : %s", NMsg.ofStyledPrimary1("builderContexts"), customBuilderContexts.size()));
        for (NTxNodeCustomBuilderContextImpl e : customBuilderContexts) {
            Set<String> a = new LinkedHashSet<>(Arrays.asList(e.aliases()));
            if (a.isEmpty()) {
                out.accept(NMsg.ofC("\t [BASE] %s : %s", NMsg.ofStyledPrimary1(e.id()), e.builder().getClass()));
            } else {
                out.accept(NMsg.ofC("\t [BASE] %s (%s) : %s", NMsg.ofStyledPrimary1(e.id()), String.join(",", a), e.builder().getClass()));
            }
        }
    }

    @Override
    protected void onAfterNewService(NTxNodeBuilder h, boolean custom) {
        NTxNodeCustomBuilderContextImpl b = new NTxNodeCustomBuilderContextImpl(h, engine);
        h.build(b);
        b.compile();
        String id = b.id();
        if (customBuilderContexts.stream().anyMatch(x -> NNameFormat.equalsIgnoreFormat(x.id(), id))) {
            return;
        }
        customBuilderContexts.add(b);
        NTxTextRendererFlavor f = b.createTextFlavor();
        if (f != null) {
            if (custom) {
                engine.textFlavors.addCustom(f);
            } else {
                engine.textFlavors.addBase(f);
            }
        }
        NTxNodeParser p = b.createParser();
        if (p != null) {
            if (custom) {
                engine.nodeTypeFactories.addCustom(p);
            } else {
                engine.nodeTypeFactories.addBase(p);
            }
        }
        NTxNodeRenderer r = b.createRenderer();
        if (r != null) {
            if (custom) {
                engine.renderers.addCustom(r);
            } else {
                engine.renderers.addBase(r);
            }
        }
    }

    public List<NTxNodeCustomBuilderContextImpl> builderContexts() {
        return customBuilderContexts;
    }
}
