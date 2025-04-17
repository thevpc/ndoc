package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.engine.parser.util.GitHelper;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;

import net.thevpc.nuts.elem.NElement;

import java.util.*;

public class DefaultNDocNodeFactoryParseContext implements NDocNodeFactoryParseContext {
    private final HLogger messages;
    private final NDocument document;
    private final NElement element;
    private final NDocEngine engine;
    private final List<HNode> nodePath = new ArrayList<>();
    private final HResource source;

    public DefaultNDocNodeFactoryParseContext(
            NDocument document
            , NElement element
            , NDocEngine engine
            ,
            List<HNode> nodePath
            , HResource source
            , HLogger messages
    ) {
        this.messages = messages;
        this.document = document;
        this.element = element;
        this.engine = engine;
        this.source = source;
        this.nodePath.addAll(nodePath);
    }

    private boolean isVarName(String name) {
        return name.matches("[$][a-zA-Z][a-zA-Z0-9_-]*]]");
    }

    @Override
    public NElement asPathRef(NElement element) {
        if (element.isAnyString()) {
            String pathString = element.stringValue();
            if (isVarName(pathString)) {
                return element;
            }
            if(!pathString.trim().isEmpty()) {
                for (int i = nodePath.size() - 1; i >= 0; i--) {
                    HResource resource = engine().computeSource(nodePath.get(i));
                    if (resource!=null) {
                        if(resource.path().isPresent()) {
                            NPath nPath = HUtils.resolvePath(element, resource);
                            if(nPath!=null) {
                                return NElements.of().of(nPath.toString());
                            }
                        }
                    }
                }
            }
        }
        return element;
    }

    @Override
    public HLogger messages() {
        return messages;
    }

    public NDocument document() {
        return document;
    }

    @Override
    public NDocNodeFactoryParseContext push(HNode node) {
        if (node == null) {
            return this;
        }
        List<HNode> nodePath2 = new ArrayList<>();
        nodePath2.addAll(Arrays.asList(nodePath()));
        nodePath2.add(node);
        return new DefaultNDocNodeFactoryParseContext(document(), element, engine(), nodePath2, source, messages);
    }

    @Override
    public NDocDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    public HResource source() {
        return source;
    }

    @Override
    public HNode node() {
        if (nodePath.isEmpty()) {
            return null;
        }
        return nodePath.get(nodePath.size() - 1);
    }

    @Override
    public HNode[] nodePath() {
        return nodePath.toArray(new HNode[0]);
    }

    @Override
    public NDocEngine engine() {
        return engine;
    }

    @Override
    public NElement element() {
        return element;
    }

    @Override
    public NPath resolvePath(NPath path) {
        if (path.isAbsolute()) {
            return path;
        }
        return resolvePath(path.toString());
    }

    @Override
    public NPath resolvePath(String path) {
        if (NBlankable.isBlank(path)) {
            return null;
        }
        if (GitHelper.isGithubFolder(path)) {
            return resolvePath(GitHelper.resolveGithubPath(path, messages));
        }
        HResource src = source;
        if (src == null) {
            if (nodePath != null) {
                for (int i = nodePath.size() - 1; i >= 0; i--) {
                    if (nodePath.get(i) != null) {
                        HResource ss = engine().computeSource(nodePath.get(i));
                        if (ss != null) {
                            src = ss;
                            break;
                        }
                    }
                }
            }
        }
        return HUtils.resolvePath(NElements.of().of(path),src);
    }
}
