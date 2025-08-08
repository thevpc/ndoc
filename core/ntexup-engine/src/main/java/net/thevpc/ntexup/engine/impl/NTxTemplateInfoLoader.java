package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.engine.NTxTemplateInfo;
import net.thevpc.ntexup.api.log.NTxLogger;
import net.thevpc.ntexup.engine.eval.NTxGitHelper;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElementAnnotation;
import net.thevpc.nuts.elem.NElementParser;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NTxTemplateInfoLoader {

    public List<NTxTemplateInfo> loadTemplateInfo(String name, NPath path, NTxLogger log) {
        List<NTxTemplateInfo> allTemplates = new ArrayList<>();
        try {
            if (NTxGitHelper.isGithubFolder(path.toString())) {
                NPath nPath1 = NTxGitHelper.resolveGithubPath(path.toString(), log);
                if (nPath1.isRegularFile()) {
                    try {
                        loadTemplateInfo(NElementParser.ofTson().parse(nPath1), name, path, allTemplates);
                    } catch (Exception e) {
                        log.log(NMsg.ofC("unable to parse repository templates '%s' at '%s' : %s", name, path, e).asError());
                    }
                } else {
                    log.log(NMsg.ofC("repository template not found '%s' at '%s'", name, path).asWarning());
                }
            } else if (path.isLocal()) {
                if (path.isRegularFile()) {
                    try {
                        loadTemplateInfo(NElementParser.ofTson().parse(path), name, path, allTemplates);
                    } catch (Exception e) {
                        log.log(NMsg.ofC("unable to parse repository templates '%s' at '%s' : %s", name, path, e).asError());
                    }
                } else {
                    log.log(NMsg.ofC("repository template not found '%s' at '%s'", name, path).asWarning());
                }
            } else {
                log.log(NMsg.ofC("unable to parse repository templates '%s' at '%s'", name, path).asWarning());
            }
        } catch (Exception e) {
            log.log(NMsg.ofC("unable to load repository '%s' at '%s' : %s", name, path, e).asError());
        }
        return allTemplates;
    }

    private void loadTemplateInfo(NElement elem, String repoName, NPath repoPath, List<NTxTemplateInfo> allTemplates) {
        if (elem.isObject()) {
            for (NElement o : elem.asObject().get().children()) {
                loadTemplateInfo(o, repoName, repoPath, allTemplates);
            }
        } else if (elem.isString()) {
            List<String> versions = new ArrayList<>();
            String name=null;
            String localPath = elem.asStringValue().orNull();
            boolean recommended = false;
            for (NElementAnnotation annotation : elem.annotations()) {
                if (annotation.name().equals("recommended")) {
                    recommended = true;
                } else if (annotation.name().equals("name") && !annotation.params().isEmpty() && annotation.param(0).isString()) {
                    name = annotation.param(0).asStringValue().orNull();
                } else if (annotation.name().equals("binaries")) {
                    versions = annotation.params().stream().map(x->x.asStringValue().orNull()).filter(NBlankable::isNonBlank).collect(Collectors.toList());
                }
            }
            if (!NBlankable.isBlank(localPath) && !NBlankable.isBlank(name)) {
                allTemplates.add(
                        new NTxTemplateInfoImpl(
                                NStringUtils.trim(name),
                                repoPath.resolveChild(localPath).toString(),
                                recommended,
                                repoName, repoPath.toString(),
                                NStringUtils.trim(localPath),
                                versions.toArray(new String[0])
                        )
                );
            }
        }
    }
}
