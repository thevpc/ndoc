package net.thevpc.ndoc.engine;

import java.awt.*;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.ndoc.engine.log.DefaultNDocLogger;
import net.thevpc.ndoc.api.engine.NDocEngineTools;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.eval.NDocCompilePageContext;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.eval.NDocFunction;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.ndoc.api.eval.NDocVar;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.parser.*;
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.engine.document.DefaultNDocNode;
import net.thevpc.ndoc.engine.eval.NDocCompilePageContextImpl;
import net.thevpc.ndoc.engine.ext.NDocNodeCustomBuilderContextImpl;
import net.thevpc.ndoc.engine.log.NDocMessageList;
import net.thevpc.ndoc.engine.log.SilentNDocLogger;
import net.thevpc.ndoc.engine.parser.*;
import net.thevpc.ndoc.engine.eval.NDocCompiler;
import net.thevpc.ndoc.engine.eval.GitHelper;
import net.thevpc.ndoc.engine.parser.resources.NDocResourceFactory;
import net.thevpc.ndoc.engine.renderer.NDocDocumentRendererFactoryContextImpl;
import net.thevpc.ndoc.engine.document.NDocPropCalculator;
import net.thevpc.ndoc.engine.document.NDocDocumentFactoryImpl;
import net.thevpc.ndoc.engine.renderer.NDocGraphicsImpl;
import net.thevpc.ndoc.engine.renderer.NDocNodeRendererManagerImpl;
import net.thevpc.ndoc.engine.eval.NDocNodeEvalNDoc;
import net.thevpc.ndoc.engine.tools.MyNDocEngineTools;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.*;


/**
 * @author vpc
 */
public class DefaultNDocEngine implements NDocEngine {

    private List<NDocDocumentRendererFactory> documentRendererFactories;
    private List<NDocNodeParserFactory> nodeParserFactories;

    private Map<String, NDocNodeParser> nodeTypeFactories;
    private NDocEngineTools tools;
    private List<NDocNodeCustomBuilderContextImpl> customBuilderContexts;
    private Map<String, String> nodeTypeAliases;
    private NDocDocumentFactory factory;
    private NDocPropCalculator propCalculator = new NDocPropCalculator();
    private NDocNodeRendererManager rendererManager;
    private List<NDocFunction> functions = new ArrayList<>();
    private NDocMessageList log = new NDocMessageList();
    private DefaultNDocDocumentItemParserFactory documentItemParserFactory;
    private Map<String, NDocTextRendererFlavor> flavorsMap;

    public DefaultNDocEngine() {
        tools = new MyNDocEngineTools(this);
        documentItemParserFactory = new DefaultNDocDocumentItemParserFactory();
        ServiceLoader<NDocFunction> all = ServiceLoader.load(NDocFunction.class);
        for (NDocFunction h : all) {
            functions.add(h);
        }
        addLog(new DefaultNDocLogger());
    }

    @Override
    public NDocEngineTools tools() {
        return tools;
    }

    public NOptional<NDocTextRendererFlavor> textRendererFlavor(String id) {
        return NOptional.ofNamed(flavorsMap().get(NDocUtils.uid(id)), id);
    }

    public List<NDocTextRendererFlavor> textRendererFlavors() {
        return new ArrayList<>(flavorsMap().values());
    }

    protected Map<String, NDocTextRendererFlavor> flavorsMap() {
        if (flavorsMap == null) {
            Map<String, NDocTextRendererFlavor> flavors2 = new HashMap<>();
            ServiceLoader<NDocTextRendererFlavor> all = ServiceLoader.load(NDocTextRendererFlavor.class);
            for (NDocTextRendererFlavor h : all) {
                String t = NDocUtils.uid(h.type());
                if (flavors2.containsKey(t)) {
                    throw new IllegalArgumentException("already registered text flavor " + t);
                }
                flavors2.put(t, h);
            }
            for (NDocNodeCustomBuilderContextImpl cb : customBuilderContexts()) {
                NDocTextRendererFlavor f = cb.createTextFlavor();
                if (f != null) {
                    flavors2.put(cb.id(), f);
                }
            }
            this.flavorsMap = flavors2;
        }
        return flavorsMap;
    }

