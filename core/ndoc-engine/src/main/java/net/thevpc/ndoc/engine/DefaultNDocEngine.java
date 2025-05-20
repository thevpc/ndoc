package net.thevpc.ndoc.engine;

import java.awt.*;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.model.node.HItemList;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.*;
import net.thevpc.ndoc.engine.parser.DefaultNDocNodeFactoryParseContext;
import net.thevpc.ndoc.engine.document.HDocumentCompiler;
import net.thevpc.ndoc.engine.parser.util.GitHelper;
import net.thevpc.ndoc.engine.renderer.NDocDocumentRendererFactoryContextImpl;
import net.thevpc.ndoc.engine.document.HPropCalculator;
import net.thevpc.ndoc.engine.document.HDocDocumentFactoryImpl;
import net.thevpc.ndoc.engine.parser.DefaultNDocDocumentItemParserFactory;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.engine.parser.HDocumentLoadingResultImpl;
import net.thevpc.ndoc.api.util.HResourceFactory;
import net.thevpc.ndoc.engine.renderer.NDocGraphicsImpl;
import net.thevpc.ndoc.engine.renderer.NDocNodeRendererManagerImpl;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.ndoc.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.log.NLog;
import net.thevpc.nuts.util.*;

import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.spi.nodes.NDocNodeParserFactory;

/**
 * @author vpc
 */
public class DefaultNDocEngine implements NDocEngine {

    private List<NDocDocumentRendererFactory> documentRendererFactories;
    private List<NDocNodeParserFactory> nodeParserFactories;

    private Map<String, NDocNodeParser> nodeTypeFactories;
    private Map<String, String> nodeTypeAliases;
    private NDocDocumentFactory factory;
    private HPropCalculator hPropCalculator = new HPropCalculator();
    private NDocNodeRendererManager rendererManager;

    public DefaultNDocEngine() {
    }


    @Override
    public NDocDocumentFactory documentFactory() {
        if (factory == null) {
            factory = new HDocDocumentFactoryImpl(this);
        }
        return factory;
    }


