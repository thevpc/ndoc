package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.ntexup.api.parser.NTxNodeParser;
import net.thevpc.ntexup.api.renderer.NTxNodeRenderer;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererConfig;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererManager;
import net.thevpc.ntexup.engine.impl.DefaultNTxEngine;
import net.thevpc.ntexup.engine.ext.NTxNodeCustomBuilderContextImpl;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class NTxNodeRendererManagerImpl implements NTxNodeRendererManager {
    private Map<String, NTxNodeRenderer> renderers;
    private NTxEngine engine;

    public NTxNodeRendererManagerImpl(NTxEngine engine) {
        this.engine = engine;
    }

    public synchronized Map<String, NTxNodeRenderer> getRenderers() {
        if (renderers == null) {
            renderers = new HashMap<>();
            for (NTxNodeRenderer renderer : engine.loadServices(NTxNodeRenderer.class)) {
                registerRenderer(renderer);
            }
            for (NTxNodeCustomBuilderContextImpl cb : ((DefaultNTxEngine) engine).builderContexts()) {
                NTxNodeRenderer renderer=cb.createRenderer();
                registerRenderer(renderer);
            }
        }
        return renderers;
    }

    private void registerRenderer(NTxNodeRenderer renderer) {
        for (String type : renderer.types()) {
            NOptional<NTxNodeParser> f = engine.nodeTypeParser(type);
            if (f.isPresent()) {
                NTxNodeParser ntf = f.get();
                this.renderers.put(NTxUtils.uid(ntf.id()), renderer);
                String[] aliases = ntf.aliases();
                if (aliases != null) {
                    for (String alias : aliases) {
                        if (!NBlankable.isBlank(alias)) {
                            this.renderers.put(NTxUtils.uid(alias), renderer);
                        }
                    }
                }
            }
        }
    }

    public NOptional<NTxNodeRenderer> getRenderer(String type) {
        NTxNodeRenderer r = getRenderers().get(type);
        if (r == null) {
            return NOptional.ofNamedEmpty("renderer for " + type);
        }
        return NOptional.of(r);
    }


    @Override
    public BufferedImage renderImage(NTxNode node, NTxNodeRendererConfig config) {
        int sizeWidth = config.getWidth();
        int sizeHeight = config.getHeight();
        Dimension dimension=new Dimension(sizeWidth, sizeHeight);
        Map<String, Object> capabilities=config.getCapabilities();
        BufferedImage newImage = new BufferedImage(sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        NTxGraphics hg = engine.createGraphics(g);
        NTxNodeRenderer renderer = getRenderer(node.type()).get();
        renderer.render(node, new NTxNodeRendererContextBase(engine, hg, dimension) {
            {
                if (capabilities != null) {
                    for (Map.Entry<String, Object> cc : capabilities.entrySet()) {
                        setCapability(cc.getKey(), cc.getValue());
                    }
                }
            }
        });
        g.dispose();
        return newImage;
    }

    @Override
    public byte[] renderImageBytes(NTxNode node, NTxNodeRendererConfig config) {
        BufferedImage newImage = renderImage(node, config);
        String imageTypeOk="png";
        if(config.getCapabilities()!=null) {
            Object imageType = config.getCapabilities().get("imageType");
            if(imageType instanceof String) {
                imageTypeOk=(String) imageType;
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(newImage, imageTypeOk, bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }
}
