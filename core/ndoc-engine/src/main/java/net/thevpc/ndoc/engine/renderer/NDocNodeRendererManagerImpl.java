package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.ndoc.spi.NDocNodeRenderer;
import net.thevpc.ndoc.spi.base.renderer.NDocNodeRendererContextBase;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererConfig;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererManager;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class NDocNodeRendererManagerImpl implements NDocNodeRendererManager {
    private Map<String, NDocNodeRenderer> renderers;
    private NDocEngine engine;

    public NDocNodeRendererManagerImpl(NDocEngine engine) {
        this.engine = engine;
    }

    public Map<String, NDocNodeRenderer> getRenderers() {
        if (renderers == null) {
            renderers = new HashMap<>();
            for (NDocNodeRenderer renderer : ServiceLoader.load(NDocNodeRenderer.class)) {
                for (String type : renderer.types()) {
                    NOptional<NDocNodeParser> f = engine.nodeTypeFactory(type);
                    if (f.isPresent()) {
                        NDocNodeParser ntf = f.get();
                        this.renderers.put(net.thevpc.ndoc.api.util.HUtils.uid(ntf.id()), renderer);
                        String[] aliases = ntf.aliases();
                        if (aliases != null) {
                            for (String alias : aliases) {
                                if (!NBlankable.isBlank(alias)) {
                                    this.renderers.put(HUtils.uid(alias), renderer);
                                }
                            }
                        }
                    }
                }
            }
        }
        return renderers;
    }

    public NOptional<NDocNodeRenderer> getRenderer(String type) {
        NDocNodeRenderer r = getRenderers().get(type);
        if (r == null) {
            return NOptional.ofNamedEmpty("renderer for " + type);
        }
        return NOptional.of(r);
    }


    @Override
    public BufferedImage renderImage(HNode node, NDocNodeRendererConfig config) {
        int sizeWidth = config.getWidth();
        int sizeHeight = config.getHeight();
        Dimension dimension=new Dimension(sizeWidth, sizeHeight);
        Map<String, Object> capabilities=config.getCapabilities();
        HLogger messages=config.getMessages();
        BufferedImage newImage = new BufferedImage(sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        NDocGraphics hg = engine.createGraphics(g);
        NDocNodeRenderer renderer = getRenderer(node.type()).get();
        renderer.render(node, new NDocNodeRendererContextBase(engine, hg, dimension, messages) {
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
    public byte[] renderImageBytes(HNode node, NDocNodeRendererConfig config) {
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
