/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.net.nscoreboard.engine;

import net.thevpc.net.nscoreboard.engine.board.HorizontalScoreBoard;
import net.thevpc.net.nscoreboard.model.NScoreboard;
import net.thevpc.net.nscoreboard.util.Utils;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 *
 * @author vpc
 */
public class ScoreBoardPanel extends JPanel {

    public HorizontalScoreBoard board;
    public JLabel label;
    public ScoreModelAnimator animator;

    public ScoreBoardPanel() {
        super(new BorderLayout());
        add(label = new JLabel(""), BorderLayout.NORTH);
        add(board = new HorizontalScoreBoard(), BorderLayout.CENTER);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                prepareAgain();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prepareAgain();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                prepareAgain();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                prepareAgain();
            }
        });
        prepareAgain();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    animator.startStop();
                }else if(SwingUtilities.isRightMouseButton(e)) {
                    animator.reset();
                }
            }

        });

        animator = new ScoreModelAnimator();
        animator.addListener(m -> {
            board.setModel(m);
            board.setStarted(animator.isStarted());
            repaint();
        });
//        setBackground(new Color(0x008c7a));

    }
    @Override
    protected void paintComponent(Graphics g) {
        NScoreboard model = board.getModel();
        label.setText("        "+model.getTitle());
        super.paintComponent(g);
        Image icon = model.getIcon();
        if(icon != null) {
            g.drawImage(icon, 0, 0, this);
        }
    }

    public void prepareAgain() {
        Dimension s = getSize();
        label.setForeground(Color.BLACK);
        label.setFont(Utils.prepareFont("ARIAL", 0, s.getHeight() / 10).deriveFont(Font.BOLD));
    }

    public void setModel(NScoreboard NScoreboard) {
        board.setModel(NScoreboard);
        animator.setModel(NScoreboard);
    }

}
