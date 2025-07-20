package net.thevpc.ndoc.engine.fct;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.ndoc.api.model.node.NDocItem;

public class DefaultNDocFunctionContext implements NDocFunctionContext {

    public DefaultNDocFunctionContext(NDocItem node, NDocEngine engine) {
    }

//    @Override
//    public Object invokeFunction(String name, NDocFunctionArg[] args) {
//        return engine.findFunction(node, name, args).get().invoke(args, this);
//    }
//

//    @Override
//    public NDocFunctionArg createNodeArg(NDocNode node) {
//        return new NDocNodeFunctionArg(node);
//    }
//
//    @Override
//    public NDocFunctionArg createUserObjectArg(Object node) {
//        return new UserObjectFunctionArg(node);
//    }
//
//
//
//    private class NDocNodeFunctionArg implements NDocFunctionArg {
//        NDocNode node;
//
//        public NDocNodeFunctionArg(NDocNode node) {
//            this.node = node;
//        }
//
//        public NDocNode get() {
//            return node.copy();
//        }
//    }
//
//
//
//    private class UserObjectFunctionArg implements NDocFunctionArg {
//        Object node;
//
//        public UserObjectFunctionArg(Object node) {
//            this.node = node;
//        }
//
//        public Object get() {
//            return node;
//        }
//    }
}
