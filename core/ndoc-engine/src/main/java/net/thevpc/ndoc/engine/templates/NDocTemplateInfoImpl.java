package net.thevpc.ndoc.engine.templates;

import net.thevpc.ndoc.api.engine.NDocTemplateInfo;
import net.thevpc.nuts.util.NBlankable;

import java.util.Objects;

public class NDocTemplateInfoImpl implements NDocTemplateInfo {
    private String name;
    private String localPath;
    private String url;
    private String repoUrl;
    private String repoName;
    private boolean recommended;

    public NDocTemplateInfoImpl(String name, String url, boolean recommended, String repoName, String repoUrl, String localPath) {
        this.name = name;
        this.localPath = localPath;
        this.repoUrl = repoUrl;
        this.repoName = repoName;
        this.url = url;
        this.recommended = recommended;
    }


    @Override
    public String repoUrl() {
        return repoUrl;
    }

    @Override
    public String repoName() {
        return repoName;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String localPath() {
        return localPath;
    }

    public String url() {
        return url;
    }

    @Override
    public boolean recommended() {
        return recommended;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (NBlankable.isBlank(name)) {
            sb.append("<noname>");
        } else {
            sb.append(name.trim());
        }
        if (!NBlankable.isBlank(repoName)) {
            sb.append("(");
            sb.append(repoName.trim());
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        NDocTemplateInfoImpl that = (NDocTemplateInfoImpl) object;
        return recommended == that.recommended && Objects.equals(name, that.name) && Objects.equals(localPath, that.localPath) && Objects.equals(url, that.url) && Objects.equals(repoUrl, that.repoUrl) && Objects.equals(repoName, that.repoName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, localPath, url, repoUrl, repoName, recommended);
    }
}
