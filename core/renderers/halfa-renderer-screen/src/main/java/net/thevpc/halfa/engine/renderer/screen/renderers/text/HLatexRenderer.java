package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HLatex;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.ConvertedHPartRenderer;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class HLatexRenderer extends ConvertedHPartRenderer {

    @Override
    public HNode convert(HNode p, HPartRendererContext ctx) {
        HLatex ul = (HLatex) p;
        List<HNode> all=new ArrayList<>();
        String mode="text";
        StringBuilder sb=new StringBuilder();
        char[] charArray = NStringUtils.trim(ul.value()).toCharArray();
        HDocumentFactory f = ctx.documentFactory();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (mode) {
                case "text": {
                    switch (c) {
                        case '$': {
                            if(i+1<charArray.length && charArray[i+1] == '$'){
                                sb.append(c);
                                i++;
                            }else{
                                if(sb.length()>0){
                                    all.add(f.ofText().setValue(sb.toString()));
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
                                all.add(f.ofEquation().setValue(sb.toString()));
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
                case "text":{
                    all.add(f.ofText().setValue(sb.toString()));
                    break;
                }
                case "eq":{
                    all.add(f.ofEquation().setValue(sb.toString()));
                    break;
                }
            }
        }
        HText t = f.ofText();
        t.setParent(p);
        HStyle o = t.computeStyle(HStyleType.FONT_SIZE).orNull();
        t.setParent(null);
        if(o==null){
            o=HStyles.fontSize(20);
        }
        for (HNode h : all) {
            h.set(o);
        }
        return f.ofFlow(all.toArray(new HNode[0]));
    }
}
