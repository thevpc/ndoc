/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.text;

import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.util.HTextUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.util.NStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author vpc
 */
public class NDocTextParserBuilder implements NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.TEXT)
                .parseParam().named(NDocPropName.VALUE).then()
                .parseParam().named(NDocPropName.FILE).set(NDocPropName.VALUE).resolvedAs(new NDocNodeCustomBuilderContext.PropResolver() {
                    @Override
                    public NDocProp resolve(String uid, NElement value, NDocArgumentParseInfo info, NDocNodeCustomBuilderContext buildContext) {
                        NPath nPath = buildContext.engine().resolvePath(value.asString().get(), info.node());
                        info.getContext().document().resources().add(nPath);
                        return NDocProp.ofString(uid, nPath.readString().trim());
                    }
                }).then()
                .parseParam().matchesStringOrName().set(NDocPropName.VALUE).then()
                .renderText(this::buildText,this::parseImmediate)
        ;
    }

    public void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder,NDocNodeCustomBuilderContext builderContext) {
        NDocTextTokenParseHelper aa = new NDocTextTokenParseHelper(ctx, new NReservedSimpleCharQueue(HTextUtils.trimBloc(text).toCharArray()));
        List<NDocTextToken> all=aa.parse();
        for (NDocTextToken a : all) {
            consumeSpecialTokenType(a, p, ctx, builder);
        }
    }

    private static class NDocTextTokenParseHelper {
        private NDocNodeRendererContext ctx;
        private NReservedSimpleCharQueue cq;
        private List<NDocTextRendererFlavor> flavors;

        public NDocTextTokenParseHelper(NDocNodeRendererContext ctx, NReservedSimpleCharQueue cq) {
            this.ctx = ctx;
            this.cq = cq;
            this.flavors = ctx.engine().textRendererFlavors();
        }

        List<NDocTextToken> readSpecial() {
            for (NDocTextRendererFlavor flavor : flavors) {
                List<NDocTextToken> image = flavor.parseImmediate(cq, ctx);
                if (image != null) {
                    return image;
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
                switch (cq.peek()) {
                    case '*': {
                        if (cq.peek(3).equals("***")) {
                            return readBoldItalic();
                        } else if (cq.peek(2).equals("**")) {
                            return readBold();
                        } else {
                            return readPlain(cq);
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
                            return readPlain(cq);
                        }
                    }
                    default: {
                        return readPlain(cq);
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


        private List<NDocTextToken> readPlain(NReservedSimpleCharQueue cq) {
            StringBuilder sb = new StringBuilder();
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
            List<NDocTextToken> all=new ArrayList<>();
            while (cq.hasNext()) {
                List<NDocTextToken> a = readAny();
                all.addAll(a);
            }
            return all;
        }
    }

    private void consumeSpecialTokenType(NDocTextToken a, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        if (a == null) {
            return;
        }
        if (a instanceof NDocTextTokenFlavored) {
            NDocTextTokenFlavored b=(NDocTextTokenFlavored)a;
            builder.appendCustom(b.flavor(), b.value(), b.options(), p, ctx);
        }else{
            NDocTextTokenText b=(NDocTextTokenText)a;
            builder.appendText(b.value(), b.options(), p, ctx);
        }
    }

    public List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        NDocTextTokenParseHelper aa = new NDocTextTokenParseHelper(ctx, queue);
        return aa.parse();
    }


}
