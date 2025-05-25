package net.thevpc.ndoc.spi.eval;

import net.thevpc.ndoc.api.model.HArrow;
import net.thevpc.ndoc.api.model.HArrowType;
import net.thevpc.ndoc.api.model.elem2d.*;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.util.DefaultColorPalette;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.*;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

import net.thevpc.ndoc.api.util.NElemUtils;

public class NDocObjEx {

    private Object element;
    private String name;
    private List<NElement> args = new ArrayList<>();
    private List<NElement> children = new ArrayList<>();
    private static Map<String, Color> predifinedColors = new HashMap<>();

    private static NOptional<Color> getRegisteredColor(String name) {
        Color u = predifinedColors.get(NNameFormat.LOWER_KEBAB_CASE.format(name));
        return NOptional.of(u, () -> NMsg.ofC("color %s", name));
    }

    private static void registerColor(String name, Color color) {
        predifinedColors.put(NNameFormat.LOWER_KEBAB_CASE.format(name), color);
    }

    static {
        registerColor("Black", new Color(0, 0, 0));
        registerColor("Maroon", new Color(128, 0, 0));
        registerColor("Green", new Color(0, 128, 0));
        registerColor("Olive", new Color(128, 128, 0));
        registerColor("Navy", new Color(0, 0, 128));
        registerColor("Purple", new Color(128, 0, 128));
        registerColor("Teal", new Color(0, 128, 128));
        registerColor("Silver", new Color(192, 192, 192));
        registerColor("Grey", new Color(128, 128, 128));
        registerColor("Red", new Color(255, 0, 0));
        registerColor("Lime", new Color(0, 255, 0));
        registerColor("Yellow", new Color(255, 255, 0));
        registerColor("Blue", new Color(0, 0, 255));
        registerColor("Fuchsia", new Color(255, 0, 255));
        registerColor("Aqua", new Color(0, 255, 255));
        registerColor("White", new Color(255, 255, 255));
        registerColor("Grey0", new Color(0, 0, 0));
        registerColor("NavyBlue", new Color(0, 0, 95));
        registerColor("DarkBlue", new Color(0, 0, 135));
        registerColor("Blue2", new Color(0, 0, 175));
        registerColor("Blue3", new Color(0, 0, 215));
        registerColor("Blue4", new Color(0, 0, 255));
        registerColor("DarkGreen", new Color(0, 95, 0));
        registerColor("DeepSkyBlue", new Color(0, 95, 95));
        registerColor("DeepSkyBlue2", new Color(0, 95, 135));
        registerColor("DeepSkyBlue3", new Color(0, 95, 175));
        registerColor("DodgerBlue", new Color(0, 95, 215));
        registerColor("DodgerBlue2", new Color(0, 95, 255));
        registerColor("Green2", new Color(0, 135, 0));
        registerColor("SpringGreen", new Color(0, 135, 95));
        registerColor("Turquoise", new Color(0, 135, 135));
        registerColor("DeepSkyBlue4", new Color(0, 135, 175));
        registerColor("DeepSkyBlue5", new Color(0, 135, 215));
        registerColor("DodgerBlue3", new Color(0, 135, 255));
        registerColor("Green3", new Color(0, 175, 0));
        registerColor("SpringGreen2", new Color(0, 175, 95));
        registerColor("DarkCyan", new Color(0, 175, 135));
        registerColor("LightSeaGreen", new Color(0, 175, 175));
        registerColor("DeepSkyBlue6", new Color(0, 175, 215));
        registerColor("DeepSkyBlue7", new Color(0, 175, 255));
        registerColor("Green4", new Color(0, 215, 0));
        registerColor("SpringGreen3", new Color(0, 215, 95));
        registerColor("SpringGreen4", new Color(0, 215, 135));
        registerColor("Cyan", new Color(0, 215, 175));
        registerColor("DarkTurquoise", new Color(0, 215, 215));
        registerColor("Turquoise2", new Color(0, 215, 255));
        registerColor("Green5", new Color(0, 255, 0));
        registerColor("SpringGreen5", new Color(0, 255, 95));
        registerColor("SpringGreen6", new Color(0, 255, 135));
        registerColor("MediumSpringGreen", new Color(0, 255, 175));
        registerColor("Cyan2", new Color(0, 255, 215));
        registerColor("Cyan3", new Color(0, 255, 255));
        registerColor("DarkRed", new Color(95, 0, 0));
        registerColor("DeepPink", new Color(95, 0, 95));
        registerColor("Purple2", new Color(95, 0, 135));
        registerColor("Purple3", new Color(95, 0, 175));
        registerColor("Purple4", new Color(95, 0, 215));
        registerColor("BlueViolet", new Color(95, 0, 255));
        registerColor("Orange", new Color(95, 95, 0));
        registerColor("Grey37", new Color(95, 95, 95));
        registerColor("MediumPurple", new Color(95, 95, 135));
        registerColor("SlateBlue", new Color(95, 95, 175));
        registerColor("SlateBlue2", new Color(95, 95, 215));
        registerColor("RoyalBlue", new Color(95, 95, 255));
        registerColor("Chartreuse", new Color(95, 135, 0));
        registerColor("DarkSeaGreen", new Color(95, 135, 95));
        registerColor("PaleTurquoise", new Color(95, 135, 135));
        registerColor("SteelBlue", new Color(95, 135, 175));
        registerColor("SteelBlue2", new Color(95, 135, 215));
        registerColor("CornflowerBlue", new Color(95, 135, 255));
        registerColor("Chartreuse2", new Color(95, 175, 0));
        registerColor("DarkSeaGreen2", new Color(95, 175, 95));
        registerColor("CadetBlue", new Color(95, 175, 135));
        registerColor("CadetBlue2", new Color(95, 175, 175));
        registerColor("SkyBlue", new Color(95, 175, 215));
        registerColor("SteelBlue3", new Color(95, 175, 255));
        registerColor("Chartreuse3", new Color(95, 215, 0));
        registerColor("PaleGreen", new Color(95, 215, 95));
        registerColor("SeaGreen", new Color(95, 215, 135));
        registerColor("Aquamarine", new Color(95, 215, 175));
        registerColor("MediumTurquoise", new Color(95, 215, 215));
        registerColor("SteelBlue4", new Color(95, 215, 255));
        registerColor("Chartreuse4", new Color(95, 255, 0));
        registerColor("SeaGreen2", new Color(95, 255, 95));
        registerColor("SeaGreen3", new Color(95, 255, 135));
        registerColor("SeaGreen4", new Color(95, 255, 175));
        registerColor("Aquamarine2", new Color(95, 255, 215));
        registerColor("DarkSlateGray", new Color(95, 255, 255));
        registerColor("DarkRed2", new Color(135, 0, 0));
        registerColor("DeepPink2", new Color(135, 0, 95));
        registerColor("DarkMagenta", new Color(135, 0, 135));
        registerColor("DarkMagenta2", new Color(135, 0, 175));
        registerColor("DarkViolet", new Color(135, 0, 215));
        registerColor("Purple5", new Color(135, 0, 255));
        registerColor("Orange2", new Color(135, 95, 0));
        registerColor("LightPink", new Color(135, 95, 95));
        registerColor("Plum", new Color(135, 95, 135));
        registerColor("MediumPurple2", new Color(135, 95, 175));
        registerColor("MediumPurple3", new Color(135, 95, 215));
        registerColor("SlateBlue3", new Color(135, 95, 255));
        registerColor("Yellow2", new Color(135, 135, 0));
        registerColor("Wheat", new Color(135, 135, 95));
        registerColor("Grey53", new Color(135, 135, 135));
        registerColor("LightSlateGrey", new Color(135, 135, 175));
        registerColor("MediumPurple4", new Color(135, 135, 215));
        registerColor("LightSlateBlue", new Color(135, 135, 255));
        registerColor("Yellow3", new Color(135, 175, 0));
        registerColor("DarkOliveGreen", new Color(135, 175, 95));
        registerColor("DarkSeaGreen3", new Color(135, 175, 135));
        registerColor("LightSkyBlue", new Color(135, 175, 175));
        registerColor("LightSkyBlue2", new Color(135, 175, 215));
        registerColor("SkyBlue2", new Color(135, 175, 255));
        registerColor("Chartreuse5", new Color(135, 215, 0));
        registerColor("DarkOliveGreen2", new Color(135, 215, 95));
        registerColor("PaleGreen2", new Color(135, 215, 135));
        registerColor("DarkSeaGreen4", new Color(135, 215, 175));
        registerColor("DarkSlateGray2", new Color(135, 215, 215));
        registerColor("SkyBlue3", new Color(135, 215, 255));
        registerColor("Chartreuse6", new Color(135, 255, 0));
        registerColor("LightGreen", new Color(135, 255, 95));
        registerColor("LightGreen2", new Color(135, 255, 135));
        registerColor("PaleGreen3", new Color(135, 255, 175));
        registerColor("Aquamarine3", new Color(135, 255, 215));
        registerColor("DarkSlateGray3", new Color(135, 255, 255));
        registerColor("Red2", new Color(175, 0, 0));
        registerColor("DeepPink3", new Color(175, 0, 95));
        registerColor("MediumVioletRed", new Color(175, 0, 135));
        registerColor("Magenta", new Color(175, 0, 175));
        registerColor("DarkViolet2", new Color(175, 0, 215));
        registerColor("Purple6", new Color(175, 0, 255));
        registerColor("DarkOrange", new Color(175, 95, 0));
        registerColor("IndianRed", new Color(175, 95, 95));
        registerColor("HotPink", new Color(175, 95, 135));
        registerColor("MediumOrchid", new Color(175, 95, 175));
        registerColor("MediumOrchid2", new Color(175, 95, 215));
        registerColor("MediumPurple5", new Color(175, 95, 255));
        registerColor("DarkGoldenrod", new Color(175, 135, 0));
        registerColor("LightSalmon", new Color(175, 135, 95));
        registerColor("RosyBrown", new Color(175, 135, 135));
        registerColor("Grey63", new Color(175, 135, 175));
        registerColor("MediumPurple6", new Color(175, 135, 215));
        registerColor("MediumPurple7", new Color(175, 135, 255));
        registerColor("Gold", new Color(175, 175, 0));
        registerColor("DarkKhaki", new Color(175, 175, 95));
        registerColor("NavajoWhite", new Color(175, 175, 135));
        registerColor("Grey69", new Color(175, 175, 175));
        registerColor("LightSteelBlue", new Color(175, 175, 215));
        registerColor("LightSteelBlue2", new Color(175, 175, 255));
        registerColor("Yellow4", new Color(175, 215, 0));
        registerColor("DarkOliveGreen3", new Color(175, 215, 95));
        registerColor("DarkSeaGreen5", new Color(175, 215, 135));
        registerColor("DarkSeaGreen6", new Color(175, 215, 175));
        registerColor("LightCyan", new Color(175, 215, 215));
        registerColor("LightSkyBlue3", new Color(175, 215, 255));
        registerColor("GreenYellow", new Color(175, 255, 0));
        registerColor("DarkOliveGreen4", new Color(175, 255, 95));
        registerColor("PaleGreen4", new Color(175, 255, 135));
        registerColor("DarkSeaGreen7", new Color(175, 255, 175));
        registerColor("DarkSeaGreen8", new Color(175, 255, 215));
        registerColor("PaleTurquoise2", new Color(175, 255, 255));
        registerColor("Red3", new Color(215, 0, 0));
        registerColor("DeepPink4", new Color(215, 0, 95));
        registerColor("DeepPink5", new Color(215, 0, 135));
        registerColor("Magenta2", new Color(215, 0, 175));
        registerColor("Magenta3", new Color(215, 0, 215));
        registerColor("Magenta4", new Color(215, 0, 255));
        registerColor("DarkOrange2", new Color(215, 95, 0));
        registerColor("IndianRed2", new Color(215, 95, 95));
        registerColor("HotPink2", new Color(215, 95, 135));
        registerColor("HotPink3", new Color(215, 95, 175));
        registerColor("Orchid", new Color(215, 95, 215));
        registerColor("MediumOrchid3", new Color(215, 95, 255));
        registerColor("Orange3", new Color(215, 135, 0));
        registerColor("LightSalmon2", new Color(215, 135, 95));
        registerColor("LightPink2", new Color(215, 135, 135));
        registerColor("Pink", new Color(215, 135, 175));
        registerColor("Plum2", new Color(215, 135, 215));
        registerColor("Violet", new Color(215, 135, 255));
        registerColor("Gold2", new Color(215, 175, 0));
        registerColor("LightGoldenrod", new Color(215, 175, 95));
        registerColor("Tan", new Color(215, 175, 135));
        registerColor("MistyRose", new Color(215, 175, 175));
        registerColor("Thistle", new Color(215, 175, 215));
        registerColor("Plum3", new Color(215, 175, 255));
        registerColor("Yellow5", new Color(215, 215, 0));
        registerColor("Khaki", new Color(215, 215, 95));
        registerColor("LightGoldenrod2", new Color(215, 215, 135));
        registerColor("LightYellow", new Color(215, 215, 175));
        registerColor("Grey84", new Color(215, 215, 215));
        registerColor("LightSteelBlue3", new Color(215, 215, 255));
        registerColor("Yellow6", new Color(215, 255, 0));
        registerColor("DarkOliveGreen5", new Color(215, 255, 95));
        registerColor("DarkOliveGreen6", new Color(215, 255, 135));
        registerColor("DarkSeaGreen9", new Color(215, 255, 175));
        registerColor("Honeydew", new Color(215, 255, 215));
        registerColor("LightCyan2", new Color(215, 255, 255));
        registerColor("Red4", new Color(255, 0, 0));
        registerColor("DeepPink6", new Color(255, 0, 95));
        registerColor("DeepPink7", new Color(255, 0, 135));
        registerColor("DeepPink8", new Color(255, 0, 175));
        registerColor("Magenta5", new Color(255, 0, 215));
        registerColor("Magenta6", new Color(255, 0, 255));
        registerColor("OrangeRed", new Color(255, 95, 0));
        registerColor("IndianRed3", new Color(255, 95, 95));
        registerColor("IndianRed4", new Color(255, 95, 135));
        registerColor("HotPink4", new Color(255, 95, 175));
        registerColor("HotPink5", new Color(255, 95, 215));
        registerColor("MediumOrchid4", new Color(255, 95, 255));
        registerColor("DarkOrange3", new Color(255, 135, 0));
        registerColor("Salmon", new Color(255, 135, 95));
        registerColor("LightCoral", new Color(255, 135, 135));
        registerColor("PaleVioletRed", new Color(255, 135, 175));
        registerColor("Orchid2", new Color(255, 135, 215));
        registerColor("Orchid3", new Color(255, 135, 255));
        registerColor("Orange4", new Color(255, 175, 0));
        registerColor("SandyBrown", new Color(255, 175, 95));
        registerColor("LightSalmon3", new Color(255, 175, 135));
        registerColor("LightPink3", new Color(255, 175, 175));
        registerColor("Pink2", new Color(255, 175, 215));
        registerColor("Plum4", new Color(255, 175, 255));
        registerColor("Gold3", new Color(255, 215, 0));
        registerColor("LightGoldenrod3", new Color(255, 215, 95));
        registerColor("LightGoldenrod4", new Color(255, 215, 135));
        registerColor("NavajoWhite2", new Color(255, 215, 175));
        registerColor("MistyRose2", new Color(255, 215, 215));
        registerColor("Thistle2", new Color(255, 215, 255));
        registerColor("Yellow7", new Color(255, 255, 0));
        registerColor("LightGoldenrod5", new Color(255, 255, 95));
        registerColor("Khaki2", new Color(255, 255, 135));
        registerColor("Wheat2", new Color(255, 255, 175));
        registerColor("Cornsilk", new Color(255, 255, 215));
        registerColor("Grey100", new Color(255, 255, 255));
        registerColor("Grey3", new Color(8, 8, 8));
        registerColor("Grey7", new Color(18, 18, 18));
        registerColor("Grey11", new Color(28, 28, 28));
        registerColor("Grey15", new Color(38, 38, 38));
        registerColor("Grey19", new Color(48, 48, 48));
        registerColor("Grey23", new Color(58, 58, 58));
        registerColor("Grey27", new Color(68, 68, 68));
        registerColor("Grey30", new Color(78, 78, 78));
        registerColor("Grey35", new Color(88, 88, 88));
        registerColor("Grey39", new Color(98, 98, 98));
        registerColor("Grey42", new Color(108, 108, 108));
        registerColor("Grey46", new Color(118, 118, 118));
        registerColor("Grey50", new Color(128, 128, 128));
        registerColor("Grey54", new Color(138, 138, 138));
        registerColor("Grey58", new Color(148, 148, 148));
        registerColor("Grey62", new Color(158, 158, 158));
        registerColor("Grey66", new Color(168, 168, 168));
        registerColor("Grey70", new Color(178, 178, 178));
        registerColor("Grey74", new Color(188, 188, 188));
        registerColor("Grey78", new Color(198, 198, 198));
        registerColor("Grey82", new Color(208, 208, 208));
        registerColor("Grey85", new Color(218, 218, 218));
        registerColor("Grey89", new Color(228, 228, 228));
        registerColor("Grey93", new Color(238, 238, 238));
    }

