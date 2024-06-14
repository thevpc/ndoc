package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.model.HArrayHead;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem3d.*;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DGroup;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.engine.renderer.screen.renderers.HGraphicsImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Example extends Canvas {
    private Projection3D projection;
    int rotating=0;
    int rotatingX=45;
    int rotatingY=-0;
    int rotatingZ=0;
    public Example() {
        projection = new Projection3D(1000);
        // Focal length for projection
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                int v=1;//(int)(Math.random()*3);
//                switch (v){
//                    case 0:{
//                        rotatingX=(rotatingX+10)%360;
//                        break;
//                    }
//                    case 1:{
//                        rotatingY=(rotatingY+10)%360;
//                        break;
//                    }
//                    case 2:{
//                        rotatingZ=(rotatingZ+10)%360;
//                        break;
//                    }
//                }
                Example.this.repaint();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        HGraphicsImpl g3 = new HGraphicsImpl(g2d);
        g3.transform3D(Matrix3D.identity()
//                .translate(0, 0, 200)
                        .rotateX(Math.toRadians(rotatingX))
                        .rotateY(Math.toRadians(rotatingY))
                        .rotateZ(Math.toRadians(rotatingZ))
                        .scale(1, 1, 1)
        );
        g3.project3D(projection);
//        g3.drawElement3D(
//                Element3DFactory.cube(new Point3D(-100, -100, -100), 200),
//                getWidth() / 2, getHeight() / 2
//        );
        Element3DGroup gg = Element3DFactory.group();
//        gg.add(Element3DFactory.sphereUV(new HPoint3D(-100, -100, -100), 200, 50));
        gg.add(Element3DFactory.surface(
                HUtils.dtimes(0,600,50)
                ,HUtils.dtimes(0,600,50)
                ,(x,y)->Math.sin(x/200* Math.PI)*Math.cos(y/200* Math.PI)*100
        ));
        gg.add(Element3DFactory.axis(
                new HPoint3D(0, 0, 0),
                300
        ));
        gg.add(Element3DFactory.segment(
                new HPoint3D(100, 100, 100),
                g3.getLight3D().orientation(),
                200
        ).setEndType(HArrayHead.ARROW).setLineStroke(new BasicStroke(3)));
        g3.draw3D(
                gg,
                new HPoint2D(getWidth() / 2, getHeight() / 2)
        );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple 3D");
        Example canvas = new Example();
        frame.add(canvas);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

