package net.thevpc.ndoc.config;

import java.util.Date;

public class NDocProject {
    private String path;
    private Date lastAccess;
    private Date lastSaved;

    public String getPath() {
        return path;
    }

    public NDocProject setPath(String path) {
        this.path = path;
        return this;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public NDocProject setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
        return this;
    }

    public Date getLastSaved() {
        return lastSaved;
    }

    public NDocProject setLastSaved(Date lastSaved) {
        this.lastSaved = lastSaved;
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(path);
    }
}