    private boolean parsedChildren;

    public static NDocObjEx ofProp(HNode n, String name) {
        if (n == null) {
            return of(null);
        }
        return of(n.getPropertyValue(name).orNull());
    }

    public static NDocObjEx of(Object element) {
        if (element instanceof NDocObjEx) {
            return (NDocObjEx) element;
        }
        if (element instanceof NOptional) {
            return of(((NOptional) element).orNull());
        }
        return new NDocObjEx(element);
    }

    public NDocObjEx(Object element) {
        if (element instanceof NListContainerElement) {
            element = ((NListContainerElement) element);
        }
        this.element = element;
    }

    private void _parsedChildren() {
        if (!parsedChildren) {
            parsedChildren = true;
            if (element instanceof NListContainerElement) {
                switch (((NListContainerElement) element).type()) {
                    case OBJECT:
                    case NAMED_PARAMETRIZED_OBJECT:
                    case PARAMETRIZED_OBJECT:
                    case NAMED_OBJECT:

                    case ARRAY:
                    case NAMED_PARAMETRIZED_ARRAY:
                    case PARAMETRIZED_ARRAY:
                    case NAMED_ARRAY:{
                        NListContainerElement te = (NListContainerElement) element;
                        name = net.thevpc.ndoc.api.util.HUtils.uid(te.toNamed().flatMap(NNamedElement::name).orNull());
                        List<NElement> a = te.children();
                        if (a != null) {
                            children.addAll(a);
                        }
                        break;
                    }

                    case UPLET:
                    case NAMED_UPLET: {
                        NListContainerElement te = (NListContainerElement) element;
                        name = net.thevpc.ndoc.api.util.HUtils.uid(te.toNamed().flatMap(NNamedElement::name).orNull());
                        List<NElement> a = te.children();
                        if (a != null) {
                            args.addAll(a);
                        }
                        break;
                    }
                }
            }

        }
    }

