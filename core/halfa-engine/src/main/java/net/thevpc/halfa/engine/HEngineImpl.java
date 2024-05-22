package net.thevpc.halfa.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
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
import net.thevpc.nuts.util.NStringUtils;
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

    @Override
    public HDocumentFactory documentFactory() {
        return new HDocumentFactoryImpl();
    }


    @Override
    public NOptional<HNode> newDocumentRoot(TsonElement element) {
        return newNode(element, null,
                new HashSet<>(Arrays.asList(HNodeType.PAGE, HNodeType.PAGE_GROUP)),
                null);
    }

    @Override
    public NOptional<HNode> newPageChild(TsonElement element, HNode currentNode, HNodeFactoryParseContext ctx) {
        Set<HNodeType> expected = new HashSet<>(Arrays.asList(HNodeType.values()));
        expected.remove(HNodeType.PAGE);
        expected.remove(HNodeType.PAGE_GROUP);
        return newNode(element, currentNode, expected, ctx);
    }

    @Override
    public NOptional<HNode> newDocumentChild(TsonElement element, HNode currentNode, HNodeFactoryParseContext ctx) {
        Set<HNodeType> expected = new HashSet<>();
        expected.add(HNodeType.PAGE);
        expected.add(HNodeType.PAGE_GROUP);
        return newNode(element, currentNode, expected, ctx);
    }

    @Override
    public NOptional<HNode> newNode(TsonElement element, HNode currentNode, Set<HNodeType> expected, HNodeFactoryParseContext ctx0) {
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
        HNodeFactoryParseContext ctx = new DefaultHNodeFactoryParseContext(element, this, session, parents, expected0);
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
        if(u instanceof HDocumentScreenRenderer){
            return (HDocumentScreenRenderer) u;
        }
        throw new IllegalArgumentException("resolved renderer is not a valid screen renderer");
    }

    @Override
    public HDocumentStreamRenderer newStreamRenderer(String type) {
        HDocumentRenderer u = newRenderer(type);
        if(u instanceof HDocumentStreamRenderer){
            return (HDocumentStreamRenderer) u;
        }
        throw new IllegalArgumentException("resolved renderer is not a valid stream renderer");
    }

    @Override
    public HDocumentRenderer newRenderer(String type) {
        HDocumentRendererFactoryContext ctx = new MyHDocumentRendererFactoryContext(type);
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
        try (InputStream is = path.getInputStream()) {
            NOptional<HDocument> d = loadDocument(is);
            if (d.isPresent()) {
                d.get().root().setSource(path);
            }
            return d;
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
    }

    public NOptional<HDocument> loadDocument(InputStream is) {
        TsonReader tr = Tson.reader();
        TsonDocument doc;
        try {
            doc = tr.readDocument(is);
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
        TsonElement c = doc.getContent();
        HDocument docd = documentFactory().document();
        NOptional<HNode> r = newDocumentRoot(c);
        if (r.isPresent()) {
            docd.add(r.get());
            return NOptional.of(docd);
        }
        return NOptional.ofError(s -> NMsg.ofC("invalid %s", r.getMessage().apply(s)));
    }

    private class MyHDocumentRendererFactoryContext implements HDocumentRendererFactoryContext {
        private final String type;

        public MyHDocumentRendererFactoryContext(String type) {
            this.type = type;
        }

        @Override
        public String rendererType() {
            return NStringUtils.trim(type);
        }

        @Override
        public HEngine engine() {
            return HEngineImpl.this;
        }

        @Override
        public NSession session() {
            return session;
        }

    }

}
