package net.thevpc.halfa.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import net.thevpc.halfa.HalfaDocumentFactory;
import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.halfa.api.model.*;
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
import net.thevpc.halfa.spi.nodes.HDocumentItemFactoryParseContext;
import net.thevpc.halfa.spi.nodes.HDocumentPartParserFactory;
import net.thevpc.halfa.spi.nodes.HPagePartParserFactory;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public class DefaultHalfaEngine implements HalfaEngine {

    private NSession session;
    private List<HDocumentStreamRendererFactory> documentStreamRendererFactories;
    private List<HDocumentRendererFactory> documentRendererFactories;
    private List<HDocumentPartParserFactory> documentPartParserFactories;
    private List<HPagePartParserFactory> pagePartParserFactories;

    public DefaultHalfaEngine(NSession session) {
        this.session = session;

    }

    @Override
    public HalfaDocumentFactory factory() {
        return new DefaultHalfaDocumentFactory();
    }



    @Override
    public NOptional<HDocumentPart> newDocumentPart(TsonElement element) {
        HDocumentItemFactoryParseContext ctx = new HDocumentItemFactoryParseContext() {
            @Override
            public NSession session() {
                return session;
            }

            @Override
            public HalfaEngine engine() {
                return DefaultHalfaEngine.this;
            }

            @Override
            public TsonElement element() {
                return element;
            }

        };
        return NCallableSupport.resolve(
                        documentPartParserFactories().stream()
                                .map(x -> x.parseDocumentPart(ctx)),
                        s -> NMsg.ofC("missing %s", "factory"))
                .toOptional();
    }

    @Override
    public NOptional<HPagePart> newPagePart(TsonElement element) {
        HDocumentItemFactoryParseContext ctx = new HDocumentItemFactoryParseContext() {
            @Override
            public NSession session() {
                return session;
            }

            @Override
            public HalfaEngine engine() {
                return DefaultHalfaEngine.this;
            }

            @Override
            public TsonElement element() {
                return element;
            }

        };
        return NCallableSupport.resolve(
                        pagePartParserFactories().stream()
                                .map(x -> x.parsePagePart(ctx)),
                        s -> NMsg.ofC("missing %s", "factory"))
                .toOptional();
    }

    @Override
    public HDocumentStreamRenderer newStreamRenderer(String type) {
        HDocumentRendererFactoryContext ctx = new MyHDocumentRendererFactoryContext(type);
        return NCallableSupport.resolve(
                        documentStreamRendererFactories().stream()
                                .map(x -> x.<HDocumentStreamRenderer>createDocumentStreamRenderer(ctx)),
                        s -> NMsg.ofC("missing StreamRenderer %s", type))
                .call(session);
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

    private List<HDocumentStreamRendererFactory> documentStreamRendererFactories() {
        if (documentStreamRendererFactories == null) {
            ServiceLoader<HDocumentStreamRendererFactory> renderers = ServiceLoader.load(HDocumentStreamRendererFactory.class);
            List<HDocumentStreamRendererFactory> loaded = new ArrayList<>();
            for (HDocumentStreamRendererFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.documentStreamRendererFactories = loaded;
        }
        return documentStreamRendererFactories;
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

    private List<HDocumentPartParserFactory> documentPartParserFactories() {
        if (documentPartParserFactories == null) {
            ServiceLoader<HDocumentPartParserFactory> renderers = ServiceLoader.load(HDocumentPartParserFactory.class);
            List<HDocumentPartParserFactory> loaded = new ArrayList<>();
            loaded.add(new DefaultHDocumentItemParserFactory());
            for (HDocumentPartParserFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.documentPartParserFactories = loaded;
        }
        return documentPartParserFactories;
    }

    private List<HPagePartParserFactory> pagePartParserFactories() {
        if (pagePartParserFactories == null) {
            ServiceLoader<HPagePartParserFactory> renderers = ServiceLoader.load(HPagePartParserFactory.class);
            List<HPagePartParserFactory> loaded = new ArrayList<>();
            loaded.add(new DefaultHDocumentItemParserFactory());
            for (HPagePartParserFactory renderer : renderers) {
                loaded.add(renderer);
            }
            this.pagePartParserFactories = loaded;
        }
        return pagePartParserFactories;
    }



    @Override
    public HDocument loadDocument(NPath path) {
        try (InputStream is = path.getInputStream()) {
            return loadDocument(is);
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
    }

    public HDocument loadDocument(InputStream is) {
        TsonReader tr = Tson.reader();
        TsonDocument doc;
        try {
            doc = tr.readDocument(is);
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
        TsonElement c = doc.getContent();
        return new HTsonReader(this, session).convertToHDocument(c);
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
        public HalfaEngine engine() {
            return DefaultHalfaEngine.this;
        }

        @Override
        public NSession session() {
            return session;
        }

    }
}
