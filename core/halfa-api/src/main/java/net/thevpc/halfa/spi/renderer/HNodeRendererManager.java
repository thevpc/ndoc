package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public interface HNodeRendererManager {

    NOptional<HNodeRenderer> getRenderer(String type);


    BufferedImage renderImage(HNode node, HNodeRendererConfig config, NSession session);

    byte[] renderImageBytes(HNode node, HNodeRendererConfig config, NSession session);
}
