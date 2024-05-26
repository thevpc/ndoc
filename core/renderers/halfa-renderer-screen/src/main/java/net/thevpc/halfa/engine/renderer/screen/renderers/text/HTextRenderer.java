package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProps;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.ConvertedHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
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
                                    all.add(f.ofPlain(sb.toString()));
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
                                all.add(f.ofEquation().setProperty(HProp.ofString(HPropName.VALUE,sb.toString())));
                                sb.setLength(0);
                            }
                            mode="text";
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
            switch (mode){
                case "plain":{
                    all.add(f.ofPlain(sb.toString()));
                    break;
                }
                case "eq":{
                    all.add(f.ofEquation(sb.toString()));
                    break;
                }
            }
        }
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
        return f.ofFlow().addAll(all.toArray(new HNode[0]));
    }
}
