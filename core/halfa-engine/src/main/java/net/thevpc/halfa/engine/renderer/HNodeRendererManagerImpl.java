package net.thevpc.halfa.engine.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.HNodeParser;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.halfa.spi.base.renderer.HNodeRendererContextBase;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererConfig;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class HNodeRendererManagerImpl implements HNodeRendererManager {
    private Map<String, HNodeRenderer> renderers;
    private HEngine engine;

    public HNodeRendererManagerImpl(HEngine engine) {
        this.engine = engine;
    }

    public Map<String, HNodeRenderer> getRenderers() {
        if (renderers == null) {
            renderers = new HashMap<>();
            for (HNodeRenderer renderer : ServiceLoader.load(HNodeRenderer.class)) {
                for (String type : renderer.types()) {
                    NOptional<HNodeParser> f = engine.nodeTypeFactory(type);
                    if (f.isPresent()) {
                        HNodeParser ntf = f.get();
                        this.renderers.put(HUtils.uid(ntf.id()), renderer);
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

    public NOptional<HNodeRenderer> getRenderer(String type) {
        HNodeRenderer r = getRenderers().get(type);
        if (r == null) {
            return NOptional.ofNamedEmpty("renderer for " + type);
        }
        return NOptional.of(r);
    }


    @Override
    public BufferedImage renderImage(HNode node, HNodeRendererConfig config, NSession session) {
        int sizeWidth = config.getWidth();
        int sizeHeight = config.getHeight();
        Dimension dimension=new Dimension(sizeWidth, sizeHeight);
        Map<String, Object> capabilities=config.getCapabilities();
        HMessageList messages=config.getMessages();
        BufferedImage newImage = new BufferedImage(sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        HGraphics hg = engine.createGraphics(g);
        HNodeRenderer renderer = getRenderer(node.type()).get();
        renderer.render(node, new HNodeRendererContextBase(engine, hg, dimension, session, messages) {
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
    public byte[] renderImageBytes(HNode node, HNodeRendererConfig config, NSession session) {
        BufferedImage newImage = renderImage(node, config, session);
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
