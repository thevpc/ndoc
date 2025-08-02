package net.thevpc.ndoc.api.engine;

public interface NDocTemplateInfo {
    String repoUrl();

    String repoName();

    String name();

    String localPath();

    String url();

    boolean recommended();
}
