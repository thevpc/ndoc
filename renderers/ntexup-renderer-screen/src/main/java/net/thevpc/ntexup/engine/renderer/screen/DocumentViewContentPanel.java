package net.thevpc.ntexup.engine.renderer.screen;

import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.engine.renderer.elem2d.text.util.NTxTextUtils;
import net.thevpc.ntexup.engine.renderer.screen.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DocumentViewContentPanel extends JPanel {

    private final DocumentView documentView;
    CardLayout cardLayout;
    List<NTxDocumentLayer> layers = new ArrayList<>();

    public DocumentViewContentPanel(DocumentView documentView) {
        this.documentView = documentView;
        this.cardLayout = new CardLayout();
        setLayout(cardLayout);
        layers.add(new PizzaProgressLayer());
        layers.add(new PageIndexSimpleLayer());
        layers.add(new SourceNameSimpleLayer());
        layers.add(new RuntimeProgressLayer());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        NTxGraphics hg = documentView.engine.createGraphics(g2d);
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension size = getSize();
        for (NTxDocumentLayer filter : layers) {
            filter.draw(documentView, documentView.currentShowingPage, size, hg);
        }
        if (documentView.currentThrowable != null) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            NTxTextUtils.drawThrowable(documentView.currentThrowable, new Rectangle2D.Double(20, 20, 1000, 1000), g2d);
        }
    }

    public void doShow(String id) {
        cardLayout.show(documentView.contentPane, id);
    }
}
