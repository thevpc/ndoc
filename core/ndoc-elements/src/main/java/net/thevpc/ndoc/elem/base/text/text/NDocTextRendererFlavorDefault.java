package net.thevpc.ndoc.elem.base.text.text;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.renderer.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.base.parser.HTextUtils;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.util.NStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NDocTextRendererFlavorDefault implements NDocTextRendererFlavor {
    @Override
    public String type() {
        return "";
    }


    @Override
    public void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        NReservedSimpleCharQueue cq = new NReservedSimpleCharQueue(HTextUtils.trimBloc(text).toCharArray());
        while (cq.hasNext()) {
            SpecialToken a = readAny(cq);
            consumeSpecialTokenType(a, p, ctx, builder);
        }
    }

    private void consumeSpecialTokenType(SpecialToken a, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        if (a == null) {
            return;
        }
        switch (a.type) {
            case LIST: {
                for (SpecialToken child : ((SpecialTokenList) a).children) {
                    consumeSpecialTokenType(child, p, ctx, builder);
                }
                break;
            }
            case TXT: {
                SpecialTokenTxt tt = (SpecialTokenTxt) a;
                builder.appendText(tt.image, tt.options, p, ctx);
                break;
            }
            case EQ: {
                SpecialTokenEq tt = (SpecialTokenEq) a;
                    builder.appendCustom("latex-equation", tt.image, tt.options, p, ctx);
                break;
            }
        }
    }


    private SpecialToken readAny(NReservedSimpleCharQueue cq) {
        if (cq.hasNext()) {
            String p2 = cq.peek(2);
            if(
                    p2.equals("\\(")
                    || cq.peek(2).equals("[[")
            ){
                return readEq(cq);
            }
            switch (cq.peek()) {
                case '*': {
                    if (cq.peek(3).equals("***")) {
                        return readBoldItalic(cq);
                    } else if (cq.peek(2).equals("**")) {
                        return readBold(cq);
                    } else {
                        return readPlain(cq);
                    }
                }
                case '#': {
                    for (int i = 10; i >= 1; i--) {
                        String r = NStringUtils.repeat('#', i);
                        if (cq.peek(i).equals(r)) {
                            return readColor(i, cq);
                        }
                    }
                    break;
                }
                case '_': {
                    if (cq.peek(2).equals("__")) {
                        return readItalic(cq);
                    } else {
                        return readPlain(cq);
                    }
                }
                default: {
                    return readPlain(cq);
                }
            }
        }
        return null;
    }

    private SpecialToken readBold(NReservedSimpleCharQueue cq) {
        return readBounded("**", new String[]{"***"}, tt -> {
            NDocTextOptions o = ((SpecialTokenTxtOptions) tt).options;
            if(o.bold==null) {
                o.bold = true;
            }
        }, cq);
    }

    private SpecialToken readItalic(NReservedSimpleCharQueue cq) {
        return readBounded("__", tt -> {
            NDocTextOptions o = ((SpecialTokenTxtOptions) tt).options;
            if(o.italic==null) {
                o.italic = true;
            }
        }, cq);
    }


    private SpecialToken readBoldItalic(NReservedSimpleCharQueue cq) {
        return readBounded("***", tt -> {
            NDocTextOptions o = ((SpecialTokenTxtOptions) tt).options;
            if(o.bold==null) {
                o.bold = true;
            }
            if(o.italic==null) {
                o.italic = true;
            }
        }, cq);
    }

    private SpecialToken readColor(int col, NReservedSimpleCharQueue cq) {
        String bounds = NStringUtils.repeat('#', col);
        return readBounded(bounds,new String[]{bounds+"#"}, tt -> {
            NDocTextOptions o = ((SpecialTokenTxtOptions) tt).options;
            if(o.foregroundColorIndex==null) {
                o.foregroundColorIndex = col;
            }
        }, cq);
    }

    private SpecialToken readBounded(String bounds,Consumer<SpecialToken> a, NReservedSimpleCharQueue cq) {
        return readBounded(bounds,new String[0], a, cq);
    }

    private SpecialToken readBounded(String bounds, String[] subs,Consumer<SpecialToken> a, NReservedSimpleCharQueue cq) {
        if (!cq.read(bounds.length()).equals(bounds)) {
            throw new IllegalArgumentException("expected " + bounds.length());
        }
        List<SpecialToken> sbs = new ArrayList<>();
        while (cq.hasNext()) {
            boolean subbed=false;
            for (String sb : subs) {
                if (cq.peek(sb.length()).equals(sb)) {
                    SpecialToken u = readAny(cq);
                    for (SpecialToken tt : flatten(u)) {
                        switch (tt.type) {
                            case EQ:
                            case TXT: {
                                a.accept(tt);
                                break;
                            }
                        }
                        sbs.add(tt);
                    }
                    subbed=true;
                    break;
                }
            }
            if(!subbed) {
                if (cq.peek(bounds.length()).equals(bounds)) {
                    cq.skip(bounds.length());
                    break;
                } else {
                    SpecialToken u = readAny(cq);
                    for (SpecialToken tt : flatten(u)) {
                        switch (tt.type) {
                            case EQ:
                            case TXT: {
                                a.accept(tt);
                                break;
                            }
                        }
                        sbs.add(tt);
                    }
                }
            }
        }
        if (sbs.size() == 1) {
            return sbs.get(0);
        }
        return new SpecialTokenList(sbs);
    }



    private SpecialToken readPlain(NReservedSimpleCharQueue cq) {
        StringBuilder sb = new StringBuilder();
        boolean stop = false;
        while (!stop && cq.hasNext()) {
            String p3 = cq.peek(2);
            if(p3.equals("\\[[") || p3.equals("\\\\(") || p3.equals("\\##") || p3.equals("\\**") || p3.equals("\\__")) {
                sb.append(p3.substring(1));
                cq.read(3);
            }else {
                String p2 = cq.peek(2);
                if (p2.equals("[[") || p2.equals("\\(") || p2.equals("##") || p2.equals("**") || p2.equals("__")) {
                    stop = true;
                } else {
                    sb.append(cq.read());
                }
            }
        }
        if (sb.length() > 0) {
            return new SpecialTokenTxt(sb.toString());
        }
        return null;
    }

    private SpecialToken readEq(NReservedSimpleCharQueue cq) {
        String start = cq.peek(2);
        String end;
        switch (start){
            case "\\(":{
                cq.skip(2);
                end="\\)";
                break;
            }
            case "[[":{
                cq.skip(2);
                end="]]";
                break;
            }
            default:{
                throw new IllegalArgumentException("expected \\( or [[ for equations");
            }
        }
        NExtendedLatexMathBuilder sb=new NExtendedLatexMathBuilder();
        while (cq.hasNext()) {
            String u = cq.peek(3);
            if(u.equals("\\"+end)){
                sb.append(cq.read(3));
            }else if(cq.peek(2).equals(end)){
                cq.read(2);
                sb.flush();
                return new SpecialTokenEq(sb.toString().trim());
            }else{
                char c = cq.read();
                sb.append(c);
            }
        }
        sb.flush();
        return new SpecialTokenEq(sb.toString().trim());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<SpecialToken> flatten(SpecialToken a) {
        List<SpecialToken> aa = new ArrayList<>();
        if (a != null) {
            switch (a.type) {
                case TXT:
                case EQ: {
                    aa.add(a);
                    break;
                }
                case LIST: {
                    for (SpecialToken child : ((SpecialTokenList) a).children) {
                        aa.addAll(flatten(child));
                    }
                    break;
                }
            }
        }
        return aa;
    }

    private enum SpecialTokenType {
        LIST,
        TXT,
        EQ,
    }

    private static class SpecialToken {
        SpecialTokenType type;
    }

    private static class SpecialTokenList extends SpecialToken {
        {
            type = SpecialTokenType.LIST;
        }

        List<SpecialToken> children = new ArrayList<>();

        public SpecialTokenList(List<SpecialToken> children) {
            this.children = children;
        }
    }

    private static class SpecialTokenTxtOptions extends SpecialToken {
        NDocTextOptions options = new NDocTextOptions();
    }

    private static class SpecialTokenTxt extends SpecialTokenTxtOptions {
        {
            type = SpecialTokenType.TXT;
        }

        String image;

        public SpecialTokenTxt(String image) {
            this.image = image;
        }
    }

    private static class SpecialTokenEq extends SpecialTokenTxtOptions {
        {
            type = SpecialTokenType.EQ;
        }

        String image;

        public SpecialTokenEq(String image) {
            this.image = image;
        }
    }

}