    public NOptional<SimplePair> asSimplePair() {
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            switch (te.type()) {
                case PAIR: {
                    NPairElement pair = te.asPair().get();
                    NElement key = pair.key();
                    NOptional<String> s = NDocObjEx.of(key).asStringOrName();
                    if (s.isPresent()) {
                        return NOptional.of(
                                new SimplePair(
                                        s.get(),
                                        key,
                                        NDocObjEx.of(pair.value())
                                )
                        );
                    }
                    break;
                }
            }
        }
        return NOptional.ofNamedEmpty("pair");
    }

    public String name() {
        _parsedChildren();
        return name;
    }

    public List<NElement> argsOrBody() {
        List<NElement> a = new ArrayList<>();
        a.addAll(args());
        a.addAll(body());
        return a;
    }

    public Map<String, NDocObjEx> argsOrBodyMap() {
        Map<String, NDocObjEx> a = new HashMap<>();
        for (NElement arg : args()) {
            NOptional<SimplePair> sp = NDocObjEx.of(arg).asSimplePair();
            if (sp.isPresent()) {
                a.put(net.thevpc.ndoc.api.util.HUtils.uid(sp.get().name), sp.get().value);
            }
        }
        for (NElement arg : body()) {
            NOptional<SimplePair> sp = NDocObjEx.of(arg).asSimplePair();
            if (sp.isPresent()) {
                a.put(net.thevpc.ndoc.api.util.HUtils.uid(sp.get().name), sp.get().value);
            }
        }
        return a;
    }

    public Map<String, NDocObjEx> argsMap() {
        Map<String, NDocObjEx> a = new HashMap<>();
        for (NElement arg : args()) {
            NOptional<SimplePair> sp = NDocObjEx.of(arg).asSimplePair();
            if (sp.isPresent()) {
                a.put(net.thevpc.ndoc.api.util.HUtils.uid(sp.get().name), sp.get().value);
            }
        }
        return a;
    }

    public List<NElement> args() {
        _parsedChildren();
        return args;
    }

    public List<NElement> body() {
        _parsedChildren();
        return children;
    }

    public NOptional<Paint> asPaint() {
        //TODO fix me later
        return asColor().map(x -> x);
    }

    public NOptional<Color> asColor() {
        if (element instanceof Color) {
            return NOptional.of((Color) element);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            switch (te.type()) {
                case BYTE:
                case BIG_INTEGER:
                case SHORT:
                case LONG:
                case INTEGER: {
                    return NDocObjEx.of(te.asIntValue().get()).asColor();
                }
                case UPLET:
                case NAMED_UPLET: {
                    NOptional<int[]> ri = asIntArray();
                    if (ri.isPresent()) {
                        int[] ints = ri.get();
                        if (ints.length == 3 || ints.length == 4) {
                            return NOptional.of(
                                    new Color(
                                            ints[0],
                                            ints[1],
                                            ints[2],
                                            ints.length == 3 ? 255 : ints[3]
                                    )
                            );
                        }
                    }
                    NOptional<float[]> rd = asFloatArray();
                    if (rd.isPresent()) {
                        float[] ints = rd.get();
                        if (ints.length == 3 || ints.length == 4) {
                            return NOptional.of(
                                    new Color(
                                            ints[0],
                                            ints[1],
                                            ints[2],
                                            ints.length == 3 ? 1.0f : ints[3]
                                    )
                            );
                        }
                    }
                    break;
                }
                case DOUBLE_QUOTED_STRING:
                case SINGLE_QUOTED_STRING:
                case ANTI_QUOTED_STRING:
                case TRIPLE_DOUBLE_QUOTED_STRING:
                case TRIPLE_SINGLE_QUOTED_STRING:
                case TRIPLE_ANTI_QUOTED_STRING:
                case LINE_STRING:
                case NAME: {
                    NDocObjEx h = NDocObjEx.of(element);
                    String s = h.asStringOrName().get();
                    return NDocObjEx.of(s).asColor();
                }
            }
        } else {
            if (element instanceof Integer
                    || element instanceof Short
                    || element instanceof Byte
                    || element instanceof Long
                    || element instanceof BigInteger) {
                return NOptional.of(new Color(((Number) element).intValue()));
            }
            if (element instanceof String) {
                String s = (String) element;
                if (s.startsWith("#")) {
                    try {
                        Color value = new Color(Integer.parseInt(s.substring(1), 16));
                        return NOptional.of(value);
                    } catch (Exception ex) {
                        return NOptional.ofNamedError(NMsg.ofC("invalid color %s", s));
                    }
                }
                if (s.indexOf(",") >= 0) {
                    String[] a = s.split(",");
                    if (a.length == 3 || a.length == 4) {
                        NDocObjEx r = NDocObjEx.of(a[0]);
                        NDocObjEx g = NDocObjEx.of(a[1]);
                        NDocObjEx b = NDocObjEx.of(a[2]);
                        NDocObjEx aa = NDocObjEx.of(a.length == 4 ? a[3] : null);
                        if (r.asInt().isPresent() && g.asInt().isPresent() && b.asInt().isPresent()) {
                            return NOptional.of(
                                    new Color(
                                            r.asInt().get(),
                                            g.asInt().get(),
                                            b.asInt().get(),
                                            aa.asInt().orElse(255)
                                    )
                            );
                        }
                        if (r.asDouble().isPresent() && g.asDouble().isPresent() && b.asDouble().isPresent()) {
                            return NOptional.of(
                                    new Color(
                                            (r.asFloat().get()),
                                            (g.asFloat().get()),
                                            (b.asFloat().get()),
                                            aa.asFloat().orElse(1.0f)
                                    )
                            );
                        }
                    }
                }
                if (s.toLowerCase().startsWith("c")) {
                    NOptional<Integer> u = NLiteral.of(s.substring(1).trim()).asInt();
                    if (u.isPresent()) {
                        return NOptional.of(DefaultColorPalette.INSTANCE.getColor(u.get()));
                    }
                }
                NOptional<Color> color = getRegisteredColor(s);
                if (color.isPresent()) {
                    return color;
                }
                switch (NNameFormat.LOWER_KEBAB_CASE.format(s.toLowerCase())) {
                    case "red": {
                        return NOptional.of(Color.RED);
                    }
                    case "blue": {
                        return NOptional.of(Color.BLUE);
                    }
                    case "black": {
                        return NOptional.of(Color.BLACK);
                    }
                    case "white": {
                        return NOptional.of(Color.WHITE);
                    }
                    case "yellow": {
                        return NOptional.of(Color.YELLOW);
                    }
                    case "cyan": {
                        return NOptional.of(Color.CYAN);
                    }
                    case "orange": {
                        return NOptional.of(Color.ORANGE);
                    }
                    case "pink": {
                        return NOptional.of(Color.PINK);
                    }
                    case "dark-gray": {
                        return NOptional.of(Color.DARK_GRAY);
                    }
                    case "light-gray": {
                        return NOptional.of(Color.LIGHT_GRAY);
                    }
                    case "gray": {
                        return NOptional.of(Color.GRAY);
                    }
                    case "green": {
                        return NOptional.of(Color.GREEN);
                    }
                    case "magenta": {
                        return NOptional.of(Color.MAGENTA);
                    }
                }
                try {
                    int z = Integer.parseInt(s, 16);
                    return NOptional.of(new Color(z));
                } catch (Exception e) {

                }
                try {
                    int z = Integer.parseInt(s);
                    return NOptional.of(new Color(z));
                } catch (Exception e) {
                    //
                }
            }
        }
        return NOptional.ofNamedEmpty("color from " + element);
    }

    public NOptional<Float> asFloat() {
        return asDouble().map(Double::floatValue);
    }

    public NOptional<Number> asNumber() {
        if (element instanceof Number) {
            return NOptional.of(((Number) element));
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if (te.type().isNumber()) {
                return NOptional.of(te.asNumberValue().get());
            }
        }
        return NOptional.ofNamedEmpty("number from " + element);
    }

    public NOptional<Double> asDouble() {
        if (element instanceof Double
                || element instanceof Float
                || element instanceof BigDecimal) {
            return NOptional.of(((Number) element).doubleValue());
        }
        if (element instanceof String) {
            return NLiteral.of(element).asDouble();
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if (te.type().isNumber()) {
                switch (te.type()) {
                    case BYTE:
                    case INTEGER:
                    case SHORT:
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                    case BIG_DECIMAL: {
                        return NOptional.of(te.asDoubleValue().get());
                    }
                }
                return NOptional.of(te.asDoubleValue().get());
            } else if (te.type().isString()) {
                return NDocObjEx.of(te.asStringValue().get()).asDouble();
            } else {
                return NOptional.ofNamedEmpty("double from " + element);
            }
        }
        return NOptional.ofNamedEmpty("double from " + element);
    }

    public NOptional<Integer> asIntOrBoolean() {
        return asInt().orElseUse(() -> asBoolean().map(x -> x ? 1 : 0));
    }

    public NOptional<NElement> asTsonInt() {
        return asInt().map(x -> NElements.of().ofInt(x));
    }

    public NOptional<Integer> asInt() {
        if (element instanceof Integer
                || element instanceof Long
                || element instanceof Byte
                || element instanceof Short
                || element instanceof BigInteger) {
            return NOptional.of(((Number) element).intValue());
        }
        if (element instanceof String) {
            return NLiteral.of(element).asInt();
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if (te.type().isNumber()) {
                switch (te.type()) {
                    case BYTE:
                    case SHORT:
                    case INTEGER:
                    case LONG: {
                        return NOptional.of(te.asIntValue().get());
                    }
                }
            } else if (te.type().isString()) {
                return NDocObjEx.of(te.asStringValue().get()).asInt();
            } else {
                return NOptional.ofNamedEmpty("double from " + element);
            }
        }
        return NOptional.ofNamedEmpty("double from " + element);
    }

    public NOptional<Boolean> asBoolean() {
        if (element instanceof Boolean) {
            return NOptional.of(((Boolean) element));
        }
        if (element instanceof String) {
            return NLiteral.of(element).asBoolean();
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            switch (te.type()) {
                case BOOLEAN: {
                    return NOptional.of(te.asBooleanValue().get());
                }
                case DOUBLE_QUOTED_STRING:
                case SINGLE_QUOTED_STRING:
                case ANTI_QUOTED_STRING:
                case TRIPLE_DOUBLE_QUOTED_STRING:
                case TRIPLE_SINGLE_QUOTED_STRING:
                case TRIPLE_ANTI_QUOTED_STRING:
                case LINE_STRING: {
                    return NLiteral.of(te.asStringValue()).asBoolean();
                }
                case NAME: {
                    return NLiteral.of(te.asStringValue().get()).asBoolean();
                }
            }
        }
        return NOptional.ofNamedEmpty("boolean from " + element);
    }

    public NOptional<NElement> asTsonStringOrName() {
        if (element instanceof String) {
            return NOptional.of(NElements.of().ofString((String) element));
        }
        if (element instanceof NElement) {
            if (((NElement) element).isAnyString()) {
                return NOptional.of((NElement) element);
            }
        }
        return NOptional.ofNamedEmpty("string from " + element);
    }

    public NOptional<String> asStringOrName() {
        if (element instanceof String) {
            return NOptional.of((String) element);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if(te.isAnyString()){
                return NOptional.of(te.asStringValue().get());
            }
        }
        return NOptional.ofNamedEmpty("string from " + element);
    }

    public NOptional<int[]> asIntArray() {
        if (element instanceof int[]) {
            return NOptional.of((int[]) element);
        }
        if (element instanceof NElement[]) {
            NElement[] arr = (NElement[]) element;
            int[] aa = new int[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Integer> d = NDocObjEx.of(arr[i]).asInt();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("int[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if(te.isListContainer()){
                return NDocObjEx.of(te.toListContainer().get().children().toArray(new NElement[0])).asIntArray();
            }
        }
        if (element instanceof Int2) {
            return NOptional.of(new int[]{((Int2) element).getY(), ((Int2) element).getY()});
        }
        return NOptional.ofNamedEmpty("int[] from " + element);
    }

    public NOptional<double[]> asDoubleArrayOrDouble() {
        NOptional<double[]> a = asDoubleArray();
        if (a.isPresent()) {
            return a;
        }
        NOptional<Double> b = asDouble();
        if (b.isPresent()) {
            return NOptional.of(new double[]{b.get()});
        }
        return a;
    }

    public NOptional<float[]> asFloatArrayOrFloat() {
        NOptional<float[]> a = asFloatArray();
        if (a.isPresent()) {
            return a;
        }
        NOptional<Float> b = asFloat();
        if (b.isPresent()) {
            return NOptional.of(new float[]{b.get()});
        }
        return a;
    }

    public NOptional<float[]> asFloatArray() {
        return asDoubleArray().map(x -> {
            float[] ff = new float[x.length];
            for (int i = 0; i < x.length; i++) {
                ff[i] = (float) x[i];
            }
            return ff;
        });
    }

    public NOptional<NElement[]> asNArrayElement() {
        if (element instanceof NElement[]) {
            return NOptional.of(((NElement[]) element));
        }
        if (element instanceof double[]) {
            return NOptional.of(NElements.of().ofDoubleArray((double[]) element).children().toArray(new NElement[0]));
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            switch (te.type()) {
                case ARRAY:
                case NAMED_PARAMETRIZED_ARRAY:
                case PARAMETRIZED_ARRAY:
                case NAMED_ARRAY: {
                    NArrayElement a = te.asArray().get();
                    if (a.isNamed() || a.isParametrized()) {
                        return NOptional.of(new NElement[]{te});
                    }
                    return NOptional.of(te.asArray().get().children().toArray(new NElement[0]));
                }
                case OBJECT:
                case NAMED_PARAMETRIZED_OBJECT:
                case NAMED_OBJECT:
                case PARAMETRIZED_OBJECT: {
                    NObjectElement a = te.asObject().get();
                    if (a.isNamed() || a.isParametrized()) {
                        return NOptional.of(new NElement[]{te});
                    }
                    return NOptional.of(te.asObject().get().children().toArray(new NElement[0]));
                }
                case UPLET:
                case NAMED_UPLET: {
                    return NOptional.of(te.asUplet().get().children().toArray(new NElement[0]));
                }
            }
        }
        if (element instanceof Double2) {
            return NDocObjEx.of(new double[]{((Double2) element).getX(), ((Double2) element).getY()}).asNArrayElement();
        }
        if (element instanceof HPoint2D) {
            return NDocObjEx.of(new double[]{((HPoint2D) element).getX(), ((HPoint2D) element).getY()}).asNArrayElement();
        }
        if (element instanceof HPoint3D) {
            return NDocObjEx.of(new double[]{((HPoint3D) element).getX(), ((HPoint3D) element).getY(), ((HPoint3D) element).getZ()}).asNArrayElement();
        }
        if (element instanceof Double4) {
            return NDocObjEx.of(new double[]{
                    ((Double4) element).getX1(),
                    ((Double4) element).getX2()
                    , ((Double4) element).getX3()
                    , ((Double4) element).getX4()
            }).asNArrayElement();
        }
        return NOptional.ofNamedEmpty("NElement[] from " + element);
    }

    public NOptional<double[]> asDoubleArray() {
        if (element instanceof double[]) {
            return NOptional.of((double[]) element);
        }
        if (element instanceof Object[]) {
            Object[] arr = (Object[]) element;
            double[] aa = new double[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Double> d = NDocObjEx.of(arr[i]).asDouble();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("double[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if(te.isListContainer()){
                return NDocObjEx.of(te.asListContainer().get().children().toArray(new NElement[0])).asDoubleArray();
            }
        }
        if (element instanceof Double2) {
            return NOptional.of(new double[]{((Double2) element).getX(), ((Double2) element).getY()});
        }
        if (element instanceof HPoint2D) {
            return NOptional.of(new double[]{((HPoint2D) element).getY(), ((HPoint2D) element).getY()});
        }
        if (element instanceof HPoint3D) {
            return NOptional.of(new double[]{((HPoint3D) element).getY(), ((HPoint3D) element).getY(), ((HPoint3D) element).getZ()});
        }
        if (element instanceof Double4) {
            Double4 d = (Double4) element;
            return NOptional.of(new double[]{
                    d.getX1(),
                    d.getX2(),
                    d.getX3(),
                    d.getX4(),});
        }
        if (element instanceof Collection) {
            Object[] arr = ((Collection)element).toArray();
            double[] aa = new double[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Double> d = NDocObjEx.of(arr[i]).asDouble();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("double[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        return NOptional.ofNamedEmpty("double[] from " + element);
    }

    public NOptional<String[]> asStringArrayOrString() {
        NOptional<String[]> a = asStringArray();
        if (a.isPresent()) {
            return a;
        }
        NOptional<String> b = asStringOrName();
        if (b.isPresent()) {
            return NOptional.of(new String[]{b.get()});
        }
        return a;
    }

    public NOptional<Object[]> asObjectArray() {
        if (element instanceof Object[]) {
            return NOptional.of((Object[]) element);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if(te.isListContainer()){
                return NOptional.of(te.asListContainer().get().children().toArray(new NElement[0]));
            }
        }
        return NOptional.ofNamedEmpty("Object[] from " + element);
    }

    public NOptional<Color[]> asColorArrayOrColor() {
        NOptional<Color[]> a = asColorArray();
        if (a.isPresent()) {
            return a;
        }
        NOptional<Color> b = asColor();
        if (b.isPresent()) {
            return NOptional.of(new Color[]{b.get()});
        }
        return a;
    }

    public NOptional<Color[]> asColorArray() {
        NOptional<Object[]> o = asObjectArray();
        if (o.isPresent()) {
            List<Color> cc = new ArrayList<>();
            for (Object oi : o.get()) {
                NOptional<Color> y = NDocObjEx.of(oi).asColor();
                if (y.isPresent()) {
                    cc.add(y.get());
                } else {
                    return NOptional.ofNamedEmpty("Object[] from " + element);
                }
            }
            return NOptional.of(cc.toArray(new Color[0]));
        }
        return NOptional.ofNamedEmpty("Object[] from " + element);
    }

    public NOptional<String[]> asStringArray() {
        if (element instanceof String[]) {
            return NOptional.of((String[]) element);
        }
        if (element instanceof NElement[]) {
            NElement[] arr = (NElement[]) element;
            String[] aa = new String[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<String> d = NDocObjEx.of(arr[i]).asStringOrName();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("double[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if(te.isListContainer()){
                return NOptional.of(te.asListContainer().get().children().stream().map(x->x.asStringValue().get()).toArray(String[]::new));
            }
        }
        return NOptional.ofNamedEmpty("String[] from " + element);
    }

    public NOptional<boolean[]> asBooleanArray() {
        if (element instanceof boolean[]) {
            return NOptional.of((boolean[]) element);
        }
        if (element instanceof NElement[]) {
            NElement[] arr = (NElement[]) element;
            boolean[] aa = new boolean[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Boolean> d = NDocObjEx.of(arr[i]).asBoolean();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("double[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if(te.isListContainer()){
                return NOptional.of(NStream.ofStream(te.asListContainer().get().children().stream()).map(x->x.asBooleanValue().get()).toBooleanArray());
            }
        }
        return NOptional.ofNamedEmpty("boolean[] from " + element);
    }

    public NOptional<Int2> asInt2() {
        if (element instanceof Int2) {
            return NOptional.of((Int2) element);
        }
        NOptional<int[]> a = asIntArray();
        if (a.isPresent()) {
            int[] g = a.get();
            if (g.length == 2) {
                return NOptional.of(new Int2(g[0], g[1]));
            }
        }
        return NOptional.ofNamedEmpty("int2 from " + element);
    }

    public NOptional<Double2> asDouble2OrHAlign() {
        NOptional<Double2> p = asDouble2();
        if (p.isPresent()) {
            return p;
        }
        if (element instanceof HAlign) {
            return ((HAlign) element).toPosition();
        }
        NOptional<String> k = asStringOrName();
        if (k.isPresent()) {
            return HAlign.parse(k.get()).flatMap(x -> x.toPosition());
        }
        return NOptional.ofNamedEmpty("Double from " + element);
    }

    public NOptional<ElemNumber2> asNNumberElement2Or1OrHAlign() {
        NOptional<NElement[]> ta = asNArrayElement();
        if (ta.isPresent()) {
            NElement[] taa = ta.get();
            switch (taa.length) {
                case 1: {
                    if (taa[0].isNumber()) {
                        return NOptional.of(new ElemNumber2((NNumberElement) taa[0], (NNumberElement) taa[0]));
                    } else if (taa[0].isAnyString()) {
                        Double2 size = HAlign.parse(NDocObjEx.of(taa[0]).asStringOrName().get()).flatMap(x -> x.toPosition()).get();
                        return NOptional.of(
                                new ElemNumber2(
                                        NElements.of().ofDouble(size.getX()).asNumber().get(),
                                        NElements.of().ofDouble(size.getY()).asNumber().get()
                                )
                        );
                    }
                    break;
                }
                case 2: {
                    NNumberElement xx;
                    NNumberElement yy;
                    if (taa[0].isNumber()) {
                        xx = taa[0].asNumber().get();
                    } else if (taa[0].isAnyString()) {
                        xx = NElements.of().ofDouble(HAlign.parse(NDocObjEx.of(taa[0]).asStringOrName().get()).flatMap(HAlign::toPosition).get().getX()).asNumber().get();
                    } else {
                        return NOptional.ofNamedError(NMsg.ofC("not a number %s in %s", taa[0], element));
                    }
                    if (taa[1].isNumber()) {
                        yy = taa[1].asNumber().get();
                    } else if (taa[1].isAnyString()) {
                        yy = NElements.of().ofDouble(HAlign.parse(NDocObjEx.of(taa[1]).asStringOrName().get()).flatMap(x -> x.toPosition()).get().getY()).asNumber().get();
                    } else {
                        return NOptional.ofNamedError(NMsg.ofC("not a number %s in %s", taa[1], element));
                    }
                    return NOptional.of(new ElemNumber2(xx, yy));
                }
            }
        }
        NOptional<NElement> te = asTson();
        if (te.isPresent()) {
            NElement taa = te.get();
            if (taa.isNumber()) {
                return NOptional.of(new ElemNumber2((NNumberElement) taa, (NNumberElement) taa));
            } else if (taa.isAnyString()) {
                Double2 size = HAlign.parse(NDocObjEx.of(taa).asStringOrName().get()).flatMap(x -> x.toPosition()).get();
                return NOptional.of(
                        new ElemNumber2(
                                NElements.of().ofDouble(size.getX()).asNumber().get(),
                                NElements.of().ofDouble(size.getY()).asNumber().get()
                        )
                );
            }
        }
        return NOptional.ofNamedEmpty("NNumberElement2Or1OrHAlign from " + element);
    }


    public NOptional<Padding> asPadding() {
        if (element instanceof Padding) {
            return NOptional.of((Padding) element);
        }
        NOptional<double[]> d = asDoubleArrayOrDouble();
        if (d.isPresent()) {
            double[] dd = d.get();
            switch (dd.length) {
                case 1: {
                    return NOptional.of(Padding.of(dd[0]));
                }
                case 2: {
                    return NOptional.of(Padding.of(dd[0], dd[1]));
                }
                case 3: {
                    return NOptional.of(Padding.of(dd[0], dd[1], dd[2]));
                }
                case 4: {
                    return NOptional.of(Padding.of(dd[0], dd[1], dd[2], dd[3]));
                }
            }
        }
        return NOptional.ofNamedEmpty("Padding from " + element);
    }

    public NOptional<Rotation> asRotation() {
        if (element instanceof Rotation) {
            return NOptional.of((Rotation) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            switch (dd.length) {
                case 1: {
                    return NOptional.of(new Rotation(
                            NElements.of().ofDouble(dd[0]),
                            NElements.of().ofDouble(50),
                            NElements.of().ofDouble(50)
                    ));
                }
                case 3: {
                    return NOptional.of(new Rotation(
                            NElements.of().ofDouble(dd[0]),
                            NElements.of().ofDouble(50),
                            NElements.of().ofDouble(50)
                    ));
                }
            }
        }
        NOptional<NElement> dd = asTson();
        if (dd.isPresent()) {
            return NOptional.of(new Rotation(
                    dd.get(),
                    NElements.of().ofDouble(50),
                    NElements.of().ofDouble(50)
            ));
        }
        return NOptional.ofNamedEmpty("Rotation from " + element);
    }

    public NOptional<Double4> asDouble4() {
        if (element instanceof Double4) {
            return NOptional.of((Double4) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 4) {
                return NOptional.of(new Double4(dd[0], dd[1], dd[2], dd[3]));
            }
        }
        return NOptional.ofNamedEmpty("Double4 from " + element);
    }

    public NOptional<Double2> asDouble2() {
        if (element instanceof Double2) {
            return NOptional.of((Double2) element);
        }
        if (element instanceof HPoint2D) {
            HPoint2D p = (HPoint2D) element;
            return NOptional.of(new Double2(p.x, p.y));
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 2) {
                return NOptional.of(new Double2(dd[0], dd[1]));
            }
        }
        return NOptional.ofNamedEmpty("Double2 from " + element);
    }

    public NOptional<Double3> asDouble3() {
        if (element instanceof Double3) {
            return NOptional.of((Double3) element);
        }
        if (element instanceof HPoint3D) {
            HPoint3D p = (HPoint3D) element;
            return NOptional.of(new Double3(p.x, p.y, p.z));
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 3) {
                return NOptional.of(new Double3(dd[0], dd[1], dd[2]));
            }
        }
        return NOptional.ofNamedEmpty("Double3 from " + element);
    }

    public NOptional<HPoint2D> asHPoint2DOrDouble() {
        if (element instanceof HPoint2D) {
            return NOptional.of((HPoint2D) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 2) {
                return NOptional.of(new HPoint2D(dd[0], dd[1]));
            }
        }
        NOptional<Double> dd = asDouble();
        if (dd.isPresent()) {
            return NOptional.of(new HPoint2D(dd.get(), dd.get()));
        }
        return NOptional.ofNamedEmpty("HPoint2D from " + element);
    }

    public NOptional<HPoint2D> asHPoint2D() {
        if (element instanceof HPoint2D) {
            return NOptional.of((HPoint2D) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 2) {
                return NOptional.of(new HPoint2D(dd[0], dd[1]));
            }
        }
        return NOptional.ofNamedEmpty("HPoint2D from " + element);
    }

    public NOptional<HArrowType> asArrowType() {
        if (element instanceof HArrowType) {
            return NOptional.of((HArrowType) element);
        }
        NOptional<String> s = asStringOrName();
        if (s.isPresent()) {
            String v = s.get().trim();
            if (v.isEmpty()) {
                return NOptional.ofNamedEmpty("arrow-type");
            }
            try {
                HArrowType y = HArrowType.valueOf(NNameFormat.CONST_NAME.format(v));
                return NOptional.of(y);
            } catch (Exception e) {
                //
            }
        }
        return NOptional.ofNamedError("HArrowType from " + element);
    }

    public NOptional<HArrow> asArrow() {
        if (element instanceof HArrow) {
            return NOptional.of((HArrow) element);
        }
        if (element instanceof HArrowType) {
            return NOptional.of(new HArrow((HArrowType) element));
        }
        if (element instanceof NUpletElement && ((NUpletElement) element).isNamed()) {
            NUpletElement f = (NUpletElement) element;
            NOptional<HArrowType> u = NDocObjEx.of(f.name().orNull()).asArrowType();
            Double width = null;
            Double height = null;
            if (u.isPresent()) {
                for (NElement arg : f.params()) {
                    NOptional<Number> n = NDocObjEx.of(arg).asNumber();
                    if (n.isPresent()) {
                        if (width == null) {
                            width = n.get().doubleValue();
                        } else if (height == null) {
                            height = n.get().doubleValue();
                        }
                    }
                }
                return NOptional.of(
                        new HArrow(
                                u.get(),
                                width == null ? 0 : width.doubleValue(),
                                height == null ? 0 : height.doubleValue()
                        )
                );
            }
            return NOptional.ofNamedError("HArrow from " + element);
        }
        NOptional<HArrowType> s = asArrowType();
        if (s.isPresent()) {
            return NOptional.of(new HArrow(s.get()));
        }
        return NOptional.ofNamedError("HArrow from " + element);
    }

    public NOptional<HPoint3D> asHPoint3D() {
        if (element instanceof HPoint3D) {
            return NOptional.of((HPoint3D) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 2) {
                return NOptional.of(new HPoint3D(dd[0], dd[1], dd[2]));
            }
        }
        return NOptional.ofNamedEmpty("HPoint3D from " + element);
    }

    public <T> NOptional<T> as(Class<T> type) {
        switch (type.getName()) {
            case "java.lang.String": {
                return (NOptional<T>) asStringOrName();
            }
            case "boolean": {
                return (NOptional<T>) asBoolean();
            }
            case "double": {
                return (NOptional<T>) asDouble();
            }
            case "int": {
                return (NOptional<T>) asInt();
            }
            case "net.thevpc.halfa.api.model.elem2d.Double2": {
                return (NOptional<T>) asDouble2();
            }
            case "[Lnet.thevpc.halfa.api.model.Double2;": {
                return (NOptional<T>) asDouble2Array();
            }
            case "[Ljava.lang.String;": {
                return (NOptional<T>) asStringArray();
            }
            case "[d": {
                return (NOptional<T>) asDoubleArray();
            }
            default: {
                throw new IllegalArgumentException("unsupported type " + type);
            }
        }
    }

    public NOptional<HPoint2D[]> asHPoint2DArray() {
        NOptional<Double2[]> u = asDouble2Array();
        if (u.isPresent()) {
            return NOptional.of(
                    Arrays.stream(u.get()).map(x -> new HPoint2D(x.getX(), x.getY())).toArray(HPoint2D[]::new)
            );
        } else {
            return (NOptional) u;
        }
    }

    public NOptional<HPoint3D[]> asHPoint3DArray() {
        NOptional<Double3[]> u = asDouble3Array();
        if (u.isPresent()) {
            return NOptional.of(
                    Arrays.stream(u.get()).map(x -> new HPoint3D(x.getX(), x.getY(), x.getZ())).toArray(HPoint3D[]::new)
            );
        } else {
            return (NOptional) u;
        }
    }

    public NOptional<Double2[]> asDouble2Array() {
        if (element instanceof Double2[]) {
            return NOptional.of((Double2[]) element);
        }
        if (element instanceof HPoint2D[]) {
            return NOptional.of(
                    Arrays.stream((HPoint2D[]) element).map(x -> new Double2(x.getX(), x.getY())).toArray(Double2[]::new)
            );
        }
        if (element instanceof NElement[]) {
            NElement[] arr = (NElement[]) element;
            Double2[] aa = new Double2[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Double2> d = NDocObjEx.of(arr[i]).asDouble2();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("Double2[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if(te.isListContainer()){
                return NDocObjEx.of(te.toArray().get().children().toArray(new NElement[0])).asDouble2Array();
            }
        }
        return NOptional.ofNamedEmpty("Double2[] from " + element);
    }

    public NOptional<Double3[]> asDouble3Array() {
        if (element instanceof Double3[]) {
            return NOptional.of((Double3[]) element);
        }
        if (element instanceof NElement[]) {
            NElement[] arr = (NElement[]) element;
            Double3[] aa = new Double3[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Double3> d = NDocObjEx.of(arr[i]).asDouble3();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("Double3[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            if (te.isListContainer()) {
                return NDocObjEx.of(te.toArray().get().children().toArray(new NElement[0])).asDouble3Array();
            }
        }
        return NOptional.ofNamedEmpty("Double3[] from " + element);
    }

    public boolean isFunction() {
        return element instanceof NUpletElement && ((NUpletElement) element).type()==NElementType.NAMED_UPLET;
    }

    public boolean hasName() {
        return !NBlankable.isBlank(name());
    }

    public boolean isBoolean() {
        if (element instanceof Boolean) {
            return true;
        }
        return false;
    }

    public boolean isNumber() {
        if (element instanceof Number) {
            return true;
        }
        if (element instanceof NElement) {
            return ((NElement) element).isNumber();
        }
        return false;
    }

    public NOptional<NElement> asTson() {
        try {
            return NOptional.of(NElemUtils.toElement(element));
        } catch (Exception e) {
            return NOptional.ofEmpty(NMsg.ofC("not a valid element : %s : %s", e, element));
        }
    }

    public NOptional<String> asName() {
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            switch (te.type()) {
                case NAME: {
                    return NOptional.of(te.asStringValue().get());
                }
            }
        }
        return NOptional.ofNamedEmpty("name from " + element);
    }

    public boolean isStringOrName() {
        if (element instanceof String) {
            return true;
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            return te.isAnyString();
        }
        return false;
    }

    public boolean isString() {
        if (element instanceof String) {
            return true;
        }
        if (element instanceof NElement) {
            NElement te = (NElement) element;
            return te.isString();
        }
        return false;
    }

    public static class SimplePair {

        private String name;
        private NElement key;
        private NDocObjEx value;

        public SimplePair(String name, NElement key, NDocObjEx value) {
            this.name = name;
            this.key = key;
            this.value = value;
        }

        public String getNameId() {
            return HUtils.uid(getName());
        }

        public String getName() {
            return name;
        }

        public NElement getKey() {
            return key;
        }

        public NDocObjEx getValue() {
            return value;
        }
    }

    public Object raw() {
        return element;
    }
}
