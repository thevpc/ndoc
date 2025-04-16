package net.thevpc.ndoc.elem.base.container.ul;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.model.elem2d.HAlign;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.base.renderer.ConvertedNDocNodeRenderer;
import net.thevpc.tson.Tson;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class NDocUnorderedListRenderer extends ConvertedNDocNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public NDocUnorderedListRenderer() {
        super(HNodeType.UNORDERED_LIST);
    }

    public HNode convert(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocDocumentFactory f = ctx.documentFactory();
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
                .setProperty(HPropName.COLUMNS, NElements.of().of(2))
                .setProperty(HPropName.ROWS, NElements.of().of(2))
                .setProperty(HPropName.ORIGIN, HAlign.TOP_LEFT)
                .setProperty(HPropName.COLUMNS_WEIGHT,NElements.of().of(new double[]{1, 20}))
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
            NElement v = ctx.computePropertyValue(p, s).orNull();
            if (v != null) {
                newNode.setProperty(s, v);
            }
        }
        if(NDocValueByName.isDebug(p, ctx)){
            Object v = ctx.computePropertyValue(p, HPropName.DRAW_GRID).orNull();
            if(v==null){
                newNode.setProperty(HPropName.DRAW_GRID, NElements.of().of(true));
            }
        }
        return newNode;
    }
}
