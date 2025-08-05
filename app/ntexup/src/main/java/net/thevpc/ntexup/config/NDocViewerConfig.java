package net.thevpc.ntexup.config;

public class NDocViewerConfig {
    private NTxProject[] recentProjects;

    public NTxProject[] getRecentProjects() {
        return recentProjects;
    }

    public NDocViewerConfig setRecentProjects(NTxProject[] recentProjects) {
        this.recentProjects = recentProjects;
        return this;
    }
}
