package net.thevpc.ndoc.engine.render.server;

import net.thevpc.ndoc.api.HEngine;
import net.thevpc.ndoc.api.document.HDocument;
import net.thevpc.ndoc.api.document.HMessageList;
import net.thevpc.ndoc.api.document.HMessageListImpl;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.engine.DefaultHEngine;
import net.thevpc.ndoc.spi.renderer.HNodeRendererConfig;
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

import java.util.List;

@RestController
@RequestMapping("/api/document")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final HEngine engine;

    @Autowired
    public DocumentController(HEngine engine) {
        this.engine = engine;
    }

    @GetMapping(value = "/images", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getDocumentImages(@RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber) {
        try {
            NPath file = NPath.of("/home/mohamed/Desktop/stage/halfa/documentation/tson-doc/main.ndoc")
                    .toAbsolute()
                    .normalize();

            int sizeWidth = 1200;
            int sizeHeight = 1000;
            HEngine e = new DefaultHEngine();
            HDocument doc = e.loadDocument(file, null).get();
            HMessageList messages = new HMessageListImpl(engine.computeSource(doc.root()));

            List<HNode> pages = doc.pages();

            if (pageNumber >= 0 && pageNumber < pages.size()) {
                byte[] imageData = engine.renderManager().renderImageBytes(
                        pages.get(pageNumber),
                        new HNodeRendererConfig(sizeWidth, sizeHeight)
                                .withAnimate(false)
                                .setMessages(messages)
                );
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
}
