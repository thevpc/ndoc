package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.BufferedImage;

public interface HNodeRendererManager {

    NOptional<HNodeRenderer> getRenderer(String type);


    BufferedImage renderImage(HNode node, HNodeRendererConfig config);

    byte[] renderImageBytes(HNode node, HNodeRendererConfig config);
}
