/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author vpc
 */
public class HPageGroupImpl extends AbstractHNodeTypeFactory {

    public HPageGroupImpl() {
        super(true, HNodeType.PAGE_GROUP);
    }

    @Override
    public boolean validateNode(HNode node) {
        boolean someChanges=false;
        List<HNode> children = new ArrayList<>(node.children());
        List<HNode> newChildren = new ArrayList<>();
        List<HNode> pending=null;
        for (HNode c : children) {
            if(c==null){
                someChanges=true;
            }else{
                someChanges|=engine().validateNode(c);
                if(Objects.equals(c.type(),HNodeType.PAGE_GROUP)
                        || Objects.equals(c.type(),HNodeType.PAGE)
                ){
                    if(pending!=null && pending.size()>0){
                        HNode newPage = engine().documentFactory().of(HNodeType.PAGE);
                        newChildren.add(newPage);
                        newPage.addAll(pending.toArray(new HNode[0]));
                        pending=null;
                    }
                    newChildren.add(c);
                }else{
                    someChanges=true;
                    if(pending==null){
                        pending=new ArrayList<>();
                    }
                    pending.add(c);
                }
            }
        }
        if(pending!=null && pending.size()>0){
            HNode newPage = engine().documentFactory().of(HNodeType.PAGE);
            newPage.addAll(pending.toArray(new HNode[0]));
            newChildren.add(newPage);
        }
        if(someChanges){
            node.children().clear();
            node.addAll(newChildren.toArray(new HNode[0]));
        }
        return someChanges;
    }
}
