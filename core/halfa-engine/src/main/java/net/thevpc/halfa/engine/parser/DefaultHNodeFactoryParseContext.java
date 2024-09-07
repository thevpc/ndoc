package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.util.NPathHResource;
import net.thevpc.halfa.engine.parser.util.GitHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class DefaultHNodeFactoryParseContext implements HNodeFactoryParseContext {
    private final HMessageList messages;
    private final HDocument document;
    private final TsonElement element;
    private final HEngine engine;
    private final NSession session;
    private final List<HNode> nodePath = new ArrayList<>();
    private final HResource source;

    public DefaultHNodeFactoryParseContext(
            HDocument document
            , TsonElement element
            , HEngine engine
            , NSession session
            , List<HNode> nodePath
            , HResource source
            , HMessageList messages
    ) {
        this.messages = messages;
        this.document = document;
        this.element = element;
        this.engine = engine;
        this.session = session;
        this.source = source;
        this.nodePath.addAll(nodePath);
    }

    @Override
    public HMessageList messages() {
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
        return new DefaultHNodeFactoryParseContext(document(), element, engine(), session, nodePath2, source, messages);
    }

    @Override
    public HDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    public HResource source() {
        return source;
    }

    @Override
    public NSession session() {
        return session;
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
            return resolvePath(GitHelper.resolveGithubPath(path, session));
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
        NPath base;
        if (src instanceof NPathHResource) {
            NPath sp = ((NPathHResource) src).getPath();
            if (sp.isRegularFile()) {
                sp = sp.getParent();
            }
            if (sp != null) {
                base = sp.resolve(path);
            } else {
                base = NPath.of(path, session);
            }
        } else {
            base = NPath.of(path, session);
        }
        return base;
    }
}
