package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.BufferedImage;

public interface NDocNodeRendererManager {

    NOptional<NDocNodeRenderer> getRenderer(String type);

    BufferedImage renderImage(NDocNode node, NDocNodeRendererConfig config);

    byte[] renderImageBytes(NDocNode node, NDocNodeRendererConfig config);
}