    @Override
    public NOptional<HItem> newNode(NElement element, NDocNodeFactoryParseContext ctx) {
        NAssert.requireNonNull(ctx, "context");
        NAssert.requireNonNull(element, "element");
        NDocNodeFactoryParseContext newContext = new DefaultNDocNodeFactoryParseContext(
                ctx.document()
                , element
                , this
                ,
                Arrays.asList(ctx.nodePath())
                , ctx.source(),
                ctx.messages()
        );
        return NCallableSupport.resolve(
                        nodeParserFactories().stream()
                                .map(x -> x.parseNode(newContext)),
                        () -> NMsg.ofC("support for node '%s' ", element))
                .toOptional();
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

    private Map<String, NDocNodeParser> nodeTypeFactories0() {
        if (nodeTypeFactories == null) {
            nodeTypeFactories = new HashMap<>();
            nodeTypeAliases = new HashMap<>();
            ServiceLoader<NDocNodeParser> renderers = ServiceLoader.load(NDocNodeParser.class);
            for (NDocNodeParser renderer : renderers) {
                addNodeTypeFactory(renderer);
            }
        }
        return nodeTypeFactories;
    }


    public NOptional<NDocNodeParser> nodeTypeFactory(String id) {
        id = NStringUtils.trim(id);
        if (!id.isEmpty()) {
            id = NNameFormat.LOWER_KEBAB_CASE.format(id);
            NDocNodeParser o = nodeTypeFactories0().get(id);
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
            NOptional<NDocNodeParser> o = nodeTypeFactory(i);
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
            loaded.add(new DefaultNDocDocumentItemParserFactory());
            for (NDocNodeParserFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.nodeParserFactories = loaded;
        }
        return nodeParserFactories;
    }


    public HDocumentLoadingResult compileDocument(NDocument document, HLogger messages) {
        return new HDocumentCompiler(this, messages).compile(document);
    }

    public boolean validateNode(HNode node) {
        return nodeTypeFactory(node.type()).get().validateNode(node);
    }

    @Override
    public HDocumentLoadingResult loadDocument(NPath path, HLogger messages) {
        NAssert.requireNonNull(path, "path");
        if (GitHelper.isGithubFolder(path.toString())) {
            path = GitHelper.resolveGithubPath(path.toString(), messages);
        }
        HResource source = HResourceFactory.of(path);
        HDocumentLoadingResultImpl r = new HDocumentLoadingResultImpl(source, messages);
        HLoggerDelegateImpl messages1 = r.messages();
        if (path.exists()) {
            if (path.isRegularFile()) {
                HResource nPathResource = HResourceFactory.of(path);
                NOptional<NElement> f = loadDocument(path);
                if (!f.isPresent()) {
                    messages1.log(HMsg.of(f.getMessage().get().asSevere(), nPathResource));
                }
                NElement d = f.get();
                NOptional<NDocument> dd = convertDocument(d, r);
                if (dd.isPresent()) {
                    r.setDocument(dd.get());
                } else if (r.isSuccessful()) {
                    messages1.log(HMsg.of(dd.getMessage().get().asSevere(), nPathResource));
                }
                if (r.get().root().source() == null) {
                    r.get().root().setSource(HResourceFactory.of(path));
                }
                return r;
            } else if (path.isDirectory()) {
                NDocument document = documentFactory().ofDocument();
                document.resources().add(path.resolve(HEngineUtils.NDOC_EXT_STAR));
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && HEngineUtils.isHalfaFile(x)).toList();
                if (all.isEmpty()) {
                    messages1.log(
                            HMsg.of(NMsg.ofC("invalid folder (no valid enclosed files) %s", path).asSevere())
                    );
                    return r;
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
                    NOptional<HItem> d = null;
                    HResource nPathResource = HResourceFactory.of(nPath);
                    try {
                        d = loadNode(document.root(), nPath, document, messages1);
                    } catch (Exception ex) {
                        NLog.of(getClass()).error(NMsg.ofC("unable to load %s : %s", nPath, ex).asSevere(), ex);
                        messages1.log(HMsg.of(NMsg.ofC("unable to load %s : %s", nPath, ex).asSevere(), nPathResource));
                    }
                    if (d != null) {
                        if (!d.isPresent()) {
                            messages1.log(HMsg.of(NMsg.ofC("invalid file %s", nPath).asSevere(), nPathResource));
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
                messages1.log(HMsg.of(NMsg.ofC("invalid file %s", path).asSevere()));
                return r;
            }
        }
        messages1.log(HMsg.of(NMsg.ofC("file does not exist %s", path).asSevere()));
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
    public NOptional<HItem> loadNode(HNode into, NPath path, NDocument document, HLogger messages) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                NOptional<HItem> d = loadNode0(into, path, document, messages);
                if (d.isPresent()) {
                    updateSource(d.get(), HResourceFactory.of(path));
                }
                return d;
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && HEngineUtils.isHalfaFile(x.getName())).toList();
                all.sort(HEngineUtils::comparePaths);
                HItem node = null;
                for (NPath nPath : all) {
                    NOptional<HItem> d = loadNode0((node instanceof HNode) ? (HNode) node : null, nPath, document, messages);
                    if (!d.isPresent()) {
                        NLog.of(getClass()).error(NMsg.ofC("invalid file %s", nPath));
                        return NOptional.ofError(() -> NMsg.ofC("invalid file %s", nPath));
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
                NLog.of(getClass()).error(NMsg.ofC("invalid file %s", path));
                return NOptional.ofError(() -> NMsg.ofC("invalid file %s", path));
            }
        }
        NLog.of(getClass()).error(NMsg.ofC("file does not exist %s", path));
        return NOptional.ofError(() -> NMsg.ofC("file does not exist %s", path));
    }

    private NOptional<NElement> loadDocument(InputStream is) {
        try {
            return NOptional.of(NElements.of().tson().parse(is));
        } catch (Exception ex) {
            NLog.of(getClass()).error(NMsg.ofC("error loading tson document %s", is), ex);
            return NOptional.ofNamedError("error loading tson document", ex);
        }
    }

    private NOptional<NElement> loadDocument(NPath is) {
        try {
            return NOptional.of(NElements.of().tson().parse(is));
        } catch (Exception ex) {
            NLog.of(getClass()).error(NMsg.ofC("error loading tson document %s", is), ex);
            return NOptional.ofNamedError("error loading tson document", ex);
        }
    }

    public HDocumentLoadingResult loadDocument(InputStream is, HLogger messages) {
        HDocumentLoadingResultImpl result = new HDocumentLoadingResultImpl(HResourceFactory.of(is), messages);
        NOptional<NElement> f = loadDocument(is);
        if (!f.isPresent()) {
            result.messages().log(HMsg.of(f.getMessage().get().asSevere()));
        }
        NElement d = f.get();
        NOptional<NDocument> dd = convertDocument(d, result);
        if (dd.isPresent()) {
            result.setDocument(dd.get());
        } else if (result.isSuccessful()) {
            result.messages().log(HMsg.of(dd.getMessage().get().asSevere()));
        }
        return result;
    }


    private NOptional<NDocument> convertDocument(NElement doc, HDocumentLoadingResultImpl result) {
        if (doc == null) {
            result.messages().log(HMsg.of(NMsg.ofPlain("missing document").asSevere()));
            return NOptional.ofNamedEmpty("document");
        }
        HResource source = result.source();
        NElement c = doc;
        NDocument docd = documentFactory().ofDocument();
        docd.resources().add(source);
        docd.root().setSource(source);
        NDocNodeFactoryParseContext newContext = new DefaultNDocNodeFactoryParseContext(
                docd,
                c, this,
                new ArrayList<>(),
                result.source(),
                result.messages()
        );

        NOptional<HItem> r = newNode(c, newContext);
        if (r.isPresent()) {
            docd.root().append(r.get());
            return NOptional.of(docd);
        }
        result.messages().log(HMsg.of(NMsg.ofC("invalid %s", r.getMessage().get()).asSevere()));
        return NOptional.of(docd);
    }


    private NOptional<HItem> loadNode0(HNode into, NPath path, NDocument document, HLogger messages) {
        HResource source = HResourceFactory.of(path);
        document.resources().add(source);
        NElement c;
        try {
            c = NElements.of().tson().parse(path);
        } catch (Throwable ex) {
            NLog.of(getClass()).error(NMsg.ofC("error parsing node from %s : %s", path, ex).asSevere(), ex);
            messages.log(HMsg.of(NMsg.ofC("error parsing node from %s : %s", path, ex).asSevere(), ex, source));
            return NOptional.ofNamedError(NMsg.ofC("error parsing node from %s : %s", path, ex));
        }
        ArrayList<HNode> parents = new ArrayList<>();
        if (into != null) {
            parents.add(into);
        }
        return newNode(c, new DefaultNDocNodeFactoryParseContext(
                document,
                null,
                this,
                parents,
                source,
                messages
        ));
    }

    @Override
    public NElement toElement(NDocument doc) {
        HNode r = doc.root();
        return nodeTypeFactory(r.type()).get().toElem(r);
    }


    @Override
    public NElement toElement(HNode node) {
        return nodeTypeFactory(node.type()).get().toElem(node);
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
        return new HDocumentCompiler(this, new DefaultHLogger(null)).computeSource(node);
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
        return new NDocGraphicsImpl(g2d);
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
                        NLog.of(getClass()).error(NMsg.ofC("Failed to resolve template boot url from %s", finalProjectUrl, ex).asSevere(), ex);
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
            if (HEngineUtils.isHalfaFile(from)) {
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