    public DefaultNDocDocumentItemParserFactory getDocumentItemParserFactory() {
        return documentItemParserFactory;
    }

    @Override
    public NDocLogger log() {
        return log;
    }

    @Override
    public NDocLogger addLog(NDocLogger messages) {
        log.add(messages);
        return log;
    }

    @Override
    public NDocLogger removeLog(NDocLogger messages) {
        log.remove(messages);
        return log;
    }

    @Override
    public NOptional<NDocFunction> findFunction(NDocItem node, String name, NDocFunctionArg... args) {
        NDocItem p = node;
        while (p != null) {
            if (p instanceof NDocNode) {
                for (NDocFunction f : ((NDocNode) p).nodeFunctions()) {
                    if (NNameFormat.equalsIgnoreFormat(f.name(), name)) {
                        return NOptional.of(f);
                    }
                }
            }
            p = p.parent();
        }
        for (NDocFunction f : functions) {
            if (NNameFormat.equalsIgnoreFormat(f.name(), name)) {
                return NOptional.of(f);
            }
        }
        return NOptional.ofNamedEmpty("function " + name);
    }

    @Override
    public NDocDocumentFactory documentFactory() {
        if (factory == null) {
            factory = new NDocDocumentFactoryImpl(this);
        }
        return factory;
    }


    @Override
    public NOptional<NDocItem> newNode(NElement element, NDocNodeFactoryParseContext ctx) {
        NAssert.requireNonNull(ctx, "context");
        NAssert.requireNonNull(element, "element");
        if (ctx.source() == null) {
            throw new IllegalArgumentException("unexpected source null");
        }
        NDocNodeFactoryParseContext newContext = new DefaultNDocNodeFactoryParseContext(
                ctx.document()
                , element
                , this
                ,
                Arrays.asList(ctx.nodePath())
                , ctx.source()
        );
        NOptional<NDocItem> optional = NCallableSupport.resolve(
                        nodeParserFactories().stream()
                                .map(x -> x.parseNode(newContext)),
                        () -> NMsg.ofC("missing support for node from type '%s' value '%s'", element.type().id(),NDocUtils.snippet(element)))
                .toOptional();
        if (optional.isPresent()) {
            NDocItem nDocItem = optional.get();
            if (nDocItem instanceof NDocNode) {
                NDocResource s = nDocItem.source();
                if (s == null) {
                    throw new IllegalArgumentException("unexpected source null");
                }
            }
        }
        return optional;
    }

    @Override
    public NOptional<NDocDocumentStreamRenderer> newPdfRenderer() {
        return newStreamRenderer("pdf");
    }

    @Override
    public NOptional<NDocDocumentStreamRenderer> newHtmlRenderer() {
        return newStreamRenderer("html");
    }

    @Override
    public NOptional<NDocDocumentScreenRenderer> newScreenRenderer() {
        NOptional<NDocDocumentRenderer> u = newRenderer("screen");
        if (u.isPresent()) {
            if (u.get() instanceof NDocDocumentScreenRenderer) {
                return NOptional.of((NDocDocumentScreenRenderer) u.get());
            }
            return NOptional.ofEmpty(NMsg.ofC("support for stream renderer is not of HDocumentScreenRenderer type"));
        }
        return NOptional.ofNamedEmpty("screen renderer");
    }

    @Override
    public NOptional<NDocDocumentStreamRenderer> newStreamRenderer(String type) {
        NOptional<NDocDocumentRenderer> u = newRenderer(type);
        if (u.isPresent()) {
            if (u.get() instanceof NDocDocumentStreamRenderer) {
                return NOptional.of((NDocDocumentStreamRenderer) u.get());
            }
            return NOptional.ofEmpty(NMsg.ofC("support for stream renderer '%s' is not of HDocumentStreamRenderer type", type));
        }

        return NOptional.ofNamedEmpty(type + " renderer");
    }

    @Override
    public NOptional<NDocDocumentRenderer> newRenderer(String type) {
        NDocDocumentRendererFactoryContext ctx = new NDocDocumentRendererFactoryContextImpl(this, type);
        return NCallableSupport.resolve(
                        documentRendererFactories().stream()
                                .map(x -> x.<NDocDocumentStreamRenderer>createDocumentRenderer(ctx)),
                        () -> NMsg.ofC("missing StreamRenderer %s", type))
                .toOptional();
    }

