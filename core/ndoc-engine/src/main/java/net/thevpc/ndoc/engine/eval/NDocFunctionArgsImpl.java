package net.thevpc.ndoc.engine.eval;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.ndoc.api.eval.NDocFunctionArgs;
import net.thevpc.nuts.elem.NElement;

import java.util.Arrays;
import java.util.List;

public class NDocFunctionArgsImpl implements NDocFunctionArgs {
    private NDocFunctionArg[] args;

    public NDocFunctionArgsImpl(NElement[] callArgs, NDocNode node,NDocEngine engine) {
        this(Arrays.stream(callArgs).map(x -> new NDocFunctionArgImpl(x, node,engine)).toArray(NDocFunctionArg[]::new));
    }
    public NDocFunctionArgsImpl(List<NElement> callArgs, NDocNode node, NDocEngine engine) {
        this(callArgs.stream().map(x -> new NDocFunctionArgImpl(x, node,engine)).toArray(NDocFunctionArg[]::new));
    }

    public NDocFunctionArgsImpl(NDocFunctionArg[] args) {
        this.args = args;
    }

    @Override
    public int size() {
        return args.length;
    }

    @Override
    public NDocFunctionArg[] args() {
        return Arrays.copyOf(args, args.length);
    }

    @Override
    public NDocFunctionArg arg(int index) {
        return args[index];
    }

    @Override
    public NElement src(int index) {
        return args[index].src();
    }

    @Override
    public NElement eval(int index) {
        return args[index].eval();
    }

    @Override
    public NElement[] eval() {
        return Arrays.stream(args).map(NDocFunctionArg::eval).toArray(NElement[]::new);
    }
}
