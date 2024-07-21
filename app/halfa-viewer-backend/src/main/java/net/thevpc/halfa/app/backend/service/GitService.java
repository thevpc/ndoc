package net.thevpc.halfa.app.backend.service;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.halfa.spi.base.renderer.HNodeRendererContextBase;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.nuts.NExecCmd;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitService {

    private final HEngine engine;
    private final NSession session;

    @Autowired
    public GitService(HEngine engine, NSession session) {
        this.engine = engine;
        this.session = session;
    }

    public NPath cloneRepositoryPath(String url, String cloneDirectoryPath) throws GitAPIException {
        String n = NPath.of(url, session).getName();
        String localFolderName = n + "-" + url.hashCode();

        NPath baseFolder = NPath.ofUserHome(session).resolve(localFolderName);
        if (baseFolder.isDirectory()) {
            NExecCmd.of(session)
                    .system()
                    .setDirectory(baseFolder.resolve(n))
                    .addCommand("git", "pull")
                    .failFast()
                    .run();
        } else {
            NExecCmd.of(session)
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

    public List<String> listRepositoryContents(String localPath, String path) throws IOException, GitAPIException {
        List<String> contents = new ArrayList<>();
        try (Repository repository = openRepository(localPath);
             TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(repository.resolve("HEAD^{tree}"));
            treeWalk.setRecursive(false);
            if (!path.isEmpty()) {
                treeWalk.setFilter(PathFilter.create(path));
            }

            while (treeWalk.next()) {
                contents.add(treeWalk.getPathString());
            }
        }
        return contents;
    }
    public String[] listRepositoryContents(String localPath){
        return NPath.of(localPath,session).list().stream().map(x->x.getName()).toArray(String[]::new);
    }

    public String getFileContent(String localPath ) throws IOException {
        File file = new File(localPath);
        return new String(Files.readAllBytes(file.toPath()));
    }

    public byte[] createPageImage( HNode page, HMessageList messages) throws IOException {
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
