package net.thevpc.ntexup.engine;

import java.awt.*;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.document.style.DefaultNTxNodeSelector;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.style.NTxStyleAndMagnitude;
import net.thevpc.ntexup.api.document.style.NTxStyleRule;
import net.thevpc.ntexup.api.engine.NTxTemplateInfo;
import net.thevpc.ntexup.api.eval.NTxVarProvider;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;
import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.ntexup.engine.eval.*;
import net.thevpc.ntexup.engine.log.DefaultNTxLogger;
import net.thevpc.ntexup.api.engine.NTxEngineTools;
import net.thevpc.ntexup.api.log.NTxLogger;
import net.thevpc.ntexup.api.eval.NTxCompilePageContext;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.*;
import net.thevpc.ntexup.api.extension.NTxFunction;
import net.thevpc.ntexup.api.eval.NTxFunctionArg;
import net.thevpc.ntexup.api.eval.NTxVar;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.parser.*;
import net.thevpc.ntexup.api.renderer.*;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.ntexup.engine.templates.NTxTemplateInfoImpl;
import net.thevpc.ntexup.engine.util.NTxElementUtils;
import net.thevpc.ntexup.engine.document.DefaultNTxNode;
import net.thevpc.ntexup.engine.ext.NTxNodeCustomBuilderContextImpl;
import net.thevpc.ntexup.engine.log.NTxMessageList;
import net.thevpc.ntexup.engine.log.SilentNTxLogger;
import net.thevpc.ntexup.engine.parser.*;
import net.thevpc.ntexup.engine.parser.resources.NTxSourceFactory;
import net.thevpc.ntexup.engine.renderer.NTxDocumentRendererFactoryContextImpl;
import net.thevpc.ntexup.engine.document.NTxPropCalculator;
import net.thevpc.ntexup.engine.document.NTxDocumentFactoryImpl;
import net.thevpc.ntexup.engine.renderer.NTxGraphicsImpl;
import net.thevpc.ntexup.engine.renderer.NTxNodeRendererManagerImpl;
import net.thevpc.ntexup.engine.tools.MyNTxEngineTools;
import net.thevpc.nuts.*;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.*;


/**
 * @author vpc
 */
public class DefaultNTxEngine implements NTxEngine {

    private List<NTxDocumentRendererFactory> documentRendererFactories;
    private List<NTxNodeParserFactory> nodeParserFactories;

    private Map<String, NTxNodeParser> nodeTypeFactories;
    private NTxEngineTools tools;
    private List<NTxNodeCustomBuilderContextImpl> customBuilderContexts;
    private Map<String, String> nodeTypeAliases;
    private NTxDocumentFactory factory;
    private NTxPropCalculator propCalculator = new NTxPropCalculator();
    private NTxNodeRendererManager rendererManager;
    private List<NTxFunction> functions = new ArrayList<>();
    private NTxMessageList log = new NTxMessageList();
    private DefaultNTxDocumentItemParserFactory documentItemParserFactory;
    private Map<String, NTxTextRendererFlavor> flavorsMap;

    public DefaultNTxEngine() {
        tools = new MyNTxEngineTools(this);
        documentItemParserFactory = new DefaultNTxDocumentItemParserFactory();
        ServiceLoader<NTxFunction> all = ServiceLoader.load(NTxFunction.class);
        for (NTxFunction h : all) {
            functions.add(h);
        }
        addLog(new DefaultNTxLogger());
    }

    @Override
    public NTxEngineTools tools() {
        return tools;
    }

    public NOptional<NTxTextRendererFlavor> textRendererFlavor(String id) {
        return NOptional.ofNamed(flavorsMap().get(NTxUtils.uid(id)), id);
    }

    public List<NTxTextRendererFlavor> textRendererFlavors() {
        return new ArrayList<>(flavorsMap().values());
    }

    protected Map<String, NTxTextRendererFlavor> flavorsMap() {
        if (flavorsMap == null) {
            Map<String, NTxTextRendererFlavor> flavors2 = new HashMap<>();
            ServiceLoader<NTxTextRendererFlavor> all = ServiceLoader.load(NTxTextRendererFlavor.class);
            for (NTxTextRendererFlavor h : all) {
                String t = NTxUtils.uid(h.type());
                if (flavors2.containsKey(t)) {
                    throw new IllegalArgumentException("already registered text flavor " + t);
                }
                flavors2.put(t, h);
            }
            for (NTxNodeCustomBuilderContextImpl cb : customBuilderContexts()) {
                NTxTextRendererFlavor f = cb.createTextFlavor();
                if (f != null) {
                    flavors2.put(cb.id(), f);
                }
            }
            this.flavorsMap = flavors2;
        }
        return flavorsMap;
    }

