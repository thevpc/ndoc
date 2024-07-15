package net.thevpc.halfa.app.backend.controller;

import net.thevpc.halfa.app.backend.service.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class RepositoryController {

    @Autowired
    private GitService gitService;

    @GetMapping("/repo")
    public String getRepository(@RequestParam String url, Model model) {
      String localPath = "/home/mohamed/7git2";

//        File localPath = File.createTempFile("TestGitRepository", "");
//        localPath.delete();

        try {
            gitService.cloneRepository(url, localPath);
            List<String> contents = gitService.listRepositoryContents(localPath, "");
            model.addAttribute("localPath", localPath);
            model.addAttribute("contents", contents);
            return "repository";
        } catch (GitAPIException | IOException e) {
            model.addAttribute("error", "Repository not found or cannot be accessed");
            return "error";
        }
    }

    @GetMapping("/repo/contents")
    public String getRepositoryContents(@RequestParam String localPath, @RequestParam String path, Model model) {
        try {
            List<String> contents = gitService.listRepositoryContents(localPath, path);
            model.addAttribute("localPath", localPath);
            model.addAttribute("contents", contents);
            return "contents";
        } catch (IOException | GitAPIException e) {
            model.addAttribute("error", "Contents not found");
            return "error";
        }
    }

    @GetMapping("/repo/content")
    public String getFileContent(@RequestParam String localPath, @RequestParam String path, Model model) {
        try {
            String fileContent = gitService.getFileContent(localPath, path);
            model.addAttribute("fileContent", fileContent);
            return "file_content";
        } catch (IOException e) {
            model.addAttribute("error", "Error retrieving file content: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/repo/content/details")
    public String getRepositoryContentDetails(
            @RequestParam String localPath,
            @RequestParam String path,
            Model model
    ) {
        try {
            String fileContent = gitService.getFileContent(localPath, path);
            model.addAttribute("fileContent", fileContent);
            return "file_content";
        } catch (IOException e) {
            model.addAttribute("error", "Error retrieving file content: " + e.getMessage());
            return "error";
        }
    }
}
