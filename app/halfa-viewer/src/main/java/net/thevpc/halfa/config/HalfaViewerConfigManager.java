package net.thevpc.halfa.config;

import net.thevpc.nuts.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.tson.*;

import java.io.*;
import java.util.*;

public class HalfaViewerConfigManager {

    private NPath viewerConfigFile;

    public HalfaViewerConfigManager() {
        this(null);
    }

    public HalfaViewerConfigManager(NPath viewerConfigFile) {
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
        HalfaViewerConfig config = loadViewerConfig();
        List<HalfaProject> old = new ArrayList<>();
        HalfaProject found = null;
        if (config.getRecentProjects() != null) {
            for (HalfaProject a : config.getRecentProjects()) {
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
            HalfaProject p = new HalfaProject();
            p.setLastAccess(now);
            p.setPath(path.toString());
            old.add(p);
        }
        config.setRecentProjects(old.toArray(new HalfaProject[0]));
        saveViewerConfig(config);
    }

    public void markSaved(NPath path) {
        HalfaViewerConfig config = loadViewerConfig();
        List<HalfaProject> old = new ArrayList<>();
        HalfaProject found = null;
        if (config.getRecentProjects() != null) {
            for (HalfaProject a : config.getRecentProjects()) {
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
            HalfaProject p = new HalfaProject();
            p.setLastAccess(now);
            p.setLastSaved(now);
            p.setPath(path.toString());
            old.add(p);
        }
        config.setRecentProjects(old.toArray(new HalfaProject[0]));
        saveViewerConfig(config);
    }

    public void saveViewerConfig(HalfaViewerConfig config) {
        config = validate(config);
        TsonElement elem = Tson.serializer().serialize(config);
        viewerConfigFile.mkParentDirs();
        try (OutputStream os = viewerConfigFile.getOutputStream()) {
            Tson.writer().write(os, elem);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public NPath[] getLastAccessedPaths() {
        HalfaViewerConfig config = loadViewerConfig();
        return Arrays.stream(config.getRecentProjects()).map(x -> NPath.of(x.getPath())).toArray(NPath[]::new);
    }

    public HalfaProject[] getRecentProjects() {
        HalfaViewerConfig config = loadViewerConfig();
        return config.getRecentProjects();
    }

    public NPath getLastAccessedPath() {
        HalfaViewerConfig config = loadViewerConfig();
        HalfaProject[] recentProjects = config.getRecentProjects();
        return recentProjects.length == 0 ? null : NPath.of(recentProjects[0].getPath());
    }

    public HalfaViewerConfig loadViewerConfig() {
        HalfaViewerConfig config = null;
        if (viewerConfigFile.isRegularFile()) {
            try (InputStream is = viewerConfigFile.getInputStream()) {
                TsonDocument d = Tson.reader().readDocument(is);
                TsonSerializer serializer = Tson.serializer();
                config = serializer.deserialize(d.getContent(), HalfaViewerConfig.class);
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

    private HalfaViewerConfig validate(HalfaViewerConfig config) {
        if (config == null) {
            config = new HalfaViewerConfig();
        }
        if (config.getRecentProjects() == null) {
            config.setRecentProjects(new HalfaProject[0]);
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
                    p = compare(b.getLastAccess(),a.getLastAccess());
                    if (p != 0) {
                        return p;
                    }
                    return compare(a.getPath(), b.getPath());
                }).toArray(HalfaProject[]::new)
        );
        return config;
    }
}
