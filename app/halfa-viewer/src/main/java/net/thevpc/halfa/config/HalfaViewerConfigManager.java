package net.thevpc.halfa.config;

import net.thevpc.nuts.NId;
import net.thevpc.nuts.NLocations;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.NStoreType;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.tson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HalfaViewerConfigManager {
    private NPath file;
    private NSession session;

    public HalfaViewerConfigManager(NSession session) {
        this(null, session);
    }

    public HalfaViewerConfigManager(NPath file, NSession session) {
        this.session = session;
        if (file == null) {
            NPath appCacheFolder = session.getAppConfFolder();
            if (appCacheFolder == null) {
                file = NLocations.of(session).getStoreLocation(NId.of("net.thevpc.halfa:halfa").get(), NStoreType.CACHE).resolve("viewer-projects.tson");
            } else {
                file = appCacheFolder.resolve("viewer-projects.tson");
            }
        } else if (file.isDirectory()) {
            file = file.resolve("viewer-projects.tson");
        } else if (!file.exists() && !file.getName().endsWith(".tson")) {
            file = file.resolve("viewer-projects.tson");
        }
        this.file = file;
    }


    public void markAccessed(NPath path) {
        HalfaViewerConfig config = loadConfig();
        List<HalfaProject> old = new ArrayList<>();
        HalfaProject found = null;
        if (config.recentProjects != null) {
            for (HalfaProject a : config.recentProjects) {
                if (a != null && !NBlankable.isBlank(a.path)) {
                    if (NPath.of(a.path, session).equals(path)) {
                        found = a;
                    }
                    old.add(a);
                }
            }
        }
        Date now = new Date();
        if (found != null) {
            found.lastAccess = now;
        } else {
            HalfaProject p = new HalfaProject();
            p.lastAccess = now;
            p.path = path.toString();
            old.add(p);
        }
        config.recentProjects = old.toArray(new HalfaProject[0]);
        saveConfig(config);
    }

    public void markSaved(NPath path) {
        HalfaViewerConfig config = loadConfig();
        List<HalfaProject> old = new ArrayList<>();
        HalfaProject found = null;
        if (config.recentProjects != null) {
            for (HalfaProject a : config.recentProjects) {
                if (a != null && !NBlankable.isBlank(a.path)) {
                    if (NPath.of(a.path, session).equals(path)) {
                        found = a;
                    }
                    old.add(a);
                }
            }
        }
        Date now = new Date();
        if (found != null) {
            found.lastAccess = now;
            found.lastSaved = now;
        } else {
            HalfaProject p = new HalfaProject();
            p.lastAccess = now;
            p.lastSaved = now;
            p.path = path.toString();
            old.add(p);
        }
        config.recentProjects = old.toArray(new HalfaProject[0]);
        saveConfig(config);
    }

    public void saveConfig(HalfaViewerConfig config) {
        if (config == null) {
            config = new HalfaViewerConfig();
        }
        if (config.recentProjects == null) {
            config.recentProjects = new HalfaProject[0];
        }
        config.recentProjects = Arrays.stream(config.recentProjects)
                .filter(x -> x != null && !NBlankable.isBlank(x.path))
                .peek(x -> {
                    if (x.lastAccess == null) {
                        x.lastAccess = new Date();
                    }
                })
                .sorted((a, b) -> {
                    int p;
                    p = compare(a.lastAccess, b.lastAccess);
                    if (p != 0) {
                        return p;
                    }
                    return compare(a.path, b.path);
                }).toArray(HalfaProject[]::new);
        TsonElement elem = Tson.serializer().serialize(config);
        file.mkParentDirs();
        try (OutputStream os = file.getOutputStream()) {
            Tson.writer().write(os, elem);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public NPath[] getLastAccessedPaths() {
        HalfaViewerConfig config = loadConfig();
        return Arrays.stream(config.getRecentProjects()).map(x -> NPath.of(x.getPath(), session)).toArray(NPath[]::new);
    }

    public HalfaProject[] getRecentProjects() {
        HalfaViewerConfig config = loadConfig();
        return config.getRecentProjects();
    }

    public NPath getLastAccessedPath() {
        HalfaViewerConfig config = loadConfig();
        HalfaProject[] recentProjects = config.getRecentProjects();
        return recentProjects.length == 0 ? null : NPath.of(recentProjects[0].getPath(), session);
    }

    public HalfaViewerConfig loadConfig() {
        HalfaViewerConfig config = null;
        if (file.isRegularFile()) {
            try (InputStream is = file.getInputStream()) {
                TsonDocument d = Tson.reader().readDocument(is);
                TsonSerializer serializer = Tson.serializer();
                config = serializer.deserialize(d.getContent(), HalfaViewerConfig.class);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        if (config == null) {
            config = new HalfaViewerConfig();
        }
        if (config.recentProjects == null) {
            config.recentProjects = new HalfaProject[0];
        }
        return config;
    }

    public static class HalfaViewerConfig {
        private HalfaProject[] recentProjects;

        public HalfaProject[] getRecentProjects() {
            return recentProjects;
        }
    }

    //public List<HalfaProject>
    public static class HalfaProject {
        private String path;
        private Date lastAccess;
        private Date lastSaved;

        public String getPath() {
            return path;
        }

        public HalfaProject setPath(String path) {
            this.path = path;
            return this;
        }

        public Date getLastAccess() {
            return lastAccess;
        }

        public HalfaProject setLastAccess(Date lastAccess) {
            this.lastAccess = lastAccess;
            return this;
        }

        public Date getLastSaved() {
            return lastSaved;
        }

        public HalfaProject setLastSaved(Date lastSaved) {
            this.lastSaved = lastSaved;
            return this;
        }

        @Override
        public String toString() {
            return String.valueOf(path);
        }
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
}
