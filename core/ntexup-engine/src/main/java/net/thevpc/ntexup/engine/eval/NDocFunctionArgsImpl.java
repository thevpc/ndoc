package net.thevpc.ntexup.engine.eval;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.eval.NTxFunctionArg;
import net.thevpc.ntexup.api.eval.NDocFunctionArgs;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.nuts.elem.NElement;

import java.util.Arrays;
import java.util.List;

public class NDocFunctionArgsImpl implements NDocFunctionArgs {
    private NTxFunctionArg[] args;

    public NDocFunctionArgsImpl(NElement[] callArgs, NTxNode node, NDocEngine engine, NDocVarProvider varProvider) {
        this(Arrays.stream(callArgs).map(x -> new NTxFunctionArgImpl(x, node,engine,varProvider)).toArray(NTxFunctionArg[]::new));
    }
    public NDocFunctionArgsImpl(List<NElement> callArgs, NTxNode node, NDocEngine engine, NDocVarProvider varProvider) {
        this(callArgs.stream().map(x -> new NTxFunctionArgImpl(x, node,engine,varProvider)).toArray(NTxFunctionArg[]::new));
    }

    public NDocFunctionArgsImpl(NTxFunctionArg[] args) {
        this.args = args;
    }

    @Override
    public int size() {
        return args.length;
    }

    @Override
    public NTxFunctionArg[] args() {
        return Arrays.copyOf(args, args.length);
    }

    @Override
    public NTxFunctionArg arg(int index) {
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
        return Arrays.stream(args).map(NTxFunctionArg::eval).toArray(NElement[]::new);
    }
}
