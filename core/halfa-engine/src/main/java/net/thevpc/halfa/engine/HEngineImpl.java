package net.thevpc.halfa.engine;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.*;
import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.engine.parser.DefaultHNodeFactoryParseContext;
import net.thevpc.halfa.engine.document.HDocumentCompiler;
import net.thevpc.halfa.engine.parser.util.GitHelper;
import net.thevpc.halfa.engine.renderer.HDocumentRendererFactoryContextImpl;
import net.thevpc.halfa.engine.document.HPropCalculator;
import net.thevpc.halfa.engine.document.HDocumentFactoryImpl;
import net.thevpc.halfa.engine.parser.DefaultHDocumentItemParserFactory;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.engine.parser.HDocumentLoadingResultImpl;
import net.thevpc.halfa.api.util.HResourceFactory;
import net.thevpc.halfa.engine.renderer.HGraphicsImpl;
import net.thevpc.halfa.engine.renderer.HNodeRendererManagerImpl;
import net.thevpc.halfa.spi.HNodeParser;
import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NSession;
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

    private Map<String, HNodeParser> nodeTypeFactories;
    private Map<String, String> nodeTypeAliases;
    private HDocumentFactory factory;
    private HPropCalculator hPropCalculator = new HPropCalculator();
    private HDocumentCompiler hDocumentCompiler;
    private HNodeRendererManager rendererManager;

    public HEngineImpl(NSession session) {
        this.session = session;
        hDocumentCompiler = new HDocumentCompiler(this, session);
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
        NAssert.requireNonNull(ctx, "context");
        NAssert.requireNonNull(element, "element");
        HNodeFactoryParseContext newContext = new DefaultHNodeFactoryParseContext(
                ctx.document()
                , element
                , this
                , session
                , Arrays.asList(ctx.nodePath())
                , ctx.source(),
                ctx.messages()
        );
        return NCallableSupport.resolve(
                        nodeParserFactories().stream()
                                .map(x -> x.parseNode(newContext)),
                        s -> NMsg.ofC("support for node '%s' ", element))
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
    public List<HNodeParser> nodeTypeFactories() {
        return new ArrayList<>(nodeTypeFactories0().values());
    }

    private Map<String, HNodeParser> nodeTypeFactories0() {
        if (nodeTypeFactories == null) {
            nodeTypeFactories = new HashMap<>();
            nodeTypeAliases = new HashMap<>();
            ServiceLoader<HNodeParser> renderers = ServiceLoader.load(HNodeParser.class);
            for (HNodeParser renderer : renderers) {
                addNodeTypeFactory(renderer);
            }
        }
        return nodeTypeFactories;
    }


    public NOptional<HNodeParser> nodeTypeFactory(String id) {
        id = NStringUtils.trim(id);
        if (!id.isEmpty()) {
            id = NNameFormat.LOWER_KEBAB_CASE.format(id);
            HNodeParser o = nodeTypeFactories0().get(id);
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

    public void addNodeTypeFactory(HNodeParser renderer) {
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
            NOptional<HNodeParser> o = nodeTypeFactory(i);
            if (o.isPresent()) {
                HNodeParser oo = o.get();
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


    public HDocumentLoadingResult compileDocument(HDocument document, HMessageList messages) {
        return hDocumentCompiler.compile(document, messages);
    }

    public boolean validateNode(HNode node) {
        return nodeTypeFactory(node.type()).get().validateNode(node);
    }

    @Override
    public HDocumentLoadingResult loadDocument(NPath path, HMessageList messages) {
        NAssert.requireNonNull(path, "path");
        if (GitHelper.isGithubFolder(path.toString())) {
            path = GitHelper.resolveGithubPath(path.toString(), messages, session);
        }
        HResource source = HResourceFactory.of(path);
        HDocumentLoadingResultImpl r = new HDocumentLoadingResultImpl(source, messages, session);
        HMessageListDelegateImpl messages1 = r.messages();
        if (path.exists()) {
            if (path.isRegularFile()) {
                HResource nPathResource = HResourceFactory.of(path);
                NOptional<TsonDocument> f = loadTsonDocument(path);
                if (!f.isPresent()) {
                    messages1.addError(f.getMessage().apply(session), nPathResource);
                }
                TsonDocument d = f.get();
                NOptional<HDocument> dd = convertDocument(d, r);
                if (dd.isPresent()) {
                    r.setDocument(dd.get());
                } else if (r.isSuccessful()) {
                    messages1.addError(dd.getMessage().apply(session), nPathResource);
                }
                if (r.get().root().source() == null) {
                    r.get().root().setSource(HResourceFactory.of(path));
                }
                return r;
            } else if (path.isDirectory()) {
                HDocument document = documentFactory().ofDocument();
                document.resources().add(path.resolve("*.hd"));
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && x.getName().endsWith(".hd")).toList();
                if (all.isEmpty()) {
                    messages1.addError(NMsg.ofC("invalid folder (no valid enclosed files) %s", path));
                    return r;
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
                for (NPath nPath : all) {
                    // document.resources().add(nPath);
                    NOptional<HItem> d = null;
                    HResource nPathResource = HResourceFactory.of(nPath);
                    try {
                        d = loadNode(document.root(), nPath, document, messages1);
                    } catch (Exception ex) {
                        messages1.addError(NMsg.ofC("unable to load %s : %s", nPath, ex), nPathResource);
                    }
                    if (d != null) {
                        if (!d.isPresent()) {
                            messages1.addError(NMsg.ofC("invalid file %s", nPath), nPathResource);
                            return r;
                        }
                        updateSource(d.get(), nPathResource);
                        document.root().append(d.get());
                    }
                }
                if (document.root().source() == null) {
                    document.root().setSource(HResourceFactory.of(path));
                }
                r.setDocument(document);
                return r;
            } else {
                messages1.addError(NMsg.ofC("invalid file %s", path));
                return r;
            }
        }
        messages1.addError(NMsg.ofC("file does not exist %s", path));
        return r;
    }

    private void updateSource(HItem item, HResource source) {
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
    public NOptional<HItem> loadNode(HNode into, NPath path, HDocument document, HMessageList messages) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                NOptional<HItem> d = loadNode0(into, path, document, messages);
                if (d.isPresent()) {
                    updateSource(d.get(), HResourceFactory.of(path));
                }
                return d;
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && x.getName().endsWith(".hd")).toList();
                all.sort((a, b) -> a.getName().compareTo(b.getName()));
                HItem node = null;
                for (NPath nPath : all) {
                    NOptional<HItem> d = loadNode0((node instanceof HNode) ? (HNode) node : null, nPath, document, messages);
                    if (!d.isPresent()) {
                        return NOptional.ofError(s -> NMsg.ofC("invalid file %s", nPath));
                    }
                    updateSource(d.get(), HResourceFactory.of(path));
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

    public HDocumentLoadingResult loadDocument(InputStream is, HMessageList messages) {
        HDocumentLoadingResultImpl result = new HDocumentLoadingResultImpl(HResourceFactory.of(is), messages, session);
        NOptional<TsonDocument> f = loadTsonDocument(is);
        if (!f.isPresent()) {
            result.messages().addError(f.getMessage().apply(session));
        }
        TsonDocument d = f.get();
        NOptional<HDocument> dd = convertDocument(d, result);
        if (dd.isPresent()) {
            result.setDocument(dd.get());
        } else if (result.isSuccessful()) {
            result.messages().addError(dd.getMessage().apply(session));
        }
        return result;
    }


    private NOptional<HDocument> convertDocument(TsonDocument doc, HDocumentLoadingResultImpl result) {
        if (doc == null) {
            result.messages().addError(NMsg.ofPlain("missing document"));
            return NOptional.ofNamedEmpty("document");
        }
        HResource source = result.source();
        TsonElement c = doc.getContent();
        HDocument docd = documentFactory().ofDocument();
        docd.resources().add(source);
        docd.root().setSource(source);
        HNodeFactoryParseContext newContext = new DefaultHNodeFactoryParseContext(
                docd,
                c, this, session,
                new ArrayList<>(),
                result.source(),
                result.messages()
        );

        NOptional<HItem> r = newNode(c, newContext);
        if (r.isPresent()) {
            docd.root().append(r.get());
            return NOptional.of(docd);
        }
        result.messages().addError(NMsg.ofC("invalid %s", r.getMessage().apply(session)));
        return NOptional.of(docd);
    }


    private NOptional<HItem> loadNode0(HNode into, NPath path, HDocument document, HMessageList messages) {
        HResource source = HResourceFactory.of(path);
        document.resources().add(source);
        TsonReader tr = Tson.reader();
        TsonDocument doc;
        try {
            doc = tr.readDocument(path.toPath().get());
        } catch (Throwable ex) {
            messages.addError(NMsg.ofC("error parsing node from %s : %s", path, ex), ex, source);
            return NOptional.ofNamedError(NMsg.ofC("error parsing node from %s : %s", path, ex));
        }
        TsonElement c = doc.getContent();
        ArrayList<HNode> parents = new ArrayList<>();
        if (into != null) {
            parents.add(into);
        }
        return newNode(c, new DefaultHNodeFactoryParseContext(
                document,
                null,
                this,
                session,
                parents,
                source,
                messages
        ));
    }

    @Override
    public TsonElement toTson(HDocument doc) {
        HNode r = doc.root();
        return nodeTypeFactory(r.type()).get().toTson(r);
    }


    @Override
    public TsonElement toTson(HNode node) {
        return nodeTypeFactory(node.type()).get().toTson(node);
    }


    @Override
    public NOptional<HProp> computeProperty(HNode node, String... propertyNames) {
        return hPropCalculator.computeProperty(node, propertyNames);
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
    public <T> NOptional<T> computePropertyValue(HNode node, String... propertyNames) {
        return hPropCalculator.computePropertyValue(node, propertyNames);
    }


    @Override
    public HResource computeSource(HNode node) {
        return hDocumentCompiler.computeSource(node);
    }

    @Override
    public HNodeRendererManager renderManager() {
        if (rendererManager == null) {
            rendererManager = new HNodeRendererManagerImpl(this);
        }
        return rendererManager;
    }

    @Override
    public HGraphics createGraphics(Graphics2D g2d) {
        return new HGraphicsImpl(g2d, session);
    }

    @Override
    public void createProject(NPath path, NPath projectUrl, Function<String, String> vars) {
        NAssert.requireNonNull(path, "path");
        NAssert.requireNonNull(projectUrl, "projectUrl");
        if (GitHelper.isGithubFolder(projectUrl.toString())) {
            projectUrl = GitHelper.resolveGithubPath(path.toString(), null, session);
        }
        if (!projectUrl.exists()) {
            throw new IllegalArgumentException("invalid project " + projectUrl);
        }
        NPath finalProjectUrl = projectUrl;
        Function<String, String> vars2 = m -> {
            switch (m) {
                case "template.templateBootUrl":
                    return finalProjectUrl.toString();
                case "template.templateUrl": {
                    try {
                        NPath bp = finalProjectUrl;
                        NPath pp = bp.getParent();
                        if (pp != null && pp.getName().equals("boot")) {
                            pp = pp.getParent();
                            if (pp != null) {
                                pp = pp.resolve("dist");
                                return pp.toString();
                            }
                        }
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Failed to resolve template boot url from " + finalProjectUrl);
                    }
                }
            }
            if (vars != null) {
                String u = vars.apply(m);
                if (u == null) {
                    switch (m) {
                        case "template.title":
                            return "New Document";
                        case "template.fullName":
                        case "template.author":
                            return System.getProperty("user.name");
                        case "template.date":
                            return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        case "template.version":
                            return "v1.0.0";
                    }
                }
                return u;
            }
            return null;
        };
        copyTemplate(projectUrl, path, vars2);
    }

    private String baseHalfaTemplatesUrl() {
        if (false) {
            return "/home/vpc/xprojects/productivity/halfa-templates";
        }
        return "github://thevpc/halfa-templates";
    }

    @Override
    public String getDefaultTemplateUrl() {
        return baseHalfaTemplatesUrl() + "/main/simple/v1.0/boot/default";
    }

    @Override
    public String[] getDefaultTemplateUrls() {
        return new String[]{
                baseHalfaTemplatesUrl() + "/main/simple/v1.0/boot/default",
                baseHalfaTemplatesUrl() + "/main/simple/v1.0/boot/single-page"
        };
    }

    private void copyTemplate(NPath from, NPath to, Function<String, String> vars) {
        if (from.isDirectory()) {
            if (!to.exists()) {
                to.mkdirs();
            }
            if (!to.isDirectory()) {
                throw new IllegalArgumentException("cannot copy folder " + from + " to " + to);
            }
            for (NPath nPath : from.list()) {
                copyTemplate(nPath, to.resolve(nPath.getName()), vars);
            }
        } else if (from.isRegularFile()) {
            if (from.getName().endsWith(".hd")) {
                String code = from.readString();
                to.writeString(NMsg.ofV(code, vars).toString());
            } else {
                from.copyTo(to);
            }
        } else {
            throw new IllegalArgumentException("cannot copy " + from + " to " + to);
        }
    }

}
