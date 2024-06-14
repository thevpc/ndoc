package net.thevpc.halfa.engine.resources;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.io.NPath;

public class HResourceFactory {
    public static HResource of(NPath p){
        if(p==null){
            return null;
        }
        return new NPathHResource(p);
    }
}
