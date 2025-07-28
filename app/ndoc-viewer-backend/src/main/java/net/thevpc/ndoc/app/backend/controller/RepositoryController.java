package net.thevpc.ndoc.app.backend.controller;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.engine.DefaultNDocLogger;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.app.backend.service.GitService;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.nuts.io.NPath;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
//@RequestMapping("/api/document")
public class RepositoryController {

    private final NDocEngine engine;
    private final GitService gitService;

    @Autowired
    public RepositoryController(NDocEngine engine, GitService gitService) {
        this.engine = engine;
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

            List<String> contents = Arrays.asList(gitService.listRepositoryContents(localPath));
            return ResponseEntity.ok(contents);
        } catch (GitAPIException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Arrays.asList("Repository not found or cannot be accessed"));
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
            NPath file = NPath.of("file:///home/mohamed/Desktop/stage/ndoc/documentation/tson-doc/main.ndoc")
                    .toAbsolute()
                    .normalize();

            NDocEngine e = new DefaultNDocEngine();
            NDocument doc = e.loadDocument(file).get();
            List<NDocNode> pages = doc.pages();
            if (pageNumber >= 0 && pageNumber < pages.size()) {
                GitService gitService = new GitService(engine);
                int sizeWidth = 1200;
                int sizeHeight = 1000;
                byte[] imageData = gitService.createPageImage(pages.get(pageNumber), sizeWidth, sizeHeight);
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



