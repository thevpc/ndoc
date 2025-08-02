package net.thevpc.ndoc.engine.base.nodes.text;

import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.renderer.text.NDocTextToken;
import net.thevpc.ndoc.api.renderer.text.NDocTextTokenText;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.util.NStringUtils;

import java.util.*;
import java.util.function.Consumer;

class NDocTextTokenParseHelper {
    private NDocNodeRendererContext ctx;
    private NReservedSimpleCharQueue cq;
    private List<NDocTextRendererFlavor> flavors;
    private NDocNodeCustomBuilderContext builderContext;

    public NDocTextTokenParseHelper(NDocNodeRendererContext ctx, NReservedSimpleCharQueue cq, NDocNodeCustomBuilderContext builderContext) {
        this.ctx = ctx;
        this.cq = cq;
        this.flavors = ctx.engine().textRendererFlavors();
        this.builderContext = builderContext;
    }

    List<NDocTextToken> readSpecial() {
        for (NDocTextRendererFlavor flavor : flavors) {
            if (!Objects.equals(flavor.type(), builderContext.id())) {
                List<NDocTextToken> image = flavor.parseImmediate(cq, ctx);
                if (image != null) {
                    return image;
                }
            }
        }
        return null;
    }

    private List<NDocTextToken> readAny() {
        if (cq.hasNext()) {
            List<NDocTextToken> s = readSpecial();
            if (s != null) {
                return s;
            }
            String p2 = cq.peek(2);
            //this is not special now
            if (p2.equals("[[") || p2.equals("\\(")) {
                cq.read(2);
                return readPlain(cq, p2);
            }
            switch (cq.peek()) {
                case '*': {
                    if (cq.peek(3).equals("***")) {
                        return readBoldItalic();
                    } else if (cq.peek(2).equals("**")) {
                        return readBold();
                    } else {
                        return readPlain(cq,"");
                    }
                }
                case '#': {
                    for (int i = 10; i >= 1; i--) {
                        String r = NStringUtils.repeat('#', i);
                        if (cq.peek(i).equals(r)) {
                            return readColor(i);
                        }
                    }
                    break;
                }
                case '_': {
                    if (cq.peek(2).equals("__")) {
                        return readItalic();
                    } else {
                        return readPlain(cq,"");
                    }
                }
                default: {
                    return readPlain(cq,"");
                }
            }
        }
        return Collections.emptyList();
    }

    private List<NDocTextToken> readBold() {
        return readBounded("**", new String[]{"***"}, tt -> {
            NDocTextOptions o = tt.options();
            if (o.bold == null) {
                o.bold = true;
            }
        });
    }

    private List<NDocTextToken> readItalic() {
        return readBounded("__", tt -> {
            NDocTextOptions o = tt.options();
            if (o.italic == null) {
                o.italic = true;
            }
        });
    }


    private List<NDocTextToken> readBoldItalic() {
        return readBounded("***", tt -> {
            NDocTextOptions o = tt.options();
            if (o.bold == null) {
                o.bold = true;
            }
            if (o.italic == null) {
                o.italic = true;
            }
        });
    }

    private List<NDocTextToken> readColor(int col) {
        String bounds = NStringUtils.repeat('#', col);
        return readBounded(bounds, new String[]{bounds + "#"}, tt -> {
            NDocTextOptions o = tt.options();
            if (o.foregroundColorIndex == null) {
                o.foregroundColorIndex = col;
            }
        });
    }

    private List<NDocTextToken> readBounded(String bounds, Consumer<NDocTextToken> a) {
        return readBounded(bounds, new String[0], a);
    }

    private List<NDocTextToken> readBounded(String bounds, String[] subs, Consumer<NDocTextToken> a) {
        if (!cq.read(bounds.length()).equals(bounds)) {
            throw new IllegalArgumentException("expected " + bounds.length());
        }
        List<NDocTextToken> sbs = new ArrayList<>();
        while (cq.hasNext()) {
            boolean subbed = false;
            for (String sb : subs) {
                if (cq.peek(sb.length()).equals(sb)) {
                    List<NDocTextToken> u = readAny();
                    for (NDocTextToken tt : u) {
                        a.accept(tt);
                        sbs.add(tt);
                    }
                    subbed = true;
                    break;
                }
            }
            if (!subbed) {
                if (cq.peek(bounds.length()).equals(bounds)) {
                    cq.skip(bounds.length());
                    break;
                } else {
                    List<NDocTextToken> u = readAny();
                    for (NDocTextToken tt : u) {
                        a.accept(tt);
                        sbs.add(tt);
                    }
                }
            }
        }
        return sbs;
    }


    private List<NDocTextToken> readPlain(NReservedSimpleCharQueue cq,String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        boolean stop = false;
        while (!stop && cq.hasNext()) {
            String p3 = cq.peek(2);
            if (p3.equals("\\[[") || p3.equals("\\\\(") || p3.equals("\\##") || p3.equals("\\**") || p3.equals("\\__")) {
                sb.append(p3.substring(1));
                cq.read(3);
            } else {
                String p2 = cq.peek(2);
                if (p2.equals("[[") || p2.equals("\\(") || p2.equals("##") || p2.equals("**") || p2.equals("__")) {
                    stop = true;
                } else {
                    sb.append(cq.read());
                }
            }
        }
        if (sb.length() > 0) {
            return Arrays.asList(new NDocTextTokenText(sb.toString()));
        }
        return Collections.emptyList();
    }


    public List<NDocTextToken> parse() {
        List<NDocTextToken> all = new ArrayList<>();
        while (cq.hasNext()) {
            List<NDocTextToken> a = readAny();
            all.addAll(a);
        }
        return all;
    }
}
