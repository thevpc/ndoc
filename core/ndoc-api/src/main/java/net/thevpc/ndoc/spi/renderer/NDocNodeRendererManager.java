package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.NDocNodeRenderer;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.BufferedImage;

public interface NDocNodeRendererManager {

    NOptional<NDocNodeRenderer> getRenderer(String type);

    BufferedImage renderImage(NDocNode node, NDocNodeRendererConfig config);

    byte[] renderImageBytes(NDocNode node, NDocNodeRendererConfig config);
}
