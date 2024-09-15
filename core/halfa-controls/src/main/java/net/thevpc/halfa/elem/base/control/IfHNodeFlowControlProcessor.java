package net.thevpc.halfa.elem.base.control;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.TsonUtils;
import net.thevpc.halfa.spi.HNodeFlowControlProcessor;
import net.thevpc.halfa.spi.HNodeFlowControlProcessorContext;
import net.thevpc.halfa.spi.eval.HNodeEval;
import net.thevpc.tson.TsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IfHNodeFlowControlProcessor implements HNodeFlowControlProcessor {
    @Override
    public HNode[] process(HNode hNode, HNodeFlowControlProcessorContext context) {
        if(hNode.type().equals(HNodeType.DEFINE)){
            return new HNode[]{hNode};
        }
        processAssign(hNode, context);
        processEvalVars(hNode, context);
        HNode[] newNodes=processIfElse(hNode, context);
        processChildren(newNodes,context);
        return newNodes;
    }

    private HNode[] processIfElse(HNode hNode, HNodeFlowControlProcessorContext context) {
        List<HNode> children = new ArrayList<>(hNode.children());
        List<HNode> newChildren = new ArrayList<>();
        while (!children.isEmpty()) {
            HNode first = children.remove(0);
            if (first.type().endsWith(HNodeType.IF)) {
                HNode ifNode = first;
                List<HNode> elseIfs = new ArrayList<>();
                HNode elseNode = null;
                while (true) {
                    if (!children.isEmpty()) {
                        HNode n = children.get(0);
                        if (n.type().equals("elseif")) {
                            children.remove(0);
                            elseIfs.add(n);
                        } else if (n.type().endsWith("else")) {
                            children.remove(0);
                            elseNode = n;
                            break;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                IfBloc ifBloc = new IfBloc();
                ifBloc.cond = ifNode.getPropertyValue("condition").get();
                ifBloc.trueBloc = new Bloc(ifNode.children().toArray(new HNode[0]));
                ifBloc.elseIfs = elseIfs.stream().map(x -> {
                    ElseIfBloc b = new ElseIfBloc();
                    b.cond = x.getPropertyValue("condition").get();
                    b.trueBloc = new Bloc(x.children().toArray(new HNode[0]));
                    return b;
                }).toArray(ElseIfBloc[]::new);
                ifBloc.elseBloc = new Bloc(elseNode == null ? new HNode[0] : elseNode.children().toArray(new HNode[0]));
                newChildren.addAll(Arrays.asList(evalIfBloc(ifNode, ifBloc, context)));
            } else {
                newChildren.add(first);
            }
        }
        hNode.setChildren(newChildren.toArray(new HNode[0]));
        if (hNode.type().endsWith(HNodeType.IF)) {
            IfBloc ifBloc = new IfBloc();
            ifBloc.cond = hNode.getPropertyValue("condition").get();
            ifBloc.trueBloc = new Bloc(hNode.children().toArray(new HNode[0]));
            ifBloc.elseIfs = new ElseIfBloc[0];
            ifBloc.elseBloc = new Bloc(new HNode[0]);
            return evalIfBloc(hNode, ifBloc, context);
        }
        return new HNode[]{hNode};
    }

    private void processAssign(HNode hNode, HNodeFlowControlProcessorContext context) {
        if (hNode.type().endsWith(HNodeType.ASSIGN)) {
            String n = hNode.getPropertyValue(HPropName.NAME).get().toStr().value();
            TsonElement value = context.evalExpression(hNode, hNode.getPropertyValue(HPropName.VALUE).get());
            hNode.parent().setVar(n, value);
        }
    }

    private void processEvalVars(HNode hNode, HNodeFlowControlProcessorContext context) {
        for (HProp property : hNode.getProperties().toArray(new HProp[0])) {
            String n = property.getName();
            Object value = property.getValue();
            hNode.setProperty(n, context.evalExpression(hNode, TsonUtils.toTson(value)));
        }
    }

    private void processChildren(HNode[] newNodes, HNodeFlowControlProcessorContext context) {
        for (HNode newNode : newNodes) {
            List<HNode> cc=new ArrayList<>();
            for (HNode child : newNode.children()) {
                HNode[] processed = process(child, context);
                cc.addAll(Arrays.asList(processed));
            }
            newNode.setChildren(cc.toArray(new HNode[0]));
        }
    }


    private static class Bloc {
        HNode[] nodes;

        public Bloc() {
        }

        public Bloc(HNode[] nodes) {
            this.nodes = nodes;
        }
    }

    private static class ElseIfBloc {
        TsonElement cond;
        Bloc trueBloc;
    }

    private static class IfBloc {
        TsonElement cond;
        Bloc trueBloc;
        ElseIfBloc[] elseIfs;
        Bloc elseBloc;
    }


    public HNode[] evalIfBloc(HNode node, IfBloc ifBloc, HNodeFlowControlProcessorContext context) {
        TsonElement value = context.evalExpression(node, ifBloc.cond);
        if (HNodeEval.asBoolean(value)) {
            return ifBloc.trueBloc.nodes;
        }

        for (ElseIfBloc elseIf : ifBloc.elseIfs) {
            value = context.evalExpression(node, elseIf.cond);
            if (HNodeEval.asBoolean(value)) {
                return elseIf.trueBloc.nodes;
            }
        }
        return ifBloc.elseBloc.nodes;
    }

}
