package net.thevpc.ndoc.engine;

import java.awt.*;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.CompilePageContext;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.ndoc.api.model.node.NDocItemList;
import net.thevpc.ndoc.api.model.node.NDocItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeDef;
import net.thevpc.ndoc.api.style.*;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.engine.fct.DefaultNDocFunctionContext;
import net.thevpc.ndoc.engine.fct.NDocEitherFunction;
import net.thevpc.ndoc.engine.parser.DefaultNDocNodeFactoryParseContext;
import net.thevpc.ndoc.engine.document.NDocCompiler;
import net.thevpc.ndoc.engine.parser.util.GitHelper;
import net.thevpc.ndoc.engine.renderer.NDocDocumentRendererFactoryContextImpl;
import net.thevpc.ndoc.engine.document.NDocPropCalculator;
import net.thevpc.ndoc.engine.document.NDocDocumentFactoryImpl;
import net.thevpc.ndoc.engine.parser.DefaultNDocDocumentItemParserFactory;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.engine.parser.NDocDocumentLoadingResultImpl;
import net.thevpc.ndoc.api.util.HResourceFactory;
import net.thevpc.ndoc.engine.renderer.NDocGraphicsImpl;
import net.thevpc.ndoc.engine.renderer.NDocNodeRendererManagerImpl;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.engine.eval.NDocNodeEvalNDoc;
import net.thevpc.ndoc.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.*;
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
    private NDocPropCalculator NDocPropCalculator = new NDocPropCalculator();
    private NDocNodeRendererManager rendererManager;
    private List<NDocFunction> functions = new ArrayList<>();

    public DefaultNDocEngine() {
        functions.add(new NDocEitherFunction());
    }

    @Override
    public NDocFunctionContext createFunctionContext(NDocItem node) {
        return new DefaultNDocFunctionContext(node, this);
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
        if(ctx.source()==null){
            throw new IllegalArgumentException("unexpected source null");
        }
        NDocNodeFactoryParseContext newContext = new DefaultNDocNodeFactoryParseContext(
                ctx.document()
                , element
                , this
                ,
                Arrays.asList(ctx.nodePath())
                , ctx.source(),
                ctx.messages()
        );
        NOptional<NDocItem> optional = NCallableSupport.resolve(
                        nodeParserFactories().stream()
                                .map(x -> x.parseNode(newContext)),
                        () -> NMsg.ofC("support for node '%s' ", element))
                .toOptional();
        if(optional.isPresent()){
            NDocItem nDocItem = optional.get();
            if(nDocItem instanceof NDocNode) {
                NDocResource s = nDocItem.source();
                if(s==null){
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
                case "for":
                    return NOptional.of(
                            new NDocNodeParserBase(true, "for") {

                            }
                    );
                case "call":
                    return NOptional.of(
                            new NDocNodeParserBase(true, "call") {

                            }
                    );
                case "if":
                    return NOptional.of(
                            new NDocNodeParserBase(true, "if") {

                            }
                    );
                case "assign":
                    return NOptional.of(
                            new NDocNodeParserBase(true, "assign") {

                            }
                    );
                case "expr":
                    return NOptional.of(
                            new NDocNodeParserBase(false, "expr") {

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
            loaded.add(new DefaultNDocDocumentItemParserFactory());
            for (NDocNodeParserFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.nodeParserFactories = loaded;
        }
        return nodeParserFactories;
    }


    public NDocDocumentLoadingResult compileDocument(NDocument document, NDocLogger messages) {
        return new NDocCompiler(this, messages).compile(document);
    }

    @Override
    public List<NDocNode> compileNode(NDocNode node, NDocument document, NDocLogger messages) {
        return compileNode(node, new MyCompilePageContext(messages, document));
    }

    @Override
    public List<NDocNode> compileNode(NDocNode node, CompilePageContext context) {
        return new NDocCompiler(this, context.messages()).compilePage(node, context);
    }

    @Override
    public List<NDocNode> compileItem(NDocItem node, CompilePageContext context) {
        List<NDocNode> all = new ArrayList<>();
        if (node instanceof NDocNode) {
            all.addAll(compileNode((NDocNode) node, context));
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
    public NDocDocumentLoadingResult loadDocument(NPath path, NDocLogger messages) {
        NAssert.requireNonNull(path, "path");
        if (GitHelper.isGithubFolder(path.toString())) {
            path = GitHelper.resolveGithubPath(path.toString(), messages);
        }
        NDocResource source = HResourceFactory.of(path);
        NDocDocumentLoadingResultImpl r = new NDocDocumentLoadingResultImpl(source, messages);
        NDocLoggerDelegateImpl messages1 = r.messages();
        if (path.exists()) {
            if (path.isRegularFile()) {
                NDocResource nPathResource = HResourceFactory.of(path);
                NOptional<NElement> f = loadElement(path, messages);
                if (!f.isPresent()) {
                    messages1.log(NDocMsg.of(f.getMessage().get().asSevere(), nPathResource));
                }
                NElement d = f.get();
                NOptional<NDocument> dd = convertDocument(d, r);
                if (dd.isPresent()) {
                    r.setDocument(dd.get());
                } else if (r.isSuccessful()) {
                    messages1.log(NDocMsg.of(dd.getMessage().get().asSevere(), nPathResource));
                }
                if (r.get().root().source() == null) {
                    r.get().root().setSource(HResourceFactory.of(path));
                }
                return r;
            } else if (path.isDirectory()) {
                NDocument document = documentFactory().ofDocument(source);
                document.resources().add(path.resolve(HEngineUtils.NDOC_EXT_STAR));
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && HEngineUtils.isNDocFile(x)).toList();
                if (all.isEmpty()) {
                    messages1.log(
                            NDocMsg.of(NMsg.ofC("invalid folder (no valid enclosed files) %s", path).asSevere())
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
                    NOptional<NDocItem> d = null;
                    NDocResource nPathResource = HResourceFactory.of(nPath);
                    try {
                        d = loadNode(document.root(), nPath, document, messages1);
                    } catch (Exception ex) {
                        NLog.of(getClass()).error(NMsg.ofC("unable to load %s : %s", nPath, ex).asSevere(), ex);
                        messages1.log(NDocMsg.of(NMsg.ofC("unable to load %s : %s", nPath, ex).asSevere(), nPathResource));
                    }
                    if (d != null) {
                        if (!d.isPresent()) {
                            messages1.log(NDocMsg.of(NMsg.ofC("invalid file %s", nPath).asSevere(), nPathResource));
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
                messages1.log(NDocMsg.of(NMsg.ofC("invalid file %s", path).asSevere()));
                return r;
            }
        }
        messages1.log(NDocMsg.of(NMsg.ofC("file does not exist %s", path).asSevere()));
        return r;
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
    public NOptional<NDocItem> loadNode(NDocNode into, NPath path, NDocument document, NDocLogger messages) {
        if (path.exists()) {
            if (path.isRegularFile()) {
                NDocResource source = HResourceFactory.of(path);
                NOptional<NDocItem> d = loadNode0(into, path, document, messages);
                if (d.isPresent()) {
                    updateSource(d.get(), source);
                }
                return d;
            } else if (path.isDirectory()) {
                List<NPath> all = path.stream().filter(x -> x.isRegularFile() && HEngineUtils.isNDocFile(x.getName())).toList();
                all.sort(HEngineUtils::comparePaths);
                NDocItem node = null;
                for (NPath nPath : all) {
                    NOptional<NDocItem> d = loadNode0((node instanceof NDocNode) ? (NDocNode) node : null, nPath, document, messages);
                    if (!d.isPresent()) {
                        NLog.of(getClass()).error(NMsg.ofC("invalid file %s", nPath));
                        return NOptional.ofError(() -> NMsg.ofC("invalid file %s", nPath));
                    }
                    updateSource(d.get(), HResourceFactory.of(path));
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
                NLog.of(getClass()).error(NMsg.ofC("invalid file %s", path));
                return NOptional.ofError(() -> NMsg.ofC("invalid file %s", path));
            }
        }
        NLog.of(getClass()).error(NMsg.ofC("file does not exist %s", path));
        return NOptional.ofError(() -> NMsg.ofC("file does not exist %s", path));
    }

    private NOptional<NElement> loadElement(InputStream is, NDocLogger messages) {
        try {
            NElement u = NElementParser.ofTson().parse(is);
            u = processControlElements(u, NBooleanRef.of(false), messages);
            return NOptional.of(u);
        } catch (Exception ex) {
            NLog.of(getClass()).error(NMsg.ofC("error loading tson document %s", is), ex);
            return NOptional.ofNamedError("error loading tson document", ex);
        }
    }

    private NOptional<NElement> loadElement(NPath is, NDocLogger messages) {
        try {
            NElement u = NElementParser.ofTson().parse(is);
            u = processControlElements(u, NBooleanRef.of(false), messages);
            return NOptional.of(u);
        } catch (Exception ex) {
            NLog.of(getClass()).error(NMsg.ofC("error loading tson document %s", is), ex);
            return NOptional.ofNamedError("error loading tson document", ex);
        }
    }

    public NDocDocumentLoadingResult loadDocument(InputStream is, NDocLogger messages) {
        NDocDocumentLoadingResultImpl result = new NDocDocumentLoadingResultImpl(HResourceFactory.of(is), messages);
        NOptional<NElement> f = loadElement(is, messages);
        if (!f.isPresent()) {
            result.messages().log(NDocMsg.of(f.getMessage().get().asSevere()));
        }
        NElement d = f.get();
        NOptional<NDocument> dd = convertDocument(d, result);
        if (dd.isPresent()) {
            result.setDocument(dd.get());
        } else if (result.isSuccessful()) {
            result.messages().log(NDocMsg.of(dd.getMessage().get().asSevere()));
        }
        return result;
    }


    private NOptional<NDocument> convertDocument(NElement doc, NDocDocumentLoadingResultImpl result) {
        if (doc == null) {
            result.messages().log(NDocMsg.of(NMsg.ofPlain("missing document").asSevere()));
            return NOptional.ofNamedEmpty("document");
        }
        NDocResource source = result.source();
        NElement c = doc;
        NDocument docd = documentFactory().ofDocument(source);
        docd.resources().add(source);
        docd.root().setSource(source);
        NDocNodeFactoryParseContext newContext = new DefaultNDocNodeFactoryParseContext(
                docd,
                c, this,
                new ArrayList<>(),
                result.source(),
                result.messages()
        );

        NOptional<NDocItem> r = newNode(c, newContext);
        if (r.isPresent()) {
            docd.root().append(r.get());
            return NOptional.of(docd);
        }
        result.messages().log(NDocMsg.of(NMsg.ofC("invalid %s", r.getMessage().get()).asSevere()));
        return NOptional.of(docd);
    }

    class CondBody {
        List<NElement> cond = null;
        List<NElement> trueBody = null;
    }

    class IfInfo {
        CondBody base = null;
        List<CondBody> elseIfs = new ArrayList<>();
        NElement elseBody;

        NElement toElement() {
            NObjectElementBuilder i = NElement.ofObjectBuilder("if");
            if (base.cond.size() == 0) {
                i.addParam("$condition", true);
            } else if (base.cond.size() == 1) {
                i.addParam("$condition", base.cond.get(0));
            } else {
                i.addParam("$condition", NElement.ofNamedUplet("and", base.cond.toArray(new NElement[0])));
            }
            i.addAll(base.trueBody);
            NObjectElementBuilder goodElse = null;
            NObjectElementBuilder deepElse = null;
            for (CondBody b : elseIfs) {
                NObjectElementBuilder i2 = NElement.ofObjectBuilder("if");
                if (b.cond.size() == 0) {
                    i2.set("$condition", true);
                } else if (b.cond.size() == 1) {
                    i2.set("$condition", b.cond.get(0));
                } else {
                    i2.set("$condition", NElement.ofNamedUplet("and", b.cond.toArray(new NElement[0])));
                }
                i2.addAll(base.trueBody);
                if (goodElse == null) {
                    goodElse = i2;
                    deepElse = i2;
                } else {
                    deepElse.set("$else", i2.build());
                }
            }
            if (elseBody != null) {
                if (deepElse != null) {
                    deepElse.set("$else", elseBody);
                } else {
                    goodElse = NElement.ofObjectBuilder();
                    if (elseBody.isObject()) {
                        goodElse.addAll(elseBody.asObject().get().children());
                    } else if (elseBody.isArray()) {
                        goodElse.addAll(elseBody.asArray().get().children());
                    } else {
                        goodElse.add(elseBody);
                    }
                }
            }
            if (goodElse != null) {
                deepElse.set("$else", elseBody);
            }
            return i.build();
        }
    }


    private List<NElement> processControlElements(List<NElement> children, NBooleanRef someChanges, NDocLogger messages) {
        List<NElement> res = new ArrayList<>();
        IfInfo ifInfo = null;
        boolean cc = false;
        if (children == null) {
            return null;
        }
        List<NElement> children2 = new ArrayList<>(children);
        while (!children2.isEmpty()) {
            NElement c = children2.remove(0);
            if (c.isNamedParametrizedObject("if")) {
                if (ifInfo != null) {
                    res.add(ifInfo.toElement());
                    ifInfo = null;
                }
                ifInfo = new IfInfo();
                NObjectElement o = c.asObject().get();
                ifInfo.base = new CondBody();
                ifInfo.base.cond = o.params().get();
                ifInfo.base.trueBody = o.children();
                cc = true;
            } else if (c.isNamedUplet("if")) {
                if (ifInfo != null) {
                    res.add(ifInfo.toElement());
                }
                ifInfo = new IfInfo();
                NObjectElement o = c.asObject().get();
                ifInfo.base = new CondBody();
                ifInfo.base.cond = o.params().get();
                ifInfo.base.trueBody = o.children();
                messages.log(NDocMsg.of(NMsg.ofC("if expression is missing brackets : %s", c).asError()));
                cc = true;
            } else if (c.isNamedParametrizedObject("elseif") && ifInfo != null) {
                NObjectElement o = c.asObject().get();
                CondBody b = new CondBody();
                b.cond = o.params().get();
                b.trueBody = o.children();
                ifInfo.elseIfs.add(b);
                cc = true;
            } else if (c.isNamedObject("else") && ifInfo != null) {
                NObjectElement o = c.asObject().get();
                ifInfo.elseBody = o.builder().name(null).build();
                cc = true;
            } else {
                if (ifInfo != null) {
                    res.add(ifInfo.toElement());
                    ifInfo = null;
                }
                res.add(c);
            }
        }
        if (ifInfo != null) {
            res.add(ifInfo.toElement());
            ifInfo = null;
        }
        if (cc) {
            someChanges.set(true);
            return res;
        }
        return children;
    }

    private NElementAnnotation processControlElementsAnnotation(NElementAnnotation child, NBooleanRef someChanges, NDocLogger messages) {
        NBooleanRef u = NBooleanRef.of(false);
        List<NElement> np = processControlElements(child.params(), u, messages);
        if (u.get()) {
            someChanges.set(true);
            return NElement.ofAnnotation(child.name(), np.toArray(new NElement[0]));
        }
        return child;
    }

    private NElement processControlElements(NElement child, NBooleanRef someChanges, NDocLogger messages) {
        List<NElementAnnotation> annotations = new ArrayList<>();
        boolean changesInAnnotation = false;
        for (NElementAnnotation annotation : child.annotations()) {
            NBooleanRef r = NBooleanRef.of(false);
            annotations.add(processControlElementsAnnotation(annotation, r, messages));
            if (r.get()) {
                changesInAnnotation = true;
            }
        }
        switch (child.type().typeGroup()) {
            case NULL:
            case NUMBER:
            case CUSTOM:
            case TEMPORAL:
            case STREAM:
            case CHAR_SEQUENCE:
            case BOOLEAN:
            case OTHER: {
                if (changesInAnnotation) {
                    child = child.builder().clearAnnotations().addAnnotations(annotations).build();
                    someChanges.set(true);
                }
                return child;
            }
            case OPERATOR: {
                NOperatorElement op = child.asOperator().get();
                NOperatorElementBuilder opb = op.builder();
                NElement first = opb.first().orNull();
                boolean cc = false;
                if (first != null) {
                    NBooleanRef r = NBooleanRef.of(false);
                    NElement u = processControlElements(first, r, messages);
                    if (r.get()) {
                        opb.first(u);
                        cc = true;
                    }
                }
                NElement second = opb.first().orNull();
                if (second != null) {
                    NBooleanRef r = NBooleanRef.of(false);
                    NElement u = processControlElements(second, r, messages);
                    if (r.get()) {
                        opb.second(u);
                        cc = true;
                    }
                }
                if (changesInAnnotation) {
                    opb.clearAnnotations().addAnnotations(annotations);
                    cc = true;
                }
                if (cc) {
                    someChanges.set(true);
                    return opb.build();
                }
                return op;
            }
        }
        switch (child.type()) {
            case OBJECT:
            case NAMED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_PARAMETRIZED_OBJECT: {
                NObjectElement p = child.asObject().get();
                List<NElement> i = p.params().orNull();
                NObjectElementBuilder builder = p.builder();
                boolean anyChange = false;
                if (i != null) {
                    NBooleanRef u = NRef.ofBoolean(false);
                    List<NElement> i2 = processControlElements(i, u, messages);
                    if (u.get()) {
                        builder.setParams(i2);
                        anyChange = true;
                    }
                }

                i = p.children();
                if (i != null) {
                    NBooleanRef u = NRef.ofBoolean(false);
                    List<NElement> i2 = processControlElements(i, u, messages);
                    if (u.get()) {
                        builder.setChildren(i2);
                        anyChange = true;
                    }
                }
                if (changesInAnnotation) {
                    builder.clearAnnotations().addAnnotations(annotations);
                    anyChange = true;
                }
                if (anyChange) {
                    someChanges.set(true);
                    p = builder.build();
                }
                return p;
            }
            case ARRAY:
            case NAMED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_PARAMETRIZED_ARRAY: {
                NArrayElement p = child.asArray().get();
                List<NElement> i = p.params().orNull();
                NArrayElementBuilder builder = p.builder();
                boolean anyChange = false;
                if (i != null) {
                    NBooleanRef u = NRef.ofBoolean(false);
                    List<NElement> i2 = processControlElements(i, u, messages);
                    if (u.get()) {
                        builder.setParams(i2);
                        anyChange = true;
                    }
                }

                i = p.children();
                if (i != null) {
                    NBooleanRef u = NRef.ofBoolean(false);
                    List<NElement> i2 = processControlElements(i, u, messages);
                    if (u.get()) {
                        builder.setChildren(i2);
                        anyChange = true;
                    }
                }
                if (changesInAnnotation) {
                    builder.clearAnnotations().addAnnotations(annotations);
                    anyChange = true;
                }
                if (anyChange) {
                    someChanges.set(true);
                    p = builder.build();
                }
                return p;
            }
            case UPLET:
            case NAMED_UPLET: {
                NUpletElement p = child.asUplet().get();
                List<NElement> i = p.params();
                NUpletElementBuilder builder = p.builder();
                boolean anyChange = false;
                if (i != null) {
                    NBooleanRef u = NRef.ofBoolean(false);
                    List<NElement> i2 = processControlElements(i, u, messages);
                    if (u.get()) {
                        builder.setParams(i2);
                        anyChange = true;
                    }
                }
                if (changesInAnnotation) {
                    builder.clearAnnotations().addAnnotations(annotations);
                    anyChange = true;
                }
                if (anyChange) {
                    someChanges.set(true);
                    p = builder.build();
                }
                return p;
            }
        }
        return child;
    }


    private NOptional<NDocItem> loadNode0(NDocNode into, NPath path, NDocument document, NDocLogger messages) {
        NDocResource source = HResourceFactory.of(path);
        document.resources().add(source);
        NElement c = loadElement(path, messages).get();
        ArrayList<NDocNode> parents = new ArrayList<>();
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
        NDocNode r = doc.root();
        return nodeTypeParser(r.type()).get().toElem(r);
    }


    @Override
    public NElement toElement(NDocNode node) {
        return nodeTypeParser(node.type()).get().toElem(node);
    }


    @Override
    public NOptional<NDocProp> computeProperty(NDocNode node, String... propertyNames) {
        return NDocPropCalculator.computeProperty(node, propertyNames);
    }

    @Override
    public List<NDocProp> computeProperties(NDocNode node) {
        return NDocPropCalculator.computeProperties(node);
    }

    @Override
    public List<NDocProp> computeInheritedProperties(NDocNode node) {
        return NDocPropCalculator.computeInheritedProperties(node);
    }

    @Override
    public <T> NOptional<T> computePropertyValue(NDocNode node, String... propertyNames) {
        return NDocPropCalculator.computePropertyValue(node, propertyNames);
    }


    @Override
    public NDocResource computeSource(NDocItem node) {
        return new NDocCompiler(this, new DefaultNDocLogger(null)).computeSource(node);
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
            if (HEngineUtils.isNDocFile(from)) {
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
    public NElement evalExpression(NDocNode node, NElement expression) {
        String baseSrc = NDocUtils.findCompilerDeclarationPath(expression).orNull();
        NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(this);
        NElement u = ne.eval(node, NElemUtils.toElement(expression));
        if(u==null){
            u=NElement.ofNull();
        }
        if(baseSrc!=null){
            u=NDocUtils.addCompilerDeclarationPath(u, baseSrc);
        }
        return u;
    }

    @Override
    public NElement resolveVarValue(NDocNode node, String varName) {
        return evalExpression(node, NDocUtils.addCompilerDeclarationPath(NElement.ofName("$" + varName),NDocUtils.sourceOf(node)));
    }

    private class ExprNDocFunctionArg implements NDocFunctionArg {
        private final NDocNode node;
        private final NElement expression;

        public ExprNDocFunctionArg(NDocNode node, NElement expression) {
            this.node = node;
            this.expression = expression;
        }

        @Override
        public NElement get() {
            NElement u = evalExpression(node, expression);
            NElement u2 = evalExpression(node, u);
            return u2;
        }

        @Override
        public String toString() {
            return String.valueOf(expression);
        }
    }
}
