package net.thevpc.halfa.elem.base.text.text;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.text.HTextRendererBuilder;
import net.thevpc.halfa.spi.HTextRendererFlavor;
import net.thevpc.halfa.spi.base.parser.HTextUtils;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.text.NTextStyle;
import net.thevpc.nuts.text.NTextStyles;
import net.thevpc.nuts.text.NTexts;
import net.thevpc.nuts.util.NStringUtils;

public class HTextRendererFlavorDefault implements HTextRendererFlavor {
    @Override
    public String type() {
        return "";
    }

    public enum Mode {
        PLAIN,
        EQ,
    }

    @Override
    public void buildText(String text, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        NReservedSimpleCharQueue cq = new NReservedSimpleCharQueue(HTextUtils.trimBloc(text).toCharArray());
        while (cq.hasNext()) {
            readAny(cq, p, ctx, builder);
        }
    }


    private void readAny(NReservedSimpleCharQueue cq, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        if (cq.hasNext()) {
            switch (cq.peek()) {
                case '$': {
                    readEq(cq, p, ctx, builder);
                    break;
                }
                case '*': {
                    if (cq.peek(3).equals("***")) {
                        readBoldItalic(cq, p, ctx, builder);
                    } else if (cq.peek(2).equals("**")) {
                        readBold(cq, p, ctx, builder);
                    } else {
                        readPlain(cq, p, ctx, builder);
                    }
                    break;
                }
                case '#': {
                    for (int i = 10; i >= 1; i--) {
                        String r = NStringUtils.repeat('#', i);
                        if (cq.peek(i).equals(r)) {
                            readColor(i,cq, p, ctx, builder);
                            break;
                        }
                    }
                    break;
                }
                case '_': {
                    if (cq.peek(2).equals("__")) {
                        readItalic(cq, p, ctx, builder);
                    } else {
                        readPlain(cq, p, ctx, builder);
                    }
                    break;
                }
                default: {
                    readPlain(cq, p, ctx, builder);
                }
            }
        }
    }

    private void readBold(NReservedSimpleCharQueue cq, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        if (!cq.read(2).equals("**")) {
            throw new IllegalArgumentException("expected **");
        }
        StringBuilder sb = new StringBuilder();
        boolean stop = false;
        while (!stop && cq.hasNext()) {
            switch (cq.peek()) {
                case '*': {
                    if (cq.peek(2).equals("**")) {
                        cq.skip(2);
                        stop = true;
                        break;
                    } else {
                        sb.append(cq.read());
                    }
                    break;
                }
                default: {
                    sb.append(cq.read());
                }
            }
        }
        String s = sb.toString();
        if (s.length() > 0) {
            if (s.trim().isEmpty()) {
                builder.appendPlain(s, ctx);
            } else {
                NTexts nTexts = NTexts.of(ctx.session());
                builder.appendNText("", s, nTexts.ofStyled(s, NTextStyle.bold()), p, ctx);
            }
        }
    }

    private void readItalic(NReservedSimpleCharQueue cq, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        if (!cq.read(2).equals("__")) {
            throw new IllegalArgumentException("expected __");
        }
        StringBuilder sb = new StringBuilder();
        boolean stop = false;
        while (!stop && cq.hasNext()) {
            switch (cq.peek()) {
                case '_': {
                    if (cq.peek(2).equals("__")) {
                        cq.skip(2);
                        stop = true;
                        break;
                    } else {
                        sb.append(cq.read());
                    }
                    break;
                }
                default: {
                    sb.append(cq.read());
                }
            }
        }
        String s = sb.toString();
        if (s.length() > 0) {
            if (s.trim().isEmpty()) {
                builder.appendPlain(s, ctx);
            } else {
                NTexts nTexts = NTexts.of(ctx.session());
                builder.appendNText("", s, nTexts.ofStyled(s, NTextStyle.italic()), p, ctx);
            }
        }
    }

