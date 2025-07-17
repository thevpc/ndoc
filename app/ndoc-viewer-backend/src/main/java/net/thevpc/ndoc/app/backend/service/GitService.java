package net.thevpc.ndoc.app.backend.service;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererConfig;
import net.thevpc.nuts.NExecCmd;
import net.thevpc.nuts.io.NPath;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class GitService {

    private final NDocEngine engine;

    @Autowired
    public GitService(NDocEngine engine) {
        this.engine = engine;
    }

    public NPath cloneRepositoryPath(String url, String cloneDirectoryPath) throws GitAPIException {
        String n = NPath.of(url).getName();
        String localFolderName = n + "-" + url.hashCode();

        NPath baseFolder = NPath.ofUserHome().resolve(localFolderName);
        if (baseFolder.isDirectory()) {
            NExecCmd.of()
                    .system()
                    .setDirectory(baseFolder.resolve(n))
                    .addCommand("git", "pull")
                    .failFast()
                    .run();
        } else {
            NExecCmd.of()
                    .system()
                    .setDirectory(baseFolder)
                    .addCommand("git", "clone", url)
                    .failFast()
                    .run();
        }
        return baseFolder.resolve(localFolderName);
    }

    public Git cloneRepository(String url, String cloneDirectoryPath) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(url)
                .setDirectory(new File(cloneDirectoryPath))
                .call();
    }

    public Repository openRepository(String localPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.setGitDir(new File(localPath + "/.git"))
                .readEnvironment()
                .findGitDir()
                .build();
    }

    //    public List<String> listRepositoryContents(String localPath, String path) throws IOException, GitAPIException {
//        List<String> contents = new ArrayList<>();
//        try (Repository repository = openRepository(localPath);
//             TreeWalk treeWalk = new TreeWalk(repository)) {
//            treeWalk.addTree(repository.resolve("HEAD^{tree}"));
//            treeWalk.setRecursive(false);
//            if (!path.isEmpty()) {
//                treeWalk.setFilter(PathFilter.create(path));
//            }
//
//            while (treeWalk.next()) {
//                contents.add(treeWalk.getPathString());
//            }
//        }
//        return contents;
//    }
    public String[] listRepositoryContents(String localPath) {
        return NPath.of(localPath).list().stream().map(x -> x.getName()).toArray(String[]::new);
    }

    public String getFileContent(String localPath) throws IOException {
        File file = new File(localPath);
        return new String(Files.readAllBytes(file.toPath()));
    }

    public byte[] createPageImage(NDocNode page, int width, int height, NDocLogger messages) throws IOException {
        return engine.renderManager().renderImageBytes(
                page,
                new NDocNodeRendererConfig((int) width, (int) height)
                        .withAnimate(false)
                        .setMessages(messages)
        );
    }

}
