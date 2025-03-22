package net.thevpc.ndoc.engine.renderer.screen.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JPopupMenuHelper {
    private List<JMenuItem> items = new ArrayList<>();

    public JPopupMenuHelper addSeparator() {
        items.add(null);
        return this;
    }
    public JPopupMenuHelper addMenuItem(String label, ActionListener listener) {
        if (Desktop.isDesktopSupported()) {
            JMenuItem menu = new JMenuItem(label);
            menu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (listener != null) {
                        listener.actionPerformed(e);
                    }
                }
            });
            items.add(menu);
        }
        return this;
    };

    public JPopupMenu build(){
        boolean wasSep=false;
        JPopupMenu jb=null;
        for (Iterator<JMenuItem> iterator = items.iterator(); iterator.hasNext(); ) {
            JMenuItem item = iterator.next();
            if(item==null){
                wasSep=true;
            }else{
                if(jb==null){
                    jb=new JPopupMenu();
                }
                if(wasSep){
                    wasSep=false;
                    jb.addSeparator();
                }
                jb.add(item);
            }
        }
        return jb;
    }

    public void show(MouseEvent e) {
        JPopupMenu build = build();
        if(build!=null){
            build.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