    private void readColor(int col, NReservedSimpleCharQueue cq, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        String sep = NStringUtils.repeat('#', col);
        if (!cq.read(col).equals(sep)) {
            throw new IllegalArgumentException("expected " + sep);
        }
        StringBuilder sb = new StringBuilder();
        boolean stop = false;
        while (!stop && cq.hasNext()) {
            switch (cq.peek()) {
                case '#': {
                    if (cq.peek(col).equals(sep)) {
                        cq.skip(col);
                        stop = true;
                        break;
                    } else {
                        sb.append(cq.read());
                    }
                    break;
                }
                default: {
                    sb.append(cq.read());
                }
            }
        }
        String s = sb.toString();
        if (s.length() > 0) {
            if (s.trim().isEmpty()) {
                builder.appendPlain(s, ctx);
            } else {
                NTexts nTexts = NTexts.of(ctx.session());
                builder.appendNText("", s, nTexts.ofStyled(s, NTextStyle.primary(col)), p, ctx);
            }
        }
    }

    private void readBoldItalic(NReservedSimpleCharQueue cq, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        if (!cq.read(3).equals("***")) {
            throw new IllegalArgumentException("expected ***");
        }
        StringBuilder sb = new StringBuilder();
        boolean stop = false;
        while (!stop && cq.hasNext()) {
            switch (cq.peek()) {
                case '*': {
                    if (cq.peek(2).equals("***")) {
                        cq.skip(2);
                        stop = true;
                        break;
                    } else {
                        sb.append(cq.read());
                    }
                    break;
                }
                default: {
                    sb.append(cq.read());
                }
            }
        }
        String s = sb.toString();
        if (s.length() > 0) {
            if (s.trim().isEmpty()) {
                builder.appendPlain(s, ctx);
            } else {
                NTexts nTexts = NTexts.of(ctx.session());
                builder.appendNText("", s, nTexts.ofStyled(s, NTextStyles.of(NTextStyle.bold(), NTextStyle.italic())), p, ctx);
            }
        }
    }

    private void readPlain(NReservedSimpleCharQueue cq, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        StringBuilder sb = new StringBuilder();
        boolean stop = false;
        while (!stop && cq.hasNext()) {
            switch (cq.peek()) {
                case '$':
                case '#':
                {
                    stop = true;
                    break;
                }
                case '\\': {
                    cq.skip(1);
                    if (cq.hasNext()) {
                        char n = cq.read();
                        switch (n) {
                            case '\'':
                            case '$': {
                                sb.append(n);
                            }
                            default: {
                                sb.append('\\');
                                sb.append(n);
                            }
                        }
                    } else {
                        sb.append('\\');
                    }
                    break;
                }
                case '*': {
                    if (sb.length() == 0) {
                        sb.append(cq.read());
                    } else if (cq.peek(2).equals("**")) {
                        stop = true;
                    } else {
                        sb.append(cq.read());
                    }
                    break;
                }
                case '_': {
                    if (sb.length() == 0) {
                        sb.append(cq.read());
                    } else if (cq.peek(2).equals("__")) {
                        stop = true;
                    } else {
                        sb.append(cq.read());
                    }
                    break;
                }
                default: {
                    sb.append(cq.read());
                }
            }
        }
        if (sb.length() > 0) {
            builder.appendPlain(sb.toString(), ctx);
        }
    }

    private void readEq(NReservedSimpleCharQueue cq, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        if (cq.peek() != '$') {
            throw new IllegalArgumentException("Unexpected");
        }
        cq.skip(1);
        StringBuilder sb = new StringBuilder();
        while (cq.hasNext()) {
            switch (cq.peek()) {
                case '$': {
                    cq.skip(1);
                    if (sb.length() > 0) {
                        if (!sb.toString().trim().isEmpty()) {
                            builder.appendCustom("latex-equation", sb.toString(), p, ctx);
                        }
                        return;
                    } else {
                        builder.appendPlain("$$", ctx);
                        return;
                    }
                }
                case '\\': {
                    cq.skip(1);
                    if (cq.hasNext()) {
                        char n = cq.read();
                        switch (n) {
                            case '$': {
                                sb.append(n);
                            }
                            default: {
                                sb.append('\\');
                                sb.append(n);
                            }
                        }
                    } else {
                        sb.append('\\');
                    }
                    break;
                }
                default: {
                    sb.append(cq.read());
                }
            }
        }
        if (!sb.toString().trim().isEmpty()) {
            builder.appendCustom("latex-equation", sb.toString(), p, ctx);
        }
    }
}
