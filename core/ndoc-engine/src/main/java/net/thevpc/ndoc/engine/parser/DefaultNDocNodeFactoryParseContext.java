package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.parser.util.GitHelper;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NStringElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;

import java.util.*;

public class DefaultNDocNodeFactoryParseContext implements NDocNodeFactoryParseContext {
    private final NDocument document;
    private final NElement element;
    private final NDocEngine engine;
    private final List<NDocNode> nodePath = new ArrayList<>();
    private final NDocResource source;

    public DefaultNDocNodeFactoryParseContext(
            NDocument document
            , NElement element
            , NDocEngine engine
            ,
            List<NDocNode> nodePath
            , NDocResource source
    ) {
        this.document = document;
        this.element = element==null?null:NDocUtils.addCompilerDeclarationPath(element,source);
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
            String pathString = element.asStringValue().get();
            if (isVarName(pathString)) {
                return element;
            }
            if(!pathString.trim().isEmpty()) {
                for (int i = nodePath.size() - 1; i >= 0; i--) {
                    NDocResource resource = engine().computeSource(nodePath.get(i));
                    if (resource!=null) {
                        if(resource.path().isPresent()) {
                            NPath nPath = NDocUtils.resolvePath(element, resource);
                            if(nPath!=null) {
                                return NElement.ofString(nPath.toString());
                            }
                        }
                    }
                }
            }
        }
        return element;
    }

    @Override
    public NDocLogger messages() {
        return engine().messages();
    }

    public NDocument document() {
        return document;
    }

    @Override
    public NDocNodeFactoryParseContext push(NDocNode node) {
        if (node == null) {
            return this;
        }
        List<NDocNode> nodePath2 = new ArrayList<>();
        nodePath2.addAll(Arrays.asList(nodePath()));
        nodePath2.add(node);
        return new DefaultNDocNodeFactoryParseContext(document(), element, engine(), nodePath2, source);
    }

    @Override
    public NDocDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    public NDocResource source() {
        return source;
    }

    @Override
    public NDocNode node() {
        if (nodePath.isEmpty()) {
            return null;
        }
        return nodePath.get(nodePath.size() - 1);
    }

    @Override
    public NDocNode[] nodePath() {
        return nodePath.toArray(new NDocNode[0]);
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
    public NPath resolvePath(NStringElement path) {
        if(path==null){
            return null;
        }
        String s = path.asStringValue().get();
        if (NBlankable.isBlank(s)) {
            return null;
        }
        return null;
    }

    @Override
    public NPath resolvePath(String path) {
        if (NBlankable.isBlank(path)) {
            return null;
        }
        if (GitHelper.isGithubFolder(path)) {
            return resolvePath(GitHelper.resolveGithubPath(path, messages()));
        }
        NDocResource src = source;
        if (src == null) {
            if (nodePath != null) {
                for (int i = nodePath.size() - 1; i >= 0; i--) {
                    if (nodePath.get(i) != null) {
                        NDocResource ss = engine().computeSource(nodePath.get(i));
                        if (ss != null) {
                            src = ss;
                            break;
                        }
                    }
                }
            }
        }
        return NDocUtils.resolvePath(NElement.ofString(path),src);
    }
}
