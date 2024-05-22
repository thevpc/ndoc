package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.tson.TsonAnnotation;
import net.thevpc.tson.TsonElement;

public class HNodeParser {
    public static boolean fillAnnotations(TsonElement e, HNode p) {
        for (TsonAnnotation a : e.getAnnotations()) {
            if(p.getParentTemplate()==null) {
                p.setParentTemplate(a.getName());
            }else{
                return false;
            }
        }
        return true;
    }
}