    public DefaultNTxDocumentItemParserFactory getDocumentItemParserFactory() {
        return documentItemParserFactory;
    }

    @Override
    public NTxLogger log() {
        return log;
    }

    @Override
    public NTxLogger addLog(NTxLogger messages) {
        log.add(messages);
        return log;
    }

    @Override
    public NTxLogger removeLog(NTxLogger messages) {
        log.remove(messages);
        return log;
    }

    @Override
    public NOptional<NTxFunction> findFunction(NTxItem node, String name, NTxFunctionArg... args) {
        NTxItem p = node;
        while (p != null) {
            if (p instanceof NTxNode) {
                for (NTxFunction f : ((NTxNode) p).nodeFunctions()) {
                    if (NNameFormat.equalsIgnoreFormat(f.name(), name)) {
                        return NOptional.of(f);
                    }
                }
            }
            p = p.parent();
        }
        for (NTxFunction f : functions) {
            if (NNameFormat.equalsIgnoreFormat(f.name(), name)) {
                return NOptional.of(f);
            }
        }
        return NOptional.ofNamedEmpty("function " + name);
    }

    @Override
    public NTxDocumentFactory documentFactory() {
        if (factory == null) {
            factory = new NTxDocumentFactoryImpl(this);
        }
        return factory;
    }


    @Override
    public NOptional<NTxItem> newNode(NElement element, NTxNodeFactoryParseContext ctx) {
        NAssert.requireNonNull(ctx, "context");
        NAssert.requireNonNull(element, "element");
        if (ctx.source() == null) {
            throw new IllegalArgumentException("unexpected source null");
        }
        NTxNodeFactoryParseContext newContext = new DefaultNTxNodeFactoryParseContext(
                ctx.document()
                , element
                , this
                ,
                Arrays.asList(ctx.nodePath())
                , ctx.source()
        );
        NOptional<NTxItem> optional = NCallableSupport.resolve(
                        nodeParserFactories().stream()
                                .map(x -> x.parseNode(newContext)),
                        () -> NMsg.ofC("missing support for node from type '%s' value '%s'", element.type().id(), NTxUtils.snippet(element)))
                .toOptional();
        if (optional.isPresent()) {
            NTxItem nTxItem = optional.get();
            if (nTxItem instanceof NTxNode) {
                NTxSource s = nTxItem.source();
                if (s == null) {
                    throw new IllegalArgumentException("unexpected source null");
                }
            }
        }
        return optional;
    }

    @Override
    public NOptional<NTxDocumentStreamRenderer> newPdfRenderer() {
        return newStreamRenderer("pdf");
    }

    @Override
    public NOptional<NTxDocumentStreamRenderer> newHtmlRenderer() {
        return newStreamRenderer("html");
    }

    @Override
    public NOptional<NTxDocumentScreenRenderer> newScreenRenderer() {
        NOptional<NTxDocumentRenderer> u = newRenderer("screen");
        if (u.isPresent()) {
            if (u.get() instanceof NTxDocumentScreenRenderer) {
                return NOptional.of((NTxDocumentScreenRenderer) u.get());
            }
            return NOptional.ofEmpty(NMsg.ofC("support for stream renderer is not of HDocumentScreenRenderer type"));
        }
        return NOptional.ofNamedEmpty("screen renderer");
    }

    @Override
    public NOptional<NTxDocumentStreamRenderer> newStreamRenderer(String type) {
        NOptional<NTxDocumentRenderer> u = newRenderer(type);
        if (u.isPresent()) {
            if (u.get() instanceof NTxDocumentStreamRenderer) {
                return NOptional.of((NTxDocumentStreamRenderer) u.get());
            }
            return NOptional.ofEmpty(NMsg.ofC("support for stream renderer '%s' is not of HDocumentStreamRenderer type", type));
        }

        return NOptional.ofNamedEmpty(type + " renderer");
    }

