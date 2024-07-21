package net.thevpc.halfa.app.backend.controller;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.app.backend.service.GitService;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.util.PagesHelper;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
//@RequestMapping("/api/document")
public class RepositoryController {

    private final HEngine engine;
    private final NSession session;
    private final GitService gitService;

    @Autowired
    public RepositoryController(HEngine engine, NSession session, GitService gitService) {
        this.engine = engine;
        this.session = session;
        this.gitService = gitService;
    }

    @GetMapping("/version")
    public String version() {
        return "1.0";
    }

    @GetMapping("/repo")
    public ResponseEntity<List<String>> getRepository(@RequestParam String url) {
        String localPath = "/home/mohamed/test02";

        try {
            File localDir = new File(localPath);

            if (localDir.exists()) {
                File[] files = localDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteDirectory(file);
                        } else {
                            file.delete();
                        }
                    }
                }
                localDir.delete();
            }

            if (!localDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + localPath);
            }

            gitService.cloneRepository(url, localPath);

            List<String> contents = gitService.listRepositoryContents(localPath, "");
            return ResponseEntity.ok(contents);
        } catch (GitAPIException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Repository not found or cannot be accessed"));
        }
    }

    private void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        if (!directory.delete()) {
            throw new IOException("Failed to delete directory: " + directory);
        }
    }


    @GetMapping("/repo/contents")
    public String[] getRepositoryContents(@RequestParam String localPath) {
        return gitService.listRepositoryContents(localPath);
    }

    @GetMapping("/file/content")
    public ResponseEntity<String> getFileContent(@RequestParam String localPath) {
        try {
            String content = gitService.getFileContent(localPath);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(content.length())
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file content: " + e.getMessage());
        }
    }

    @GetMapping(value = "/images", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getDocumentImages(
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber) {
        try {
            NPath file = NPath.of("file:///home/mohamed/Desktop/stage/halfa/documentation/tson-doc/main.hd", session)
                    .toAbsolute()
                    .normalize();

            HEngine e = new HEngineImpl(session);
            HDocument doc = e.loadDocument(file, null).get();
            HMessageList messages = new HMessageListImpl(session, engine.computeSource(doc.root()));

            List<HNode> pages = PagesHelper.resolvePages(doc);

            if (pageNumber >= 0 && pageNumber < pages.size()) {
                GitService gitService = new GitService(engine, session);
                byte[] imageData = gitService.createPageImage(pages.get(pageNumber), messages);
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



