package net.thevpc.ndoc.config;

public class NDocViewerConfig {
    private NDocProject[] recentProjects;

    public NDocProject[] getRecentProjects() {
        return recentProjects;
    }

    public NDocViewerConfig setRecentProjects(NDocProject[] recentProjects) {
        this.recentProjects = recentProjects;
        return this;
    }
}
