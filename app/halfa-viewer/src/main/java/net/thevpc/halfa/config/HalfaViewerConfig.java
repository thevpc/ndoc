package net.thevpc.halfa.config;

public class HalfaViewerConfig {
    private HalfaProject[] recentProjects;

    public HalfaProject[] getRecentProjects() {
        return recentProjects;
    }

    public HalfaViewerConfig setRecentProjects(HalfaProject[] recentProjects) {
        this.recentProjects = recentProjects;
        return this;
    }
}
