package net.thevpc.halfa.engine.nodes.elem2d.container.ul;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProps;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engin.spibase.renderer.ConvertedHPartRenderer;

import java.util.ArrayList;
import java.util.List;

public class HUnorderedListRenderer extends ConvertedHPartRenderer {
    HProperties defaultStyles = new HProperties();

    public HUnorderedListRenderer() {
        super(HNodeType.UNORDERED_LIST);
    }

    public HNode convert(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        HDocumentFactory f = ctx.documentFactory();
        List<HNode> all = new ArrayList<>();
        for (HNode child : p.children()) {
            switch (child.type()) {
                case HNodeType.UNORDERED_LIST:
                case HNodeType.ORDERED_LIST: {
                    all.add(f.ofVoid().addStyleClasses("ul-bullet"));
                    break;
                }
                default: {
                    all.add(f.ofSphere()
                            //.setProperty(HPropName.SIZE, Tson.ofDouble(2,"%g"))
                            .addStyleClasses("ul-bullet"));
                    break;
                }
            }
            all.add(child.addStyleClasses("ul-item"));
        }
        HNode newNode = f.ofGrid().addAll(all.toArray(new HNode[0]))
                .setProperty(HProp.ofInt(HPropName.COLUMNS, 2))
                .setProperty(HProp.ofInt(HPropName.ROWS, 2))
                .setProperty(HPropName.ORIGIN, HAlign.TOP_LEFT)
                .setProperty(HProps.columnsWeight(1, 20))
                .setProperties(p.props().toArray(new HProp[0]));
        for (String s : new String[]{
                HPropName.GRID_COLOR
                , HPropName.ORIGIN
                , HPropName.DRAW_GRID
                , HPropName.DRAW_CONTOUR
                , HPropName.COLUMNS_WEIGHT
                , HPropName.BACKGROUND_COLOR
                , HPropName.FILL_BACKGROUND
                , HPropName.PADDING
                , HPropName.MARGIN
                , HPropName.AT
                , HPropName.STROKE
                , HPropName.CLASS
                , HPropName.DEBUG
                , HPropName.DEBUG_COLOR
                , HPropName.HIDE
                , HPropName.FOREGROUND_COLOR
                , HPropName.FONT_BOLD
                , HPropName.FONT_FAMILY
                , HPropName.FONT_ITALIC
                , HPropName.FONT_SIZE
                , HPropName.FONT_UNDERLINED
                , HPropName.FONT_STRIKE
                , HPropName.FONT_STRIKE
        }) {
            Object v = ctx.computePropertyValue(p, s).orNull();
            if (v != null) {
                newNode.setProperty(s, v);
            }
        }
        if(HValueByName.isDebug(p, ctx)){
            Object v = ctx.computePropertyValue(p, HPropName.DRAW_GRID).orNull();
            if(v==null){
                newNode.setProperty(HPropName.DRAW_GRID, true);
            }
        }
        return newNode;
    }
}
