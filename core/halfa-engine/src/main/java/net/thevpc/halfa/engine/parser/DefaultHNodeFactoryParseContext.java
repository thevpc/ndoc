package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HLogger;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.engine.parser.util.GitHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class DefaultHNodeFactoryParseContext implements HNodeFactoryParseContext {
    private final HLogger messages;
    private final HDocument document;
    private final TsonElement element;
    private final HEngine engine;
    private final List<HNode> nodePath = new ArrayList<>();
    private final HResource source;

    public DefaultHNodeFactoryParseContext(
            HDocument document
            , TsonElement element
            , HEngine engine
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
    public TsonElement asPathRef(TsonElement element) {
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
                                return Tson.of(nPath.toString());
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

    public HDocument document() {
        return document;
    }

    @Override
    public HNodeFactoryParseContext push(HNode node) {
        if (node == null) {
            return this;
        }
        List<HNode> nodePath2 = new ArrayList<>();
        nodePath2.addAll(Arrays.asList(nodePath()));
        nodePath2.add(node);
        return new DefaultHNodeFactoryParseContext(document(), element, engine(), nodePath2, source, messages);
    }

    @Override
    public HDocumentFactory documentFactory() {
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
    public HEngine engine() {
        return engine;
    }

    @Override
    public TsonElement element() {
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
        return HUtils.resolvePath(Tson.of(path),src);
    }
}
