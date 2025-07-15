package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.NDocNodeRenderer;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.BufferedImage;

public interface NDocNodeRendererManager {

    NOptional<NDocNodeRenderer> getRenderer(String type);

    BufferedImage renderImage(HNode node, NDocNodeRendererConfig config);

    byte[] renderImageBytes(HNode node, NDocNodeRendererConfig config);
}
