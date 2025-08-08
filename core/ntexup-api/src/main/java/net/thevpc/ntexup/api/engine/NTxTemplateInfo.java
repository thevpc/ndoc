package net.thevpc.ntexup.api.engine;

import net.thevpc.nuts.elem.NElement;

import java.util.List;

public interface NTxTemplateInfo {
    NTxTemplateInfo withName(String name);

    NTxTemplateInfo withRecommended(boolean recommended);

    NTxTemplateInfo withBinaryVersions(String[] binaryVersions);

    NTxTemplateInfo withLocalPath(String localPath);

    NTxTemplateInfo withUrl(String url);

    NTxTemplateInfo withRepoUrl(String repoUrl);

    NTxTemplateInfo withRepoName(String repoName);

    String repoUrl();

    String repoName();

    String name();

    String localPath();

    String url();

    List<String> binaryVersions();
    boolean recommended();
    NElement toElement();
}
