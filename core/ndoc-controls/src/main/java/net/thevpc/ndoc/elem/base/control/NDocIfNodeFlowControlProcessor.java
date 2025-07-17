package net.thevpc.ndoc.elem.base.control;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.spi.NDocNodeFlowControlProcessor;
import net.thevpc.ndoc.spi.NDocNodeFlowControlProcessorContext;
import net.thevpc.ndoc.spi.eval.NDocNodeEvalNDoc;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NDocIfNodeFlowControlProcessor implements NDocNodeFlowControlProcessor {
    @Override
    public NDocNode[] process(NDocNode node, NDocNodeFlowControlProcessorContext context) {
        if(node.type().equals(NDocNodeType.DEFINE)){
            return new NDocNode[]{node};
        }
        processAssign(node, context);
        processEvalVars(node, context);
        NDocNode[] newNodes=processIfElse(node, context);
        processChildren(newNodes,context);
        return newNodes;
    }

    private NDocNode[] processIfElse(NDocNode nn, NDocNodeFlowControlProcessorContext context) {
        List<NDocNode> children = new ArrayList<>(nn.children());
        List<NDocNode> newChildren = new ArrayList<>();
        while (!children.isEmpty()) {
            NDocNode first = children.remove(0);
            if (first.type().endsWith(NDocNodeType.IF)) {
                NDocNode ifNode = first;
                List<NDocNode> elseIfs = new ArrayList<>();
                NDocNode elseNode = null;
                while (true) {
                    if (!children.isEmpty()) {
                        NDocNode n = children.get(0);
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
                ifBloc.trueBloc = new Bloc(ifNode.children().toArray(new NDocNode[0]));
                ifBloc.elseIfs = elseIfs.stream().map(x -> {
                    ElseIfBloc b = new ElseIfBloc();
                    b.cond = x.getPropertyValue("condition").get();
                    b.trueBloc = new Bloc(x.children().toArray(new NDocNode[0]));
                    return b;
                }).toArray(ElseIfBloc[]::new);
                ifBloc.elseBloc = new Bloc(elseNode == null ? new NDocNode[0] : elseNode.children().toArray(new NDocNode[0]));
                newChildren.addAll(Arrays.asList(evalIfBloc(ifNode, ifBloc, context)));
            } else {
                newChildren.add(first);
            }
        }
        nn.setChildren(newChildren.toArray(new NDocNode[0]));
        if (nn.type().endsWith(NDocNodeType.IF)) {
            IfBloc ifBloc = new IfBloc();
            ifBloc.cond = nn.getPropertyValue("condition").get();
            ifBloc.trueBloc = new Bloc(nn.children().toArray(new NDocNode[0]));
            ifBloc.elseIfs = new ElseIfBloc[0];
            ifBloc.elseBloc = new Bloc(new NDocNode[0]);
            return evalIfBloc(nn, ifBloc, context);
        }
        return new NDocNode[]{nn};
    }

    private void processAssign(NDocNode nn, NDocNodeFlowControlProcessorContext context) {
        if (nn.type().endsWith(NDocNodeType.ASSIGN)) {
            String n = nn.getPropertyValue(NDocPropName.NAME).get().asStringValue().get();
            NElement value = context.evalExpression(nn, nn.getPropertyValue(NDocPropName.VALUE).get());
            nn.parent().setVar(n, value);
        }
    }

    private void processEvalVars(NDocNode nn, NDocNodeFlowControlProcessorContext context) {
        for (HProp property : nn.getProperties().toArray(new HProp[0])) {
            String n = property.getName();
            Object value = property.getValue();
            nn.setProperty(n, context.evalExpression(nn, NElemUtils.toElement(value)));
        }
    }

    private void processChildren(NDocNode[] newNodes, NDocNodeFlowControlProcessorContext context) {
        for (NDocNode newNode : newNodes) {
            List<NDocNode> cc=new ArrayList<>();
            for (NDocNode child : newNode.children()) {
                NDocNode[] processed = process(child, context);
                cc.addAll(Arrays.asList(processed));
            }
            newNode.setChildren(cc.toArray(new NDocNode[0]));
        }
    }


    private static class Bloc {
        NDocNode[] nodes;

        public Bloc() {
        }

        public Bloc(NDocNode[] nodes) {
            this.nodes = nodes;
        }
    }

    private static class ElseIfBloc {
        NElement cond;
        Bloc trueBloc;
    }

    private static class IfBloc {
        NElement cond;
        Bloc trueBloc;
        ElseIfBloc[] elseIfs;
        Bloc elseBloc;
    }


    public NDocNode[] evalIfBloc(NDocNode node, IfBloc ifBloc, NDocNodeFlowControlProcessorContext context) {
        NElement value = context.evalExpression(node, ifBloc.cond);
        if (NDocNodeEvalNDoc.asBoolean(value)) {
            return ifBloc.trueBloc.nodes;
        }

        for (ElseIfBloc elseIf : ifBloc.elseIfs) {
            value = context.evalExpression(node, elseIf.cond);
            if (NDocNodeEvalNDoc.asBoolean(value)) {
                return elseIf.trueBloc.nodes;
            }
        }
        return ifBloc.elseBloc.nodes;
    }

}
