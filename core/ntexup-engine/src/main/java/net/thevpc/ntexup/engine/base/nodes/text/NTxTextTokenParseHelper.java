package net.thevpc.ntexup.engine.base.nodes.text;

import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;
import net.thevpc.ntexup.api.renderer.text.NTxTextToken;
import net.thevpc.ntexup.api.renderer.text.NTxTextTokenText;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.util.NStringUtils;

import java.util.*;
import java.util.function.Consumer;

class NTxTextTokenParseHelper {
    private NTxNodeRendererContext ctx;
    private NReservedSimpleCharQueue cq;
    private List<NTxTextRendererFlavor> flavors;
    private NTxNodeCustomBuilderContext builderContext;

    public NTxTextTokenParseHelper(NTxNodeRendererContext ctx, NReservedSimpleCharQueue cq, NTxNodeCustomBuilderContext builderContext) {
        this.ctx = ctx;
        this.cq = cq;
        this.flavors = ctx.engine().textRendererFlavors();
        this.builderContext = builderContext;
    }

    List<NTxTextToken> readSpecial() {
        for (NTxTextRendererFlavor flavor : flavors) {
            if (!Objects.equals(flavor.type(), builderContext.id())) {
                List<NTxTextToken> image = flavor.parseImmediate(cq, ctx);
                if (image != null) {
                    return image;
                }
            }
        }
        return null;
    }

    private List<NTxTextToken> readAny() {
        if (cq.hasNext()) {
            List<NTxTextToken> s = readSpecial();
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

    private List<NTxTextToken> readBold() {
        return readBounded("**", new String[]{"***"}, tt -> {
            NTxTextOptions o = tt.options();
            if (o.bold == null) {
                o.bold = true;
            }
        });
    }

    private List<NTxTextToken> readItalic() {
        return readBounded("__", tt -> {
            NTxTextOptions o = tt.options();
            if (o.italic == null) {
                o.italic = true;
            }
        });
    }


    private List<NTxTextToken> readBoldItalic() {
        return readBounded("***", tt -> {
            NTxTextOptions o = tt.options();
            if (o.bold == null) {
                o.bold = true;
            }
            if (o.italic == null) {
                o.italic = true;
            }
        });
    }

    private List<NTxTextToken> readColor(int col) {
        String bounds = NStringUtils.repeat('#', col);
        return readBounded(bounds, new String[]{bounds + "#"}, tt -> {
            NTxTextOptions o = tt.options();
            if (o.foregroundColorIndex == null) {
                o.foregroundColorIndex = col;
            }
        });
    }

    private List<NTxTextToken> readBounded(String bounds, Consumer<NTxTextToken> a) {
        return readBounded(bounds, new String[0], a);
    }

    private List<NTxTextToken> readBounded(String bounds, String[] subs, Consumer<NTxTextToken> a) {
        if (!cq.read(bounds.length()).equals(bounds)) {
            throw new IllegalArgumentException("expected " + bounds.length());
        }
        List<NTxTextToken> sbs = new ArrayList<>();
        while (cq.hasNext()) {
            boolean subbed = false;
            for (String sb : subs) {
                if (cq.peek(sb.length()).equals(sb)) {
                    List<NTxTextToken> u = readAny();
                    for (NTxTextToken tt : u) {
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
                    List<NTxTextToken> u = readAny();
                    for (NTxTextToken tt : u) {
                        a.accept(tt);
                        sbs.add(tt);
                    }
                }
            }
        }
        return sbs;
    }


    private List<NTxTextToken> readPlain(NReservedSimpleCharQueue cq, String prefix) {
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
            return Arrays.asList(new NTxTextTokenText(sb.toString()));
        }
        return Collections.emptyList();
    }


    public List<NTxTextToken> parse() {
        List<NTxTextToken> all = new ArrayList<>();
        while (cq.hasNext()) {
            List<NTxTextToken> a = readAny();
            all.addAll(a);
        }
        return all;
    }
}
