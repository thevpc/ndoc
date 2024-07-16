package net.thevpc.halfa.engine.render.server;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.engine.renderer.pdf.PdfDocumentRenderer;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.halfa.spi.base.renderer.HNodeRendererContextBase;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.util.PagesHelper;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/document")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final HEngine engine;
    private final NSession session;

    @Autowired
    public DocumentController(HEngine engine, NSession session) {
        this.engine = engine;
        this.session = session;
    }

    @GetMapping(value = "/images", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getDocumentImages(@RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber) {
        try {
            NPath file = NPath.of("/home/mohamed/Desktop/stage/halfa/documentation/tson-doc/main.hd", session)
                    .toAbsolute()
                    .normalize();

            HEngine e = new HEngineImpl(session);
            HDocument doc = e.loadDocument(file, null).get();
            HMessageList messages = new HMessageListImpl(session, engine.computeSource(doc.root()));

            List<HNode> pages = PagesHelper.resolvePages(doc);

            if (pageNumber >= 0 && pageNumber < pages.size()) {
                byte[] imageData = createPageImage(pages.get(pageNumber), messages);
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(imageData);
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private byte[] createPageImage(HNode page, HMessageList messages) throws IOException {
        int sizeWidth = 1200;
        int sizeHeight = 1000;

        BufferedImage newImage = new BufferedImage(sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        HGraphics hg = engine.createGraphics(g);
        HNodeRenderer renderer = engine.renderManager().getRenderer(page.type()).get();
        renderer.render(page, new PdfHNodeRendererContext(engine, hg, new Dimension(sizeWidth, sizeHeight), session, messages));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(newImage, "png", bos);
        } finally {
            g.dispose();
        }

        return bos.toByteArray();
    }

    private static class PdfHNodeRendererContext extends HNodeRendererContextBase {

        public PdfHNodeRendererContext(HEngine engine, HGraphics g, Dimension size, NSession session, HMessageList messages) {
            super(engine, g, size, session, messages);
            setCapability("print", true);
            setCapability("animated", false);
        }

    }
}
