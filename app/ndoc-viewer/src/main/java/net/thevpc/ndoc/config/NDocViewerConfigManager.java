package net.thevpc.ndoc.config;

import net.thevpc.nuts.*;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElementParser;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;

import java.io.*;
import java.util.*;

public class NDocViewerConfigManager {

    private NPath viewerConfigFile;

    public NDocViewerConfigManager() {
        this(null);
    }

    public NDocViewerConfigManager(NPath viewerConfigFile) {
        String halfaViewerConfigName = "halfa-viewer-config.tson";
        NPath appCacheFolder = NApp.of().getConfFolder();
        if (viewerConfigFile == null) {
            if (appCacheFolder == null) {
                viewerConfigFile = NWorkspace.of().getStoreLocation(NId.of("net.thevpc.halfa:halfa"), NStoreType.CACHE).resolve(halfaViewerConfigName);
            } else {
                viewerConfigFile = appCacheFolder.resolve(halfaViewerConfigName);
            }
        } else if (viewerConfigFile.isDirectory()) {
            viewerConfigFile = viewerConfigFile.resolve(halfaViewerConfigName);
        } else if (!viewerConfigFile.exists() && !viewerConfigFile.getName().endsWith(".tson")) {
            viewerConfigFile = viewerConfigFile.resolve(halfaViewerConfigName);
        }
        this.viewerConfigFile = viewerConfigFile;
    }

    public void markAccessed(NPath path) {
        NDocViewerConfig config = loadViewerConfig();
        List<NDocProject> old = new ArrayList<>();
        NDocProject found = null;
        if (config.getRecentProjects() != null) {
            for (NDocProject a : config.getRecentProjects()) {
                if (a != null && !NBlankable.isBlank(a.getPath())) {
                    if (NPath.of(a.getPath()).equals(path)) {
                        found = a;
                    }
                    old.add(a);
                }
            }
        }
        Date now = new Date();
        if (found != null) {
            found.setLastAccess(now);
        } else {
            NDocProject p = new NDocProject();
            p.setLastAccess(now);
            p.setPath(path.toString());
            old.add(p);
        }
        config.setRecentProjects(old.toArray(new NDocProject[0]));
        saveViewerConfig(config);
    }

    public void markSaved(NPath path) {
        NDocViewerConfig config = loadViewerConfig();
        List<NDocProject> old = new ArrayList<>();
        NDocProject found = null;
        if (config.getRecentProjects() != null) {
            for (NDocProject a : config.getRecentProjects()) {
                if (a != null && !NBlankable.isBlank(a.getPath())) {
                    if (NPath.of(a.getPath()).equals(path)) {
                        found = a;
                    }
                    old.add(a);
                }
            }
        }
        Date now = new Date();
        if (found != null) {
            found.setLastAccess(now);
            found.setLastSaved(now);
        } else {
            NDocProject p = new NDocProject();
            p.setLastAccess(now);
            p.setLastSaved(now);
            p.setPath(path.toString());
            old.add(p);
        }
        config.setRecentProjects(old.toArray(new NDocProject[0]));
        saveViewerConfig(config);
    }

    public void saveViewerConfig(NDocViewerConfig config) {
        config = validate(config);
        NElement elem = NElements.of().toElement(config);
        viewerConfigFile.mkParentDirs();
        try (OutputStream os = viewerConfigFile.getOutputStream()) {
            NElements.of(elem).tson().print(os);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public NPath[] getLastAccessedPaths() {
        NDocViewerConfig config = loadViewerConfig();
        return Arrays.stream(config.getRecentProjects()).map(x -> NPath.of(x.getPath())).toArray(NPath[]::new);
    }

    public NDocProject[] getRecentProjects() {
        NDocViewerConfig config = loadViewerConfig();
        return config.getRecentProjects();
    }

    public NPath getLastAccessedPath() {
        NDocViewerConfig config = loadViewerConfig();
        NDocProject[] recentProjects = config.getRecentProjects();
        return recentProjects.length == 0 ? null : NPath.of(recentProjects[0].getPath());
    }

    public NDocViewerConfig loadViewerConfig() {
        NDocViewerConfig config = null;
        if (viewerConfigFile.isRegularFile()) {
            try (InputStream is = viewerConfigFile.getInputStream()) {
                NElement d = NElementParser.ofTson().parse(is);
                config = NElements.of().fromElement(d, NDocViewerConfig.class);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        config = validate(config);
        return config;
    }

    private int compare(Object a, Object b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null && b != null) {
            return -1;
        }
        if (a != null && b == null) {
            return 1;
        }
        if (a instanceof Comparable) {
            return ((Comparable) a).compareTo(b);
        }
        throw new IllegalArgumentException("invalid comparision");
    }

    private NDocViewerConfig validate(NDocViewerConfig config) {
        if (config == null) {
            config = new NDocViewerConfig();
        }
        if (config.getRecentProjects() == null) {
            config.setRecentProjects(new NDocProject[0]);
        }
        config.setRecentProjects(Arrays.stream(config.getRecentProjects())
                .filter(x -> x != null && !NBlankable.isBlank(x.getPath()))
                .peek(x -> {
                    if (x.getLastAccess() == null) {
                        x.setLastAccess(new Date());
                    }
                })
                .sorted((a, b) -> {
                    int p;
                    p = compare(b.getLastAccess(), a.getLastAccess());
                    if (p != 0) {
                        return p;
                    }
                    return compare(a.getPath(), b.getPath());
                }).toArray(NDocProject[]::new)
        );
        return config;
    }
}