    @Override
    public NOptional<NTxDocumentRenderer> newRenderer(String type) {
        NTxDocumentRendererFactoryContext ctx = new NTxDocumentRendererFactoryContextImpl(this, type);
        return NCallableSupport.resolve(
                        documentRendererFactories().stream()
                                .map(x -> x.<NTxDocumentStreamRenderer>createDocumentRenderer(ctx)),
                        () -> NMsg.ofC("missing StreamRenderer %s", type))
                .toOptional();
    }

    private List<NTxDocumentRendererFactory> documentRendererFactories() {
        if (documentRendererFactories == null) {
            ServiceLoader<NTxDocumentRendererFactory> renderers = ServiceLoader.load(NTxDocumentRendererFactory.class);
            List<NTxDocumentRendererFactory> loaded = new ArrayList<>();
            for (NTxDocumentRendererFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.documentRendererFactories = loaded;
        }
        return documentRendererFactories;
    }

    @Override
    public List<NTxNodeParser> nodeTypeFactories() {
        return new ArrayList<>(nodeTypeFactories0().values());
    }

    public List<NTxNodeCustomBuilderContextImpl> customBuilderContexts() {
        if (customBuilderContexts == null) {
            ServiceLoader<NTxNodeBuilder> customBuilders = ServiceLoader.load(NTxNodeBuilder.class);
            List<NTxNodeCustomBuilderContextImpl> builderContexts = new ArrayList<>();
            for (NTxNodeBuilder customBuilder : customBuilders) {
                NTxNodeCustomBuilderContextImpl b = new NTxNodeCustomBuilderContextImpl(customBuilder, this);
                customBuilder.build(b);
                b.compile();
                builderContexts.add(b);
            }
            this.customBuilderContexts = builderContexts;
        }
        return customBuilderContexts;
    }

    private Map<String, NTxNodeParser> nodeTypeFactories0() {
        if (nodeTypeFactories == null) {
            nodeTypeFactories = new HashMap<>();
            nodeTypeAliases = new HashMap<>();
            ServiceLoader<NTxNodeParser> renderers = ServiceLoader.load(NTxNodeParser.class);
            for (NTxNodeParser renderer : renderers) {
                addNodeTypeFactory(renderer);
            }
            for (NTxNodeCustomBuilderContextImpl cb : customBuilderContexts()) {
                addNodeTypeFactory(cb.createParser());
            }
        }
        return nodeTypeFactories;
    }


    public NTxNode newDefaultNode(String id) {
        return new DefaultNTxNode(id);
    }

    public NOptional<NTxNodeParser> nodeTypeParser(String id) {
        id = NStringUtils.trim(id);
        if (!id.isEmpty()) {
            id = NNameFormat.LOWER_KEBAB_CASE.format(id);
            NTxNodeParser o = nodeTypeFactories0().get(id);
            if (o != null) {
                return NOptional.of(o);
            }
            String oid = nodeTypeAliases.get(id);
            if (oid != null) {
                return nodeTypeParser(oid);
            }
            switch (id) {
                case NTxNodeType.CTRL_CALL:
                    return NOptional.of(
                            new NTxNodeParserBase(true, NTxNodeType.CTRL_CALL) {

                            }
                    );
                case NTxNodeType.CTRL_ASSIGN:
                    return NOptional.of(
                            new NTxNodeParserBase(true, NTxNodeType.CTRL_ASSIGN) {

                            }
                    );
                case NTxNodeType.CTRL_EXPR:
                    return NOptional.of(
                            new NTxNodeParserBase(false, NTxNodeType.CTRL_EXPR) {

                            }
                    );
                case NTxNodeType.CTRL_NAME:
                    return NOptional.of(
                            new NTxNodeParserBase(false, NTxNodeType.CTRL_NAME) {

                            }
                    );
            }
        }
        return NOptional.ofNamedEmpty("node type parser for NodeType " + id);
    }

    public void addNodeTypeFactory(NTxNodeParser renderer) {
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
            NOptional<NTxNodeParser> o = nodeTypeParser(i);
            if (o.isPresent()) {
                NTxNodeParser oo = o.get();
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

    private List<NTxNodeParserFactory> nodeParserFactories() {
        if (nodeParserFactories == null) {
            ServiceLoader<NTxNodeParserFactory> renderers = ServiceLoader.load(NTxNodeParserFactory.class);
            List<NTxNodeParserFactory> loaded = new ArrayList<>();
            loaded.add(documentItemParserFactory);
            for (NTxNodeParserFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.nodeParserFactories = loaded;
        }
        return nodeParserFactories;
    }


    public NTxDocumentLoadingResult compileDocument(NTxDocument document) {
        return new NTxCompiler(this).compile(document);
    }

    @Override
    public List<NTxNode> compilePageNode(NTxNode node, NTxDocument document) {
        return compilePageNode(node, new NTxCompilePageContextImpl(this, document));
    }

    @Override
    public List<NTxNode> compilePageNode(NTxNode node, NTxCompilePageContext context) {
        return new NTxCompiler(this).compilePageNode(node, context);
    }

    @Override
    public List<NTxNode> compileItem(NTxItem node, NTxCompilePageContext context) {
        List<NTxNode> all = new ArrayList<>();
        if (node instanceof NTxNode) {
            all.addAll(compilePageNode((NTxNode) node, context));
        } else if (node instanceof NTxItemList) {
            for (NTxItem i : ((NTxItemList) node).getItems()) {
                all.addAll(compileItem(i, context));
            }
        } else if (
                node instanceof NTxStyleRule
                        || node instanceof NTxNodeDef
                        || node instanceof NTxProp
        ) {
            //just ignore
        }
        return all;
    }

    public boolean validateNode(NTxNode node) {
        return nodeTypeParser(node.type()).get().validateNode(node);
    }

    @Override
    public NTxDocumentLoadingResult loadDocument(NPath path) {
        NAssert.requireNonNull(path, "path");
        synchronized (this) {
            if (NTxGitHelper.isGithubFolder(path.toString())) {
                path = NTxGitHelper.resolveGithubPath(path.toString(), log());
            }
            NTxSource source = NTxSourceFactory.of(path);
            if (path.exists()) {
                if (path.isRegularFile()) {
                    NTxSource nPathResource = NTxSourceFactory.of(path);
                    NOptional<NElement> f = new NTxDocStreamParser(this).parsePath(path, nPathResource);
                    if (!f.isPresent()) {
                        log().log(f.getMessage().get().asSevere(), nPathResource);
                    }
                    NElement d = f.get();
                    SilentNTxLogger slog = new SilentNTxLogger();
                    try {
                        this.addLog(slog);
                        NOptional<NTxDocument> dd = convertDocument(d, source);
                        if (dd.isPresent()) {
                            return new NTxDocumentLoadingResultImpl(dd.get(), nPathResource, slog.isSuccessful());
                        } else {
                            log().log(dd.getMessage().get().asSevere(), nPathResource);
                        }
                    } finally {
                        this.removeLog(slog);
                    }

                } else if (path.isDirectory()) {
                    NTxDocument document = documentFactory().ofDocument(source);
                    document.resources().add(path.resolve(NTxEngineUtils.NTEXUP_EXT_STAR));
                    List<NPath> all = path.stream().filter(x -> x.isRegularFile() && NTxEngineUtils.isNTexupFile(x)).toList();
                    if (all.isEmpty()) {
                        log().log(
                                NMsg.ofC("invalid folder (no valid enclosed files) %s", path).asSevere()
                        );
                        return new NTxDocumentLoadingResultImpl(document, source, false);
                    }
                    NPath main = null;
                    for (String mainFiled : new String[]{
                            "main."+FILE_EXT,
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
                        NOptional<NTxItem> d = null;
                        NTxSource nPathResource = NTxSourceFactory.of(nPath);
                        try {
                            d = loadNode(document.root(), nPath, document);
                        } catch (Exception ex) {
                            log().log(NMsg.ofC("unable to load %s : %s", nPath, ex).asSevere(), nPathResource);
                        }
                        if (d != null) {
                            if (!d.isPresent()) {
                                log().log(NMsg.ofC("invalid file %s", nPath).asSevere(), nPathResource);
                            }
                            updateSource(d.get(), nPathResource);
                            document.root().append(d.get());
                        }
                    }
                    if (document.root().source() == null) {
                        document.root().setSource(NTxSourceFactory.of(path));
                    }
                    return new NTxDocumentLoadingResultImpl(document, source, true);
                } else {
                    log().log(NMsg.ofC("invalid file %s", path).asSevere());
                    return new NTxDocumentLoadingResultImpl(null, source, false);
                }
            }
            log().log(NMsg.ofC("file does not exist %s", path).asSevere());
            return new NTxDocumentLoadingResultImpl(null, source, false);
        }
    }

    private void updateSource(NTxItem item, NTxSource source) {
        if (item != null) {
            if (item instanceof NTxItemList) {
                for (NTxItem hItem : ((NTxItemList) item).getItems()) {
                    updateSource(hItem, source);
                }
            } else if (item instanceof NTxNode) {
                NTxNode i = (NTxNode) item;
                if (i.source() == null) {
                    i.setSource(source);
                }
            }
        }
    }

    @Override
    public NOptional<NTxItem> loadNode(NTxNode into, NPath path, NTxDocument document) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                NTxSource source = NTxSourceFactory.of(path);
                NOptional<NTxItem> d = loadNode0(into, path, document);
                if (d.isPresent()) {
                    updateSource(d.get(), source);
                }
                return d;
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && NTxEngineUtils.isNTexupFile(x.getName())).toList();
                all.sort(NTxEngineUtils::comparePaths);
                NTxItem node = null;
                for (NPath nPath : all) {
                    NOptional<NTxItem> d = loadNode0((node instanceof NTxNode) ? (NTxNode) node : null, nPath, document);
                    if (!d.isPresent()) {
                        log().log(NMsg.ofC("invalid file %s", nPath));
                        return NOptional.ofError(() -> NMsg.ofC("invalid file %s", nPath));
                    }
                    updateSource(d.get(), NTxSourceFactory.of(path));
                    if (node == null) {
                        node = d.get();
                    } else {
                        if (node instanceof NTxNode) {
                            ((NTxNode) node).mergeNode(d.get());
                        } else if (node instanceof NTxItemList) {
                            node = new NTxItemList().addAll(((NTxItemList) node).getItems()).add(node);
                        } else {
                            node = new NTxItemList().add(node).add(d.get());
                        }
                    }
                }
                return NOptional.of(node);
            } else {
                log().log(NMsg.ofC("invalid file %s", path));
            }
        }
        log().log(NMsg.ofC("file does not exist %s", path));
        return NOptional.ofError(() -> NMsg.ofC("file does not exist %s", path));
    }


    public NTxDocumentLoadingResult loadDocument(InputStream is) {
        NTxSource source = NTxSourceFactory.of(is);

        SilentNTxLogger slog = new SilentNTxLogger();
        try {
            this.addLog(slog);

            NOptional<NElement> f = new NTxDocStreamParser(this).parseInputStream(is, source);
            if (!f.isPresent()) {
                log().log(f.getMessage().get().asSevere());
            }
            NElement d = f.get();
            NOptional<NTxDocument> dd = convertDocument(d, source);
            if (dd.isPresent()) {
                return new NTxDocumentLoadingResultImpl(dd.get(), source, slog.isSuccessful());
            } else {
                log().log(dd.getMessage().get().asSevere());
                return new NTxDocumentLoadingResultImpl(null, source, false);
            }
        } finally {
            this.removeLog(slog);
        }
    }


    private NOptional<NTxDocument> convertDocument(NElement doc, NTxSource source) {
        if (doc == null) {
            log().log(NMsg.ofPlain("missing document").asError());
            return NOptional.ofNamedEmpty("document");
        }
        NTxDocument docd = documentFactory().ofDocument(source);
        docd.resources().add(source);
        docd.root().setSource(source);
        NTxNodeFactoryParseContext newContext = new DefaultNTxNodeFactoryParseContext(
                docd,
                doc, this,
                new ArrayList<>(),
                source
        );

        NOptional<NTxItem> r = newNode(doc, newContext);
        if (r.isPresent()) {
            docd.root().append(r.get());
            return NOptional.of(docd);
        }
        log().log(NMsg.ofC("invalid %s", r.getMessage().get()).asSevere());
        return NOptional.of(docd);
    }


    private NOptional<NTxItem> loadNode0(NTxNode into, NPath path, NTxDocument document) {
        NTxSource source = NTxSourceFactory.of(path);
        document.resources().add(source);
        NOptional<NElement> u = new NTxDocStreamParser(this).parsePath(path, source);
        if(!u.isPresent()) {
            return NOptional.ofEmpty(u.getMessage());
        }
        NElement c = u.get();
        ArrayList<NTxNode> parents = new ArrayList<>();
        if (into != null) {
            parents.add(into);
        }
        return newNode(c, new DefaultNTxNodeFactoryParseContext(
                document,
                null,
                this,
                parents,
                source
        ));
    }

    @Override
    public NElement toElement(NTxDocument doc) {
        NTxNode r = doc.root();
        return nodeTypeParser(r.type()).get().toElem(r);
    }


    @Override
    public NElement toElement(NTxNode node) {
        return nodeTypeParser(node.type()).get().toElem(node);
    }


    @Override
    public NOptional<NTxProp> computeProperty(NTxNode node, String... propertyNames) {
        return propCalculator.computeProperty(node, propertyNames);
    }

    @Override
    public List<NTxProp> computeProperties(NTxNode node) {
        return propCalculator.computeProperties(node);
    }

    @Override
    public List<NTxProp> computeInheritedProperties(NTxNode node) {
        return propCalculator.computeInheritedProperties(node);
    }
    @Override
    public List<NTxStyleRule> computeStyles(NTxNode node) {
        return propCalculator.computeStyles(node);
    }

    @Override
    public List<NTxStyleRule> computeDeclaredStyles(NTxNode node) {
        return propCalculator.computeDeclaredStyles(node);
    }

    @Override
    public Set<String> computeDeclaredStylesClasses(NTxNode node) {
        return computeDeclaredStyles(node).stream().flatMap(x -> {
            if (x instanceof DefaultNTxNodeSelector) {
                DefaultNTxNodeSelector y = (DefaultNTxNodeSelector) x;
                return y.getClasses().stream();
            }
            return Stream.empty();
        }).collect(Collectors.toSet());
    }

    @Override
    public <T> NOptional<T> computePropertyValue(NTxNode node, String... propertyNames) {
        return propCalculator.computePropertyValue(node, propertyNames);
    }


    @Override
    public NTxNodeRendererManager renderManager() {
        if (rendererManager == null) {
            rendererManager = new NTxNodeRendererManagerImpl(this);
        }
        return rendererManager;
    }

    @Override
    public NTxGraphics createGraphics(Graphics2D g2d) {
        return new NTxGraphicsImpl(g2d, this);
    }

    @Override
    public void createProject(NPath path, NPath projectUrl, Function<String, String> vars) {
        NAssert.requireNonNull(path, "path");
        NAssert.requireNonNull(projectUrl, "projectUrl");
        if (NTxGitHelper.isGithubFolder(projectUrl.toString())) {
            projectUrl = NTxGitHelper.resolveGithubPath(path.toString(), null);
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
                        log().log(NMsg.ofC("Failed to resolve template boot url from %s", finalProjectUrl, ex).asSevere());
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

    @Override
    public NTxTemplateInfo[] getTemplates() {
        List<NTxTemplateInfo> allTemplates = new ArrayList<>();
        class Repo {
            String name;
            NPath path;

            public Repo(String name, NPath path) {
                this.name = name;
                this.path = path;
            }
        }
        for (Repo repo : new Repo[]{
                new Repo("dev", NPath.ofUserHome().resolve("xprojects/nuts-world/nuts-productivity/ntexup-templates/main")),
                new Repo("local", NApp.of().getSharedConfFolder().resolve("templates")),
                new Repo("user", NPath.ofUserStore(NStoreType.CONF).resolve("ntexup/templates")),
                new Repo("user", NPath.ofSystemStore(NStoreType.CONF).resolve("ntexup/templates")),
                new Repo("central-github", NPath.of("github://thevpc/ntexup-templates/main"))
        }) {
            try {
                if (NTxGitHelper.isGithubFolder(repo.path.toString())) {
                    NPath nPath1 = NTxGitHelper.resolveGithubPath(repo.path.toString(), log());
                    if (nPath1.resolve("ntexup-repository.tson").isRegularFile()) {
                        try {
                            loadTemplateInfo(NElementParser.ofTson().parse(nPath1.resolve("ntexup-repository.tson")), repo.name, repo.path, allTemplates);
                        } catch (Exception e) {
                            log().log(NMsg.ofC("unable to parse repository templates '%s' at '%s' : %s", repo.name, repo.path, e).asError());
                        }
                    }else{
                        log().log(NMsg.ofC("repository template not found '%s' at '%s'", repo.name, repo.path).asWarning());
                    }
                }else if (repo.path.isLocal()) {
                    if (repo.path.resolve("ntexup-repository.tson").isRegularFile()) {
                        try {
                            loadTemplateInfo(NElementParser.ofTson().parse(repo.path.resolve("ntexup-repository.tson")), repo.name, repo.path, allTemplates);
                        } catch (Exception e) {
                            log().log(NMsg.ofC("unable to parse repository templates '%s' at '%s' : %s", repo.name, repo.path, e).asError());
                        }
                    }else{
                        log().log(NMsg.ofC("repository template not found '%s' at '%s'", repo.name, repo.path).asWarning());
                    }
                }else{
                    log().log(NMsg.ofC("unable to parse repository templates '%s' at '%s'", repo.name, repo.path).asWarning());
                }
            } catch (Exception e) {
                log().log(NMsg.ofC("unable to load repository '%s' at '%s' : %s", repo.name, repo.path, e).asError());
            }
        }
        return allTemplates.toArray(new NTxTemplateInfo[0]);
    }

    private void loadTemplateInfo(NElement elem, String repoName, NPath repoPath, List<NTxTemplateInfo> allTemplates) {
        if (elem.isObject()) {
            for (NElement o : elem.asObject().get().children()) {
                loadTemplateInfo(o, repoName, repoPath, allTemplates);
            }
        } else if (elem.isString()) {
            String name = null;
            String localPath = elem.asStringValue().orNull();
            boolean recommended = false;
            for (NElementAnnotation annotation : elem.annotations()) {
                if (annotation.name().equals("recommended")) {
                    recommended = true;
                } else if (annotation.name().equals("name") && !annotation.params().isEmpty() && annotation.param(0).isString()) {
                    name = annotation.param(0).asStringValue().orNull();
                }
            }
            if (!NBlankable.isBlank(localPath) && !NBlankable.isBlank(name)) {
                allTemplates.add(
                        new NTxTemplateInfoImpl(
                                NStringUtils.trim(name),
                                repoPath.resolveChild(localPath).toString(),
                                recommended,
                                repoName, repoPath.toString(),
                                NStringUtils.trim(localPath)
                        )
                );
            }
        }
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
            if (NTxEngineUtils.isNTexupFile(from)) {
                String code = from.readString();
                to.writeString(NMsg.ofV(code, vars).toString());
            } else {
                from.copyTo(to);
            }
        } else {
            throw new IllegalArgumentException("cannot copy " + from + " to " + to);
        }
    }

    @Override
    public NElement evalExpression(NElement expression, NTxNode node, NTxVarProvider varProvider) {
        String baseSrc = NTxUtils.findCompilerDeclarationPath(expression).orNull();
        NTxNodeEval ne = new NTxNodeEval(this,varProvider);
        NElement u = ne.eval(NTxElementUtils.toElement(expression), node);
        if (u == null) {
            u = NElement.ofNull();
        }
        if (baseSrc != null) {
            u = NTxUtils.addCompilerDeclarationPath(u, baseSrc);
        }
        return u;
    }

    @Override
    public NElement resolveVarValue(String varName, NTxNode node, NTxVarProvider varProvider) {
        NOptional<NTxVar> v = findVar(varName, node, varProvider);
        if (!v.isPresent()) {
            log().log(NMsg.ofC("var not found %s", varName).asWarning(), NTxUtils.sourceOf(node));
            return null;
        }
        NElement ee = v.get().get();
        return evalExpression(ee, node, varProvider);
    }

    public NOptional<NTxVar> findVar(String varName, NTxNode node, NTxVarProvider varProvider) {
        NTxNodeEval ne = new NTxNodeEval(this,varProvider);
        return ne.findVar(varName, node);
    }

    @Override
    public NPath resolvePath(NElement path, NTxNode node) {
        if (NBlankable.isBlank(path)) {
            return null;
        }
        if (path.isAnyString()) {
            String pathStr = path.asStringValue().get();
            if (NTxGitHelper.isGithubFolder(pathStr)) {
                return NTxGitHelper.resolveGithubPath(pathStr, log());
            }
            NTxSource source = NTxUtils.sourceOf(node);
            return NTxUtils.resolvePath(path, source);
        }
        throw new NIllegalArgumentException(NMsg.ofC("unsupported path type", path));
    }

}
