package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.spi.nodes.HNodeTypeFactory;
import net.thevpc.halfa.spi.renderer.HNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;

import java.util.*;

public class RendererManagerImpl implements HNodeRendererManager {
    private Map<String, HNodeRenderer> renderers;
    private HEngine engine;

    public RendererManagerImpl(HEngine engine) {
        this.engine = engine;
    }

    public Map<String, HNodeRenderer> getRenderers() {
        if(renderers==null){
            renderers = new HashMap<>();
            for (HNodeRenderer renderer : ServiceLoader.load(HNodeRenderer.class)) {
                for (String type : renderer.types()) {
                    NOptional<HNodeTypeFactory> f=engine.nodeTypeFactory(type);
                    if(f.isPresent()){
                        HNodeTypeFactory ntf = f.get();
                        this.renderers.put(HUtils.uid(ntf.id()),renderer);
                        String[] aliases = ntf.aliases();
                        if(aliases!=null){
                            for (String alias : aliases) {
                                if(!NBlankable.isBlank(alias)){
                                    this.renderers.put(HUtils.uid(alias),renderer);
                                }
                            }
                        }
                    }
                }
            }
        }
        return renderers;
    }

    public NOptional<HNodeRenderer> getRenderer(String type){
        HNodeRenderer r = getRenderers().get(type);
        if (r == null) {
            return NOptional.ofNamedEmpty("renderer for "+type);
        }
        return NOptional.of(r);
    }

}
