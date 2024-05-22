package net.thevpc.halfa.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.HDocumentFactoryImpl;
import net.thevpc.halfa.engine.parser.DefaultHDocumentItemParserFactory;
import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonDocument;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonReader;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.nodes.HNodeParserFactory;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public class HEngineImpl implements HEngine {

    private NSession session;
    private List<HDocumentRendererFactory> documentRendererFactories;
    private List<HNodeParserFactory> nodeParserFactories;

    public HEngineImpl(NSession session) {
        this.session = session;
    }

    public NSession getSession() {
        return session;
    }

    @Override
    public HDocumentFactory documentFactory() {
        return new HDocumentFactoryImpl();
    }


    @Override
    public NOptional<HItem> newDocumentRoot(TsonElement element) {
        return newNode(element, null,
                new HashSet<>(Arrays.asList(HNodeType.PAGE, HNodeType.PAGE_GROUP)),
                null, null);
    }

    @Override
    public NOptional<HItem> newPageChild(TsonElement element, HNode currentNode, HNodeFactoryParseContext ctx) {
        Set<HNodeType> expected = new HashSet<>(Arrays.asList(HNodeType.values()));
        expected.remove(HNodeType.PAGE);
        expected.remove(HNodeType.PAGE_GROUP);
        return newNode(element, currentNode, expected, ctx, ctx.source());
    }

    @Override
    public NOptional<HItem> newDocumentChild(TsonElement element, HNode currentNode, HNodeFactoryParseContext ctx) {
        Set<HNodeType> expected = new HashSet<>();
        expected.add(HNodeType.PAGE);
        expected.add(HNodeType.PAGE_GROUP);
        return newNode(element, currentNode, expected, ctx, ctx.source());
    }

    @Override
    public NOptional<HItem> newNode(TsonElement element, HNode currentNode, Set<HNodeType> expected, HNodeFactoryParseContext ctx0, Object source) {
        List<HNode> parents = new ArrayList<>();
        if (ctx0 != null) {
            parents.addAll(Arrays.asList(ctx0.parents()));
        }
        if (currentNode != null) {
            parents.add(currentNode);
        }
        Set<HNodeType> expected0 = new HashSet<>();
        if (expected != null) {
            for (HNodeType e : expected) {
                if (e != null) {
                    expected0.add(e);
                }
            }
        }
        if (expected0.isEmpty()) {
            expected0.addAll(Arrays.asList(HNodeType.values()));
        }
        HNodeFactoryParseContext ctx = new DefaultHNodeFactoryParseContext(element, this, session, parents, expected0,source);
        return NCallableSupport.resolve(
                        nodeParserFactories().stream()
                                .map(x -> x.parseNode(ctx)),
                        s -> NMsg.ofC("missing %s", "factory"))
                .toOptional();
    }

    @Override
    public HDocumentStreamRenderer newPdfRenderer() {
        return newStreamRenderer("pdf");
    }

    @Override
    public HDocumentScreenRenderer newScreenRenderer() {
        HDocumentRenderer u = newRenderer("screen");
        if (u instanceof HDocumentScreenRenderer) {
            return (HDocumentScreenRenderer) u;
        }
        throw new IllegalArgumentException("resolved renderer is not a valid screen renderer");
    }

    @Override
    public HDocumentStreamRenderer newStreamRenderer(String type) {
        HDocumentRenderer u = newRenderer(type);
        if (u instanceof HDocumentStreamRenderer) {
            return (HDocumentStreamRenderer) u;
        }
        throw new IllegalArgumentException("resolved renderer is not a valid stream renderer");
    }

    @Override
    public HDocumentRenderer newRenderer(String type) {
        HDocumentRendererFactoryContext ctx = new HDocumentRendererFactoryContextImpl(this, type);
        return NCallableSupport.resolve(
                        documentRendererFactories().stream()
                                .map(x -> x.<HDocumentStreamRenderer>createDocumentRenderer(ctx)),
                        s -> NMsg.ofC("missing StreamRenderer %s", type))
                .call(session);
    }

    private List<HDocumentRendererFactory> documentRendererFactories() {
        if (documentRendererFactories == null) {
            ServiceLoader<HDocumentRendererFactory> renderers = ServiceLoader.load(HDocumentRendererFactory.class);
            List<HDocumentRendererFactory> loaded = new ArrayList<>();
            for (HDocumentRendererFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.documentRendererFactories = loaded;
        }
        return documentRendererFactories;
    }

    private List<HNodeParserFactory> nodeParserFactories() {
        if (nodeParserFactories == null) {
            ServiceLoader<HNodeParserFactory> renderers = ServiceLoader.load(HNodeParserFactory.class);
            List<HNodeParserFactory> loaded = new ArrayList<>();
            loaded.add(new DefaultHDocumentItemParserFactory());
            for (HNodeParserFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.nodeParserFactories = loaded;
        }
        return nodeParserFactories;
    }


    @Override
    public NOptional<HDocument> loadDocument(NPath path) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                return loadTsonDocument(path).flatMap(x -> convertDocument(x, path));
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && x.getName().endsWith(".hd")).toList();
                if (all.isEmpty()) {
                    return NOptional.ofError(s -> NMsg.ofC("invalid file %s", path));
                }
                NPath main = null;
                for (String mainFiled : new String[]{
                        "main.hd",
                }) {
                    NPath m = all.stream().filter(x -> mainFiled.equals(x.getName())).findFirst().orElse(null);
                    if (m != null) {
                        main = m;
                        all.remove(m);
                        break;
                    }
                }
                all.sort((a, b) -> a.getName().compareTo(b.getName()));
                if (main != null) {
                    all.add(0, main);
                }
                HDocument doc = documentFactory().document();
                for (NPath nPath : all) {
                    NOptional<HItem> d = loadNode(doc.root(), new HashSet<>(Arrays.asList(HNodeType.values())), nPath);
                    if (!d.isPresent()) {
                        return NOptional.ofError(s -> NMsg.ofC("invalid file %s", nPath));
                    }
                    updateSource(d.get(),nPath);
                    doc.root().append(d.get());
                }
                return NOptional.of(doc);
            } else {
                return NOptional.ofError(s -> NMsg.ofC("invalid file %s", path));
            }
        }
        return NOptional.ofError(s -> NMsg.ofC("file does not exist %s", path));
    }

    private void updateSource(HItem item, Object source) {
        if (item != null) {
            if (item instanceof HItemList) {
                for (HItem hItem : ((HItemList) item).getItems()) {
                    updateSource(hItem, source);
                }
            } else if (item instanceof HNode) {
                ((HNode) item).setSource(source);
            }
        }
    }

    @Override
    public NOptional<HItem> loadNode(HNode into, Set<HNodeType> expected, NPath path) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                NOptional<HItem> d = loadNode0(into, expected,path);
                if (d.isPresent()) {
                    updateSource(d.get(), path);
                }
                return d;
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && x.getName().endsWith(".hd")).toList();
                all.sort((a, b) -> a.getName().compareTo(b.getName()));
                HItem node = null;
                for (NPath nPath : all) {
                    NOptional<HItem> d = loadNode0((node instanceof HNode) ? (HNode) node : null, expected, nPath);
                    if (!d.isPresent()) {
                        return NOptional.ofError(s -> NMsg.ofC("invalid file %s", nPath));
                    }
                    updateSource(d.get(), path);
                    if (node == null) {
                        node = d.get();
                    } else {
                        if (node instanceof HNode) {
                            ((HNode) node).mergeNode(d.get());
                        } else if (node instanceof HItemList) {
                            node = new HItemList().addAll(((HItemList) node).getItems()).add(node);
                        } else {
                            node = new HItemList().add(node).add(d.get());
                        }
                    }
                }
                return NOptional.of(node);
            } else {
                return NOptional.ofError(s -> NMsg.ofC("invalid file %s", path));
            }
        }
        return NOptional.ofError(s -> NMsg.ofC("file does not exist %s", path));
    }

    private NOptional<TsonDocument> loadTsonDocument(InputStream is) {
        TsonReader tr = Tson.reader();
        TsonDocument doc;
        try {
            doc = tr.readDocument(is);
        } catch (IOException ex) {
            return NOptional.ofNamedError("error loading tson document", ex);
        }
        return NOptional.of(doc);
    }

    private NOptional<TsonDocument> loadTsonDocument(NPath is) {
        TsonReader tr = Tson.reader();
        TsonDocument doc;
        try {
            doc = tr.readDocument(is.toPath().get());
        } catch (IOException ex) {
            return NOptional.ofNamedError("error loading tson document", ex);
        }
        return NOptional.of(doc);
    }

    public NOptional<HDocument> loadDocument(InputStream is) {
        return loadTsonDocument(is).flatMap(x -> convertDocument(x, "stream"));
    }

    public NOptional<HDocument> convertDocument(TsonDocument doc, Object source) {
        if (doc == null) {
            return NOptional.ofNamedEmpty("document");
        }
        TsonElement c = doc.getContent();
        HDocument docd = documentFactory().document();
        docd.root().setSource(source);
        NOptional<HItem> r = newDocumentRoot(c);
        if (r.isPresent()) {
            docd.root().mergeNode(r.get());
            return NOptional.of(docd);
        }
        return NOptional.ofError(s -> NMsg.ofC("invalid %s", r.getMessage().apply(s)));
    }


    public NOptional<HItem> loadNode0(HNode into, Set<HNodeType> expected, NPath path) {
        TsonReader tr = Tson.reader();
        TsonDocument doc;
        try {
            doc = tr.readDocument(path.toPath().get());
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
        TsonElement c = doc.getContent();
        return newNode(c, into, expected, null, path);
    }

}
