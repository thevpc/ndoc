package net.thevpc.halfa.api.node;

public interface HCtrlAssign extends HNode{
    String getLeft();

    HCtrlAssign setLeft(String left) ;

    Object getRight();

    HCtrlAssign setRight(Object right) ;
}