    private List<NDocDocumentRendererFactory> documentRendererFactories() {
        if (documentRendererFactories == null) {
            ServiceLoader<NDocDocumentRendererFactory> renderers = ServiceLoader.load(NDocDocumentRendererFactory.class);
            List<NDocDocumentRendererFactory> loaded = new ArrayList<>();
            for (NDocDocumentRendererFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.documentRendererFactories = loaded;
        }
        return documentRendererFactories;
    }

    @Override
    public List<NDocNodeParser> nodeTypeFactories() {
        return new ArrayList<>(nodeTypeFactories0().values());
    }

    public List<NDocNodeCustomBuilderContextImpl> customBuilderContexts() {
        if (customBuilderContexts == null) {
            ServiceLoader<NDocNodeCustomBuilder> customBuilders = ServiceLoader.load(NDocNodeCustomBuilder.class);
            List<NDocNodeCustomBuilderContextImpl> builderContexts = new ArrayList<>();
            for (NDocNodeCustomBuilder customBuilder : customBuilders) {
                NDocNodeCustomBuilderContextImpl b = new NDocNodeCustomBuilderContextImpl(customBuilder, this);
                customBuilder.build(b);
                b.compile();
                builderContexts.add(b);
            }
            this.customBuilderContexts = builderContexts;
        }
        return customBuilderContexts;
    }

    private Map<String, NDocNodeParser> nodeTypeFactories0() {
        if (nodeTypeFactories == null) {
            nodeTypeFactories = new HashMap<>();
            nodeTypeAliases = new HashMap<>();
            ServiceLoader<NDocNodeParser> renderers = ServiceLoader.load(NDocNodeParser.class);
            for (NDocNodeParser renderer : renderers) {
                addNodeTypeFactory(renderer);
            }
            for (NDocNodeCustomBuilderContextImpl cb : customBuilderContexts()) {
                addNodeTypeFactory(cb.createParser());
            }
        }
        return nodeTypeFactories;
    }


    public NDocNode newDefaultNode(String id) {
        return new DefaultNDocNode(id);
    }

    public NOptional<NDocNodeParser> nodeTypeParser(String id) {
        id = NStringUtils.trim(id);
        if (!id.isEmpty()) {
            id = NNameFormat.LOWER_KEBAB_CASE.format(id);
            NDocNodeParser o = nodeTypeFactories0().get(id);
            if (o != null) {
                return NOptional.of(o);
            }
            String oid = nodeTypeAliases.get(id);
            if (oid != null) {
                return nodeTypeParser(oid);
            }
            switch (id) {
                case NDocNodeType.CTRL_CALL:
                    return NOptional.of(
                            new NDocNodeParserBase(true, NDocNodeType.CTRL_CALL) {

                            }
                    );
                case NDocNodeType.CTRL_ASSIGN:
                    return NOptional.of(
                            new NDocNodeParserBase(true, NDocNodeType.CTRL_ASSIGN) {

                            }
                    );
                case NDocNodeType.CTRL_EXPR:
                    return NOptional.of(
                            new NDocNodeParserBase(false, NDocNodeType.CTRL_EXPR) {

                            }
                    );
                case NDocNodeType.CTRL_NAME:
                    return NOptional.of(
                            new NDocNodeParserBase(false, NDocNodeType.CTRL_NAME) {

                            }
                    );
            }
        }
        return NOptional.ofNamedEmpty("node type parser for NodeType " + id);
    }

    public void addNodeTypeFactory(NDocNodeParser renderer) {
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
            NOptional<NDocNodeParser> o = nodeTypeParser(i);
            if (o.isPresent()) {
                NDocNodeParser oo = o.get();
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

    private List<NDocNodeParserFactory> nodeParserFactories() {
        if (nodeParserFactories == null) {
            ServiceLoader<NDocNodeParserFactory> renderers = ServiceLoader.load(NDocNodeParserFactory.class);
            List<NDocNodeParserFactory> loaded = new ArrayList<>();
            loaded.add(documentItemParserFactory);
            for (NDocNodeParserFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.nodeParserFactories = loaded;
        }
        return nodeParserFactories;
    }


    public NDocDocumentLoadingResult compileDocument(NDocument document) {
        return new NDocCompiler(this).compile(document);
    }

    @Override
    public List<NDocNode> compilePageNode(NDocNode node, NDocument document) {
        return compilePageNode(node, new NDocCompilePageContextImpl(this, document));
    }

    @Override
    public List<NDocNode> compilePageNode(NDocNode node, NDocCompilePageContext context) {
        return new NDocCompiler(this).compilePageNode(node, context);
    }

    @Override
    public List<NDocNode> compileItem(NDocItem node, NDocCompilePageContext context) {
        List<NDocNode> all = new ArrayList<>();
        if (node instanceof NDocNode) {
            all.addAll(compilePageNode((NDocNode) node, context));
        } else if (node instanceof NDocItemList) {
            for (NDocItem i : ((NDocItemList) node).getItems()) {
                all.addAll(compileItem(i, context));
            }
        } else if (
                node instanceof NDocStyleRule
                        || node instanceof NDocNodeDef
                        || node instanceof NDocProp
        ) {
            //just ignore
        }
        return all;
    }

    public boolean validateNode(NDocNode node) {
        return nodeTypeParser(node.type()).get().validateNode(node);
    }

    @Override
    public NDocDocumentLoadingResult loadDocument(NPath path) {
        NAssert.requireNonNull(path, "path");
        synchronized (this) {
            if (GitHelper.isGithubFolder(path.toString())) {
                path = GitHelper.resolveGithubPath(path.toString(), log());
            }
            NDocResource source = NDocResourceFactory.of(path);
            if (path.exists()) {
                if (path.isRegularFile()) {
                    NDocResource nPathResource = NDocResourceFactory.of(path);
                    NOptional<NElement> f = new NDocStreamParser(this).parsePath(path, nPathResource);
                    if (!f.isPresent()) {
                        log().log(f.getMessage().get().asSevere(), nPathResource);
                    }
                    NElement d = f.get();
                    SilentNDocLogger slog = new SilentNDocLogger();
                    try {
                        this.addLog(slog);
                        NOptional<NDocument> dd = convertDocument(d, source);
                        if (dd.isPresent()) {
//                            if (r.get().root().source() == null) {
//                                r.get().root().setSource(NDocResourceFactory.of(path));
//                            }
                            return new NDocDocumentLoadingResultImpl(dd.get(), nPathResource, slog.isSuccessful());
                        } else {
                            log().log(dd.getMessage().get().asSevere(), nPathResource);
                        }
                    } finally {
                        this.removeLog(slog);
                    }

                } else if (path.isDirectory()) {
                    NDocument document = documentFactory().ofDocument(source);
                    document.resources().add(path.resolve(NDocEngineUtils.NDOC_EXT_STAR));
                    List<NPath> all = path.stream().filter(x -> x.isRegularFile() && NDocEngineUtils.isNDocFile(x)).toList();
                    if (all.isEmpty()) {
                        log().log(
                                NMsg.ofC("invalid folder (no valid enclosed files) %s", path).asSevere()
                        );
                        return new NDocDocumentLoadingResultImpl(document, source, false);
                    }
                    NPath main = null;
                    for (String mainFiled : new String[]{
                            "main.ndoc",
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
                        NOptional<NDocItem> d = null;
                        NDocResource nPathResource = NDocResourceFactory.of(nPath);
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
                        document.root().setSource(NDocResourceFactory.of(path));
                    }
                    return new NDocDocumentLoadingResultImpl(document, source, true);
                } else {
                    log().log(NMsg.ofC("invalid file %s", path).asSevere());
                    return new NDocDocumentLoadingResultImpl(null, source, false);
                }
            }
            log().log(NMsg.ofC("file does not exist %s", path).asSevere());
            return new NDocDocumentLoadingResultImpl(null, source, false);
        }
    }

    private void updateSource(NDocItem item, NDocResource source) {
        if (item != null) {
            if (item instanceof NDocItemList) {
                for (NDocItem hItem : ((NDocItemList) item).getItems()) {
                    updateSource(hItem, source);
                }
            } else if (item instanceof NDocNode) {
                NDocNode i = (NDocNode) item;
                if (i.source() == null) {
                    i.setSource(source);
                }
            }
        }
    }

    @Override
    public NOptional<NDocItem> loadNode(NDocNode into, NPath path, NDocument document) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                NDocResource source = NDocResourceFactory.of(path);
                NOptional<NDocItem> d = loadNode0(into, path, document);
                if (d.isPresent()) {
                    updateSource(d.get(), source);
                }
                return d;
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && NDocEngineUtils.isNDocFile(x.getName())).toList();
                all.sort(NDocEngineUtils::comparePaths);
                NDocItem node = null;
                for (NPath nPath : all) {
                    NOptional<NDocItem> d = loadNode0((node instanceof NDocNode) ? (NDocNode) node : null, nPath, document);
                    if (!d.isPresent()) {
                        log().log(NMsg.ofC("invalid file %s", nPath));
                        return NOptional.ofError(() -> NMsg.ofC("invalid file %s", nPath));
                    }
                    updateSource(d.get(), NDocResourceFactory.of(path));
                    if (node == null) {
                        node = d.get();
                    } else {
                        if (node instanceof NDocNode) {
                            ((NDocNode) node).mergeNode(d.get());
                        } else if (node instanceof NDocItemList) {
                            node = new NDocItemList().addAll(((NDocItemList) node).getItems()).add(node);
                        } else {
                            node = new NDocItemList().add(node).add(d.get());
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


    public NDocDocumentLoadingResult loadDocument(InputStream is) {
        NDocResource source = NDocResourceFactory.of(is);

        SilentNDocLogger slog = new SilentNDocLogger();
        try {
            this.addLog(slog);

            NOptional<NElement> f = new NDocStreamParser(this).parseInputStream(is, source);
            if (!f.isPresent()) {
                log().log(f.getMessage().get().asSevere());
            }
            NElement d = f.get();
            NOptional<NDocument> dd = convertDocument(d, source);
            if (dd.isPresent()) {
                return new NDocDocumentLoadingResultImpl(dd.get(), source, slog.isSuccessful());
            } else {
                log().log(dd.getMessage().get().asSevere());
                return new NDocDocumentLoadingResultImpl(null, source, false);
            }
        } finally {
            this.removeLog(slog);
        }
    }


    private NOptional<NDocument> convertDocument(NElement doc, NDocResource source) {
        if (doc == null) {
            log().log(NMsg.ofPlain("missing document").asError());
            return NOptional.ofNamedEmpty("document");
        }
        NDocument docd = documentFactory().ofDocument(source);
        docd.resources().add(source);
        docd.root().setSource(source);
        NDocNodeFactoryParseContext newContext = new DefaultNDocNodeFactoryParseContext(
                docd,
                doc, this,
                new ArrayList<>(),
                source
        );

        NOptional<NDocItem> r = newNode(doc, newContext);
        if (r.isPresent()) {
            docd.root().append(r.get());
            return NOptional.of(docd);
        }
        log().log(NMsg.ofC("invalid %s", r.getMessage().get()).asSevere());
        return NOptional.of(docd);
    }


    private NOptional<NDocItem> loadNode0(NDocNode into, NPath path, NDocument document) {
        NDocResource source = NDocResourceFactory.of(path);
        document.resources().add(source);
        NElement c = new NDocStreamParser(this).parsePath(path, source).get();
        ArrayList<NDocNode> parents = new ArrayList<>();
        if (into != null) {
            parents.add(into);
        }
        return newNode(c, new DefaultNDocNodeFactoryParseContext(
                document,
                null,
                this,
                parents,
                source
        ));
    }

    @Override
    public NElement toElement(NDocument doc) {
        NDocNode r = doc.root();
        return nodeTypeParser(r.type()).get().toElem(r);
    }


    @Override
    public NElement toElement(NDocNode node) {
        return nodeTypeParser(node.type()).get().toElem(node);
    }


    @Override
    public NOptional<NDocProp> computeProperty(NDocNode node, String... propertyNames) {
        return propCalculator.computeProperty(node, propertyNames);
    }

    @Override
    public List<NDocProp> computeProperties(NDocNode node) {
        return propCalculator.computeProperties(node);
    }

    @Override
    public List<NDocProp> computeInheritedProperties(NDocNode node) {
        return propCalculator.computeInheritedProperties(node);
    }

    @Override
    public <T> NOptional<T> computePropertyValue(NDocNode node, String... propertyNames) {
        return propCalculator.computePropertyValue(node, propertyNames);
    }


    @Override
    public NDocNodeRendererManager renderManager() {
        if (rendererManager == null) {
            rendererManager = new NDocNodeRendererManagerImpl(this);
        }
        return rendererManager;
    }

    @Override
    public NDocGraphics createGraphics(Graphics2D g2d) {
        return new NDocGraphicsImpl(g2d,this);
    }

    @Override
    public void createProject(NPath path, NPath projectUrl, Function<String, String> vars) {
        NAssert.requireNonNull(path, "path");
        NAssert.requireNonNull(projectUrl, "projectUrl");
        if (GitHelper.isGithubFolder(projectUrl.toString())) {
            projectUrl = GitHelper.resolveGithubPath(path.toString(), null);
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

    private String baseNDocTemplatesUrl() {
        if (false) {
            return "/home/vpc/xprojects/productivity/ndoc-templates";
        }
        return "github://thevpc/ndoc-templates";
    }

    @Override
    public String getDefaultTemplateUrl() {
        return baseNDocTemplatesUrl() + "/main/default/v1.0/boot/medium";
    }

    @Override
    public String[] getDefaultTemplateUrls() {
        return new String[]{
                baseNDocTemplatesUrl() + "/main/default/v1.0/boot/small",
                baseNDocTemplatesUrl() + "/main/default/v1.0/boot/medium",
                baseNDocTemplatesUrl() + "/main/default/v1.0/boot/large"
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
            if (NDocEngineUtils.isNDocFile(from)) {
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
    public NDocFunctionArg createRawArg(NDocNode node, NElement expression) {
        return new ExprNDocFunctionArg(node, expression);
    }


    @Override
    public NElement evalExpression(NElement expression, NDocNode node) {
        String baseSrc = NDocUtils.findCompilerDeclarationPath(expression).orNull();
        NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(this);
        NElement u = ne.eval(NElemUtils.toElement(expression), node);
        if (u == null) {
            u = NElement.ofNull();
        }
        if (baseSrc != null) {
            u = NDocUtils.addCompilerDeclarationPath(u, baseSrc);
        }
        return u;
    }

    @Override
    public NElement resolveVarValue(String varName, NDocNode node) {
        NOptional<NDocVar> v = findVar(varName, node);
        if (!v.isPresent()) {
            log().log(NMsg.ofC("var not found %s", varName).asWarning(), NDocUtils.sourceOf(node));
            return null;
        }
        NElement ee = v.get().get();
        return evalExpression(ee, node);
    }

    public NOptional<NDocVar> findVar(String varName, NDocNode node) {
        NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(this);
        return ne.findVar(varName, node);
    }

    @Override
    public NPath resolvePath(NElement path, NDocNode node) {
        if (NBlankable.isBlank(path)) {
            return null;
        }
        if (path.isAnyString()) {
            String pathStr = path.asStringValue().get();
            if (GitHelper.isGithubFolder(pathStr)) {
                return GitHelper.resolveGithubPath(pathStr, log());
            }
            NDocResource source = NDocUtils.sourceOf(node);
            return NDocUtils.resolvePath(path, source);
        }
        throw new NIllegalArgumentException(NMsg.ofC("unsupported path type", path));
    }

    private class ExprNDocFunctionArg implements NDocFunctionArg {
        private final NDocNode node;
        private final NElement expression;

        public ExprNDocFunctionArg(NDocNode node, NElement expression) {
            this.node = node;
            this.expression = expression;
        }

        @Override
        public NElement eval() {
            NElement u = evalExpression(expression, node);
            NElement u2 = evalExpression(u, node);
            return u2;
        }

        @Override
        public String toString() {
            return String.valueOf(expression);
        }
    }

}
