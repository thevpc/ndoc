package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.BufferedImage;

public interface NDocNodeRendererManager {

    NOptional<NDocNodeRenderer> getRenderer(String type);

    BufferedImage renderImage(NTxNode node, NDocNodeRendererConfig config);

    byte[] renderImageBytes(NTxNode node, NDocNodeRendererConfig config);
}
