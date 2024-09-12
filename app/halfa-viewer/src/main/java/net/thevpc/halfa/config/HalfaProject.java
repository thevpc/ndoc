package net.thevpc.halfa.config;

import java.util.Date;

//public List<HalfaProject>
public class HalfaProject {
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
