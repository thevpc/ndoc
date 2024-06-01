package net.thevpc.halfa.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.engine.impl.DefaultHNodeFactoryParseContext;
import net.thevpc.halfa.engine.impl.HDocumentCompiler;
import net.thevpc.halfa.engine.impl.HDocumentRendererFactoryContextImpl;
import net.thevpc.halfa.engine.impl.HPropCalculator;
import net.thevpc.halfa.engine.nodes.HDocumentFactoryImpl;
import net.thevpc.halfa.engine.parser.DefaultHDocumentItemParserFactory;
import net.thevpc.halfa.spi.nodes.HNodeTypeFactory;
import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.*;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonDocument;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonReader;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.nodes.HNodeParserFactory;

/**
 * @author vpc
 */
public class HEngineImpl implements HEngine {

    private NSession session;
    private List<HDocumentRendererFactory> documentRendererFactories;
    private List<HNodeParserFactory> nodeParserFactories;

    private Map<String, HNodeTypeFactory> nodeTypeFactories;
    private Map<String, String> nodeTypeAliases;
    private HDocumentFactory factory;
    private HPropCalculator hPropCalculator = new HPropCalculator();
    HDocumentCompiler hDocumentCompiler;

    public HEngineImpl(NSession session) {
        this.session = session;
        hDocumentCompiler = new HDocumentCompiler(this);
    }

    public NSession getSession() {
        return session;
    }

    @Override
    public HDocumentFactory documentFactory() {
        if (factory == null) {
            factory = new HDocumentFactoryImpl(this);
        }
        return factory;
    }


    @Override
    public NOptional<HItem> newNode(TsonElement element, HNodeFactoryParseContext ctx) {
        HNodeFactoryParseContext newContext = new DefaultHNodeFactoryParseContext(element, this, session,
                ctx == null ? new ArrayList<>() : Arrays.asList(ctx.nodePath())
                , ctx == null ? null : ctx.source()
        );
        return NCallableSupport.resolve(
                        nodeParserFactories().stream()
                                .map(x -> x.parseNode(newContext)),
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

    @Override
    public List<HNodeTypeFactory> nodeTypeFactories() {
        return new ArrayList<>(nodeTypeFactories0().values());
    }

    private Map<String, HNodeTypeFactory> nodeTypeFactories0() {
        if (nodeTypeFactories == null) {
            nodeTypeFactories = new HashMap<>();
            nodeTypeAliases = new HashMap<>();
            ServiceLoader<HNodeTypeFactory> renderers = ServiceLoader.load(HNodeTypeFactory.class);
            for (HNodeTypeFactory renderer : renderers) {
                addNodeTypeFactory(renderer);
            }
        }
        return nodeTypeFactories;
    }


    public NOptional<HNodeTypeFactory> nodeTypeFactory(String id) {
        id = NStringUtils.trim(id);
        if (!id.isEmpty()) {
            id = NNameFormat.LOWER_KEBAB_CASE.format(id);
            HNodeTypeFactory o = nodeTypeFactories0().get(id);
            if (o != null) {
                return NOptional.of(o);
            }
            String oid = nodeTypeAliases.get(id);
            if (oid != null) {
                return nodeTypeFactory(oid);
            }
        }
        return NOptional.ofNamedEmpty("NodeType " + id);
    }

    public void addNodeTypeFactory(HNodeTypeFactory renderer) {
        String id = NStringUtils.trim(renderer.id());
        if (id.isEmpty()) {
            throw new IllegalArgumentException("invalid id in " + renderer.getClass().getName());
        }
        Set<String> allIds = new HashSet<>();
        allIds.add(id);
        if (renderer.aliases() != null) {
            for (String alias : renderer.aliases()) {
                String aid = NStringUtils.trim(alias);
                if (!aid.isEmpty()) {
                    allIds.add(aid);
                }
            }
        }
        for (String i : allIds) {
            NOptional<HNodeTypeFactory> o = nodeTypeFactory(i);
            if (o.isPresent()) {
                HNodeTypeFactory oo = o.get();
                String oid = NNameFormat.LOWER_KEBAB_CASE.format(oo.id());
                throw new IllegalArgumentException("already registered " + i + "@" + renderer.getClass().getName() + " as " + oid + "@" + oo.getClass().getName());
            }
        }
        nodeTypeFactories.put(id, renderer);
        for (String i : allIds) {
            nodeTypeAliases.put(i, id);
        }
        renderer.init(this);
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


    public HDocument compileDocument(HDocument document) {
        return hDocumentCompiler.compile(document);
    }

    public boolean validateNode(HNode node) {
        return nodeTypeFactory(node.type()).get().validateNode(node);
    }

    @Override
    public NOptional<HDocument> loadDocument(NPath path) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                return loadTsonDocument(path)
                        .flatMap(x -> convertDocument(x, path))
                        ;
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
                HDocument doc = documentFactory().ofDocument();
                for (NPath nPath : all) {
                    NOptional<HItem> d = loadNode(doc.root(), nPath);
                    if (!d.isPresent()) {
                        return NOptional.ofError(s -> NMsg.ofC("invalid file %s", nPath));
                    }
                    updateSource(d.get(), nPath);
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
                HNode i = (HNode) item;
                if (i.source() == null) {
                    i.setSource(source);
                }
            }
        }
    }

    @Override
    public NOptional<HItem> loadNode(HNode into, NPath path) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                NOptional<HItem> d = loadNode0(into, path);
                if (d.isPresent()) {
                    updateSource(d.get(), path);
                }
                return d;
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && x.getName().endsWith(".hd")).toList();
                all.sort((a, b) -> a.getName().compareTo(b.getName()));
                HItem node = null;
                for (NPath nPath : all) {
                    NOptional<HItem> d = loadNode0((node instanceof HNode) ? (HNode) node : null, nPath);
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
        HDocument docd = documentFactory().ofDocument();
        docd.root().setSource(source);
        NOptional<HItem> r = newNode(c, null);
        if (r.isPresent()) {
            docd.root().append(r.get());
            return NOptional.of(docd);
        }
        return NOptional.ofError(s -> NMsg.ofC("invalid %s", r.getMessage().apply(s)));
    }


    private NOptional<HItem> loadNode0(HNode into, NPath path) {
        TsonReader tr = Tson.reader();
        TsonDocument doc;
        try {
            doc = tr.readDocument(path.toPath().get());
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
        TsonElement c = doc.getContent();
        ArrayList<HNode> parents = new ArrayList<>();
        if (into != null) {
            parents.add(into);
        }
        return newNode(c, new DefaultHNodeFactoryParseContext(
                null,
                this,
                session,
                parents,
                path
        ));
    }

    @Override
    public TsonElement toTson(HDocument doc) {
        HNode r = doc.root();
        return nodeTypeFactory(r.type()).get().toTson(r);
    }


    @Override
    public NOptional<HProp> computeProperty(HNode node, String propertyName) {
        return hPropCalculator.computeProperty(node, propertyName);
    }

    @Override
    public List<HProp> computeProperties(HNode node) {
        return hPropCalculator.computeProperties(node);
    }

    @Override
    public List<HProp> computeInheritedProperties(HNode node) {
        return hPropCalculator.computeInheritedProperties(node);
    }

    @Override
    public <T> NOptional<T> computePropertyValue(HNode node, String propertyName) {
        return hPropCalculator.computePropertyValue(node, propertyName);
    }

    @Override
    public Object computeSource(HNode node) {
        return hDocumentCompiler.computeSource(node);
    }

}
