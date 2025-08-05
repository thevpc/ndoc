package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.BufferedImage;

public interface NTxNodeRendererManager {

    NOptional<NTxNodeRenderer> getRenderer(String type);

    BufferedImage renderImage(NTxNode node, NTxNodeRendererConfig config);

    byte[] renderImageBytes(NTxNode node, NTxNodeRendererConfig config);
}
