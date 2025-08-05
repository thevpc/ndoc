package net.thevpc.ntexup.engine.eval;

import net.thevpc.ntexup.api.eval.NTxVar;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.util.function.Supplier;

public class NTxVarImpl implements NTxVar {
    private Supplier<NElement>e;

    public static NOptional<NTxVar> ofOptional(String name, Supplier<NElement> e) {
        return NOptional.ofNamed(new NTxVarImpl(e),"var "+name);
    }

    public NTxVarImpl(Supplier<NElement> e) {
        this.e = e;
    }

    @Override
    public NElement get() {
        return e.get();
    }
}
