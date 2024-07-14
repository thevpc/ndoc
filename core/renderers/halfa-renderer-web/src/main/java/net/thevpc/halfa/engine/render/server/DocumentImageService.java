//package net.thevpc.halfa.engine.render.server;
//
//
//import net.thevpc.halfa.api.HEngine;
//import net.thevpc.halfa.api.document.HDocument;
//import net.thevpc.halfa.api.document.HMessageList;
//import net.thevpc.halfa.api.document.HMessageListImpl;
//import net.thevpc.halfa.api.model.node.HNode;
//import net.thevpc.halfa.spi.HNodeRenderer;
//import net.thevpc.halfa.spi.base.renderer.HNodeRendererContextBase;
//import net.thevpc.halfa.spi.renderer.HGraphics;
//import net.thevpc.halfa.spi.util.PagesHelper;
//import net.thevpc.nuts.NSession;
//import net.thevpc.nuts.io.NIOException;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//
//public class DocumentImageService {
//
//
//    private final HEngine engine;
//    private final NSession session;
//
//    public DocumentImageService(HEngine engine, NSession session) {
//        this.engine = engine;
//        this.session = session;
//    }
//
//    public List<byte[]> generateImages(HDocument document) {
//        List<byte[]> images = new ArrayList<>();
//        HMessageList messages = new HMessageListImpl(session, engine.computeSource(document.root()));
//        List<HNode> pages = PagesHelper.resolvePages(document);
//
//        for (HNode page : pages) {
//            images.add(createPageImage(800, 600, page, messages)); // Assuming size 800x600 for example
//        }
//
//        return images;
//    }
//
//    private byte[] createPageImage(int sizeWidth, int sizeHeight, HNode page, HMessageList messages) {
//        BufferedImage newImage = new BufferedImage(sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g = newImage.createGraphics();
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        HGraphics hg = engine.createGraphics(g);
//        HNodeRenderer renderer = engine.renderManager().getRenderer(page.type()).get();
//        renderer.render(page, new PdfHNodeRendererContext(engine, hg, new Dimension(sizeWidth, sizeHeight), session, messages));
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            ImageIO.write(newImage, "png", bos);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            g.dispose();
//        }
//
//        return bos.toByteArray();
//    }
//
//    private static class PdfHNodeRendererContext extends HNodeRendererContextBase {
//        public PdfHNodeRendererContext(HEngine engine, HGraphics g, Dimension size, NSession session, HMessageList messages) {
//            super(engine, g, size, session, messages);
//            setCapability("print", true);
//            setCapability("animated", false);
//        }
//    }
//}
