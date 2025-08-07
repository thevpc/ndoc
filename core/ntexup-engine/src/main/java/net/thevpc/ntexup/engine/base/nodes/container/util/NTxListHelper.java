package net.thevpc.ntexup.engine.base.nodes.container.util;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.DefaultNTxStyleRule;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxSizeRef;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NTxListHelper {
    private static NumberingStrategy resolveNumberingStrategy(NTxNode p, boolean ordered, NTxNodeRendererContext ctx){
        return new NumberingStrategy();
    }
    public static List<NodeWithIndent> build(NTxNode p, boolean ordered, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext buildContext) {
        List<NodeWithIndent> all = new ArrayList<>();
        List<NTxNode> children = p.children();
        NTxDocumentFactory f = ctx.documentFactory();
        NumberingStrategy ns = resolveNumberingStrategy(p, ordered, ctx);
        int userIndex = 1;
        String parentString = "";
        String lastParent = "";
        for (int i = 0; i < children.size(); i++) {
            NTxNode child = children.get(i);
            if (isList(child)) {
                fillAny(child, all, 0, ordered, lastParent, userIndex, f, ctx,ns);
            } else {
                fillAny(child, all, 0, ordered, parentString, userIndex, f, ctx,ns);
                lastParent = ns.eval("", userIndex, false);
                userIndex++;
            }
        }
        NTxSizeRef nTxSizeRef = ctx.sizeRef();
        double childHeight = nTxSizeRef.getParentHeight() / (all.isEmpty() ? 1 : all.size());
        NTxBounds2 bounds = ctx.getBounds();
        double lastY = bounds.getY();
        double indentFactor = Math.min(nTxSizeRef.getParentWidth() / 10, childHeight);
        double bulletWidthFactor=ordered?0.3:0.1;
        for (NodeWithIndent child : all) {
            double indentWidth = indentFactor * child.indent;
            double bulletWidth = (nTxSizeRef.getParentWidth() - indentWidth) * bulletWidthFactor;
            double childWidth = nTxSizeRef.getParentWidth() - indentWidth - bulletWidth;
            child.bulletSelfBounds = ctx.selfBounds(child.bullet
                    , new NTxDouble2(bulletWidth, childHeight)
                    , new NTxDouble2(bulletWidth, childHeight)
            );
            child.childSelfBounds = ctx.engine().renderManager().getRenderer(child.child.type()).get().selfBounds(child.child,
                    ctx.withBounds(child.child, new NTxBounds2(0, 0, child.bulletSelfBounds.getMaxX(), childHeight))
//                    , new NTxDouble2(child.bulletSelfBounds.getMaxX() + 10, childHeight)
//                    , new NTxDouble2(childWidth, childHeight)
            );
            child.height = Math.max(Math.max(child.childSelfBounds.getHeight(), childHeight), child.bulletSelfBounds.getHeight());
            lastY += child.height;
        }
        lastY = bounds.getY();
        for (NodeWithIndent child : all) {
            double indentWidth = indentFactor * child.indent;
            double bulletWidth = (nTxSizeRef.getParentWidth() - indentWidth) * bulletWidthFactor;
            double childWidth = nTxSizeRef.getParentWidth() - indentWidth - bulletWidth;

            child.bulletBounds = new NTxBounds2(bounds.getX() + indentWidth, lastY, bulletWidth, childHeight);
            child.childBounds = new NTxBounds2(child.bulletBounds.getMaxX(), lastY, childWidth, child.height);
            child.rowBounds = child.bulletBounds.expand(child.childBounds);
            lastY += child.height;
        }
        return all;
    }

    private static boolean isList(NTxNode p) {
        switch (p.type()) {
            case NTxNodeType.UNORDERED_LIST:
            case NTxNodeType.ORDERED_LIST: {
                return true;
            }
        }
        return false;
    }

    private static void fillList(List<NTxNode> children, List<NodeWithIndent> all, int indent, boolean ordered, String parentString, NTxDocumentFactory f, NTxNodeRendererContext ctx,NumberingStrategy ns) {
        int userIndex = 1;
        String lastParent = "";
        for (int i = 0; i < children.size(); i++) {
            NTxNode child = children.get(i);
            if (isList(child)) {
                fillAny(child, all, indent, ordered, lastParent, userIndex, f, ctx,ns);
            } else {
                fillAny(child, all, indent, ordered, parentString, userIndex, f, ctx,ns);
                lastParent = ns.eval("", userIndex, false);
                userIndex++;
            }
        }
    }

    private static boolean fillNonList(NTxNode p, List<NodeWithIndent> all, int indent, boolean ordered, String parentString, int index, NTxDocumentFactory f, NTxNodeRendererContext ctx,NumberingStrategy ns) {
        switch (p.type()) {
            case NTxNodeType.UNORDERED_LIST:
            case NTxNodeType.ORDERED_LIST: {
                return false;
            }
            default: {
                NodeWithIndent g = new NodeWithIndent();
                Set<String> allClasses = ctx.engine().computeDeclaredStylesClasses(p);
                String specialStyle = null;
                if (ordered) {
                    if (allClasses.contains("ol-bullet-" + (indent + 1))) {
                        specialStyle = "ol-bullet-" + (indent + 1);
                    } else {
                        if (!allClasses.contains("ol-bullet")) {
                            // add default style!
                            NTxNode r = NTxUtils.findRootNode(p.parent());
                            r.addRule(DefaultNTxStyleRule.ofClass(r, r.source(), "ol-bullet"
                                            , NTxProp.of("origin", NElement.ofString("center"))
                                            , NTxProp.of("position", NElement.ofDoubleArray(50, 50))
                                            , NTxProp.of("size", NElement.ofDouble(8, "%P"))
                                    )
                            );
                        }
                        specialStyle = "ol-bullet";
                    }

                    g.bullet = f.ofText(ns.eval(parentString, index, true))
                            .addStyleClasses(specialStyle)
                            .setSource(p.source());
                } else {
                    if (allClasses.contains("ul-bullet-" + (indent + 1))) {
                        specialStyle = "ul-bullet-" + (indent + 1);
                    } else {
                        if (!allClasses.contains("ul-bullet")) {
                            // add default style!
                            NTxNode r = NTxUtils.findRootNode(p.parent());
                            r.addRule(DefaultNTxStyleRule.ofClass(r, r.source(), "ul-bullet"
                                            , NTxProp.of("background", NElement.ofString("blue"))
                                            , NTxProp.of("origin", NElement.ofString("center"))
                                            , NTxProp.of("position", NElement.ofDoubleArray(50, 50))
                                            , NTxProp.of("size", NElement.ofDouble(8, "%P"))
                                    )
                            );
                        }
                        specialStyle = "ul-bullet";
                    }

                    g.bullet = f.ofSphere()
                            .addStyleClasses(specialStyle)
                            .setSource(p.source());
                }
                g.bullet.setParent(p.parent());
                String clsPrefix = ordered ? "ol" : "ul";
                if (allClasses.contains(clsPrefix + "-item-" + (indent + 1))) {
                    specialStyle = clsPrefix + "-item-" + (indent + 1);
                } else {
                    if (!allClasses.contains(clsPrefix + "-item")) {
                        // add default style!
                        NTxNode r = NTxUtils.findRootNode(p.parent());
                        r.addRule(DefaultNTxStyleRule.ofClass(r, r.source(), clsPrefix + "-bullet"
                                        , NTxProp.of("origin", NElement.ofString("left"))
                                        , NTxProp.of("position", NElement.ofString("left"))
                                        , NTxProp.of("size", NElement.ofDouble(8, "%P"))
                                )
                        );
                    }
                    specialStyle = clsPrefix + "-item";
                }
                g.child = p.addStyleClasses(specialStyle);

                g.indent = indent;
                all.add(g);
                return true;
            }
        }
    }

    private static void fillAny(NTxNode p, List<NodeWithIndent> all, int indent, boolean ordered, String parentString, int index, NTxDocumentFactory f, NTxNodeRendererContext ctx,NumberingStrategy ns) {
        switch (p.type()) {
            case NTxNodeType.UNORDERED_LIST: {
                List<NTxNode> children = p.children();
                fillList(children, all, indent+1, false, parentString, f, ctx,ns);
                break;
            }
            case NTxNodeType.ORDERED_LIST: {
                ns = resolveNumberingStrategy(p, ordered, ctx);
                List<NTxNode> children = p.children();
                fillList(children, all, indent+1, true, parentString, f, ctx,ns);
                break;
            }
            default: {
                fillNonList(p, all, indent, ordered, parentString, index, f, ctx,ns);
                break;
            }
        }
    }

    public static class NumberingStrategy{
        boolean includeParent=false;
        char mode='V';
        public String eval(String parentString,int index,boolean format){
            StringBuilder sb = new StringBuilder();
            if(includeParent){
                if(parentString.isEmpty()) {
                    sb.append(parentString);
                    sb.append(".");
                }
            }
            switch (mode){
                case '1':{
                    sb.append(index);
                    break;
                }
                case 'a':{
                    StringBuilder sb2=new StringBuilder();
                    int a=index-1;
                    while(true){
                        int u = a / 26;
                        int v = a % 26;
                        sb2.insert(0,(char)(v+'a'));
                        if(u==0){
                            break;
                        }
                        a=u;
                    }
                    sb.append(sb2);
                    break;
                }
                case 'A':{
                    StringBuilder sb2=new StringBuilder();
                    int a=index-1;
                    while(true){
                        int u = a / 26;
                        int v = a % 26;
                        sb2.insert(0,(char)(v+'A'));
                        if(u==0){
                            break;
                        }
                        a=u;
                    }
                    sb.append(sb2);
                    break;
                }
                case 'V':{
                    if (index > 3999) {
                        sb.append(index);
                    }else {
                        sb.append(intToRoman(index));
                    }
                    break;
                }
                default:{
                    sb.append(index);
                }
            }
            if(format){
                sb.append(".");
            }
            return sb.toString();
        }
    }

    public static String intToRoman(int num) {
        if (num <= 0 || num > 3999) {
            throw new IllegalArgumentException("Number out of range (must be 1..3999)");
        }

        int[] values =    {1000, 900, 500, 400, 100,  90,  50, 40, 10, 9,  5, 4, 1};
        String[] symbols ={"M",  "CM","D", "CD","C","XC","L","XL","X","IX","V","IV","I"};

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                num -= values[i];
                result.append(symbols[i]);
            }
        }

        return result.toString();
    }
    public static class NodeWithIndent {
        public int indent;
        public NTxNode bullet;
        public NTxNode child;
        public NTxBounds2 rowBounds;
        public NTxBounds2 childBounds;
        public NTxBounds2 childSelfBounds;
        public NTxBounds2 bulletBounds;
        public NTxBounds2 bulletSelfBounds;
        public double height;
    }
}
