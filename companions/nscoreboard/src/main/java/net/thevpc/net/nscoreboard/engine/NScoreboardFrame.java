package net.thevpc.net.nscoreboard.engine;

import net.thevpc.net.nscoreboard.model.NScoreboard;

import javax.swing.*;
import java.awt.*;

public class NScoreboardFrame extends JFrame {
    public NScoreboardFrame(NScoreboard model) {
        ScoreBoardPanel b = new ScoreBoardPanel();
        this.getContentPane().add(b);
        Dimension preferredSize = new Dimension(800, 600);
        this.setPreferredSize(preferredSize);
        this.setSize(preferredSize);
        this.setIconImage(model.getIcon());
        b.setModel(model);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
