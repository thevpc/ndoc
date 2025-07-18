package net.thevpc.ndoc.elem.base.container.ul;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocProp;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.base.renderer.ConvertedNDocNodeRenderer;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class NDocUnorderedListRenderer extends ConvertedNDocNodeRenderer {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocUnorderedListRenderer() {
        super(NDocNodeType.UNORDERED_LIST);
    }

    public NDocNode convert(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocDocumentFactory f = ctx.documentFactory();
        List<NDocNode> all = new ArrayList<>();
        for (NDocNode child : p.children()) {
            switch (child.type()) {
                case NDocNodeType.UNORDERED_LIST:
                case NDocNodeType.ORDERED_LIST: {
                    all.add(f.ofVoid().addStyleClasses("ul-bullet"));
                    break;
                }
                default: {
                    all.add(f.ofSphere()
                            //.setProperty(HPropName.SIZE, NElements.ofDouble(2,"%g"))
                            .addStyleClasses("ul-bullet"));
                    break;
                }
            }
            all.add(child.addStyleClasses("ul-item"));
        }
        NDocNode newNode = f.ofGrid().addAll(all.toArray(new NDocNode[0]))
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(2))
                .setProperty(NDocPropName.ORIGIN, NDocAlign.TOP_LEFT)
                .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NDocProp[0]));
        for (String s : new String[]{
                NDocPropName.GRID_COLOR
                , NDocPropName.ORIGIN
                , NDocPropName.DRAW_GRID
                , NDocPropName.DRAW_CONTOUR
                , NDocPropName.COLUMNS_WEIGHT
                , NDocPropName.BACKGROUND_COLOR
                , NDocPropName.FILL_BACKGROUND
                , NDocPropName.PADDING
                , NDocPropName.MARGIN
                , NDocPropName.AT
                , NDocPropName.STROKE
                , NDocPropName.CLASS
                , NDocPropName.DEBUG
                , NDocPropName.DEBUG_COLOR
                , NDocPropName.HIDE
                , NDocPropName.FOREGROUND_COLOR
                , NDocPropName.FONT_BOLD
                , NDocPropName.FONT_FAMILY
                , NDocPropName.FONT_ITALIC
                , NDocPropName.FONT_SIZE
                , NDocPropName.FONT_UNDERLINED
                , NDocPropName.FONT_STRIKE
                , NDocPropName.FONT_STRIKE
        }) {
            NElement v = ctx.computePropertyValue(p, s).orNull();
            if (v != null) {
                newNode.setProperty(s, v);
            }
        }
        if(NDocValueByName.isDebug(p, ctx)){
            Object v = ctx.computePropertyValue(p, NDocPropName.DRAW_GRID).orNull();
            if(v==null){
                newNode.setProperty(NDocPropName.DRAW_GRID, NElement.ofBoolean(true));
            }
        }
        return newNode;
    }
}
