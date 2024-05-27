package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.ConvertedHPartRenderer;
import net.thevpc.nuts.util.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class HTextRenderer extends ConvertedHPartRenderer {
    HProperties defaultStyles = new HProperties();

    public HTextRenderer() {
        super(HNodeType.TEXT);
    }

    @Override
    public HNode convert(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        List<HNode> all=new ArrayList<>();
        String mode="plain";
        StringBuilder sb=new StringBuilder();
        String trim = NStringUtils.trim((String) (p.getPropertyValue(HPropName.VALUE).orElse("")));
        char[] charArray = trim.toCharArray();
        HDocumentFactory f = ctx.documentFactory();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (mode) {
                case "plain": {
                    switch (c) {
                        case '$': {
                            if(i+1<charArray.length && charArray[i+1] == '$'){
                                sb.append(c);
                                i++;
                            }else{
                                if(sb.length()>0){
                                    all.add(f.ofPlain(sb.toString())
                                            .setPosition(HAlign.TOP_LEFT)
                                            .setOrigin(HAlign.TOP_LEFT)
                                    );
                                    sb.setLength(0);
                                }
                                mode="eq";
                            }
                            break;
                        }
                        default:{
                            sb.append(c);
                        }
                    }
                    break;
                }
                case "eq": {
                    switch (c) {
                        case '\\': {
                            sb.append(c);
                            if(i+1<charArray.length) {
                                sb.append(charArray[i+1]);
                                i++;
                            }
                            break;
                        }
                        case '$': {
                            if(sb.length()>0){
                                all.add(f.ofEquation()
                                        .setProperty(HProp.ofString(HPropName.VALUE,sb.toString()))
                                        .setPosition(HAlign.TOP_LEFT)
                                        .setOrigin(HAlign.TOP_LEFT)
                                );
                                sb.setLength(0);
                            }
                            mode="plain";
                            break;
                        }
                        default:{
                            sb.append(c);
                        }
                    }
                    break;
                }
            }
        }

        if(sb.length()>0){
            switch (mode) {
                case "plain": {
                    all.add(f.ofPlain(sb.toString())
                            .setPosition(HAlign.TOP_LEFT)
                            .setOrigin(HAlign.TOP_LEFT)
                    );
                    break;
                }
                case "eq": {
                    all.add(f.ofEquation()
                            .setProperty(HProp.ofString(HPropName.VALUE,sb.toString()))
                            .setPosition(HAlign.TOP_LEFT)
                            .setOrigin(HAlign.TOP_LEFT)
                    );
                    break;
                }
            }
            sb.setLength(0);
        }

        HProp[] childInheritedProps = p.getProperties()
                .stream().filter(x -> {
                    switch (x.getName()) {
                        case HPropName.SIZE: {
                            return false;
                        }
                        case HPropName.VALUE: {
                            return false;
                        }
                        case HPropName.POSITION: {
                            return false;
                        }
                        case HPropName.ORIGIN: {
                            return false;
                        }
                    }
                    return true;
                })
                .toArray(HProp[]::new);
        HProp[] containerSelfProps = p.getProperties().stream().toArray(HProp[]::new);
        HProperties containerPromotedProps=new HProperties();
        containerPromotedProps.set(p.getProperties()
                .stream().filter(x -> {
//                    switch (x.getName()) {
//                        case HPropName.SIZE:
//                        case HPropName.ORIGIN:
//                        case HPropName.POSITION:
//                        case HPropName.VALUE:
//                        case HPropName.STROKE:
//                        case HPropName.BACKGROUND_COLOR:
//                        case HPropName.FOREGROUND_COLOR:
//                        case HPropName.DRAW_CONTOUR:
//                        case HPropName.D:
//                        {
//                            return false;
//                        }
//                    }
                    return false;
                })
                .toArray(HProp[]::new));
        containerPromotedProps.set(HPropName.DRAW_CONTOUR,false);
        containerPromotedProps.set(HPropName.FILL_BACKGROUND,false);
//        if(sb.length()>0){
//            switch (mode){
//                case "plain":{
//                    all.add(f.ofPlain(sb.toString())
//                            .setProperties(childInheritedProps)
//                    );
//                    break;
//                }
//                case "eq":{
//                    all.add(f.ofEquation(sb.toString())
//                            .setProperties(childInheritedProps)
//                    );
//                    break;
//                }
//            }
//        }
//        HNode t = f.ofPlain();
//        t.setParent(p);
//        HProp o = t.computeProperty(HPropName.FONT_SIZE).orNull();
//        t.setParent(null);
//        if(o==null){
//            o= HProps.fontSize(20);
//        }
//        for (HNode h : all) {
//            h.setProperty(o);
//        }
        return f.ofFlow()
                .setProperties(containerSelfProps)
                .addRule(DefaultHStyleRule.ofAny(containerPromotedProps.toArray()))
                .addAll(all.toArray(new HNode[0]));
    }
}
