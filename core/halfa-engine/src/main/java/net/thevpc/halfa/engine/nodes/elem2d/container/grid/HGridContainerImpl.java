/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.elem2d.container.grid;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem2d.Int2;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

/**
 * @author vpc
 */
public class HGridContainerImpl extends HNodeParserBase {

    public HGridContainerImpl() {
        super(true, HNodeType.GRID, "vgrid", "hgrid");
    }

    @Override
    protected void processImplicitStyles(String id, HNode p, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (id) {
            case "vgrid": {
                p.setProperty(HPropName.COLUMNS, 1);
                p.setProperty(HPropName.ROWS, -1);
                break;
            }
            case "hgrid": {
                p.setProperty(HPropName.COLUMNS, -1);
                p.setProperty(HPropName.ROWS, 1);
                break;
            }
        }
    }

    @Override
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case UPLET: {
                NOptional<Int2> d = ObjEx.of(e).asInt2();
                if (d.isPresent()) {
                    Int2 dd = d.get();
                    node.setProperty(HProp.ofInt(HPropName.COLUMNS, ObjEx.of(dd.getX()).asInt().get()));
                    node.setProperty(HProp.ofInt(HPropName.ROWS, ObjEx.of(dd.getY()).asInt().get()));
                    return true;
                }
                return false;
            }
            case PAIR: {
                TsonPair pp = e.toPair();
                TsonElement k = pp.getKey();
                TsonElement v = pp.getValue();
                ObjEx ph = ObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {
                    switch (HUtils.uid(n.get())) {
                        case "columns": {
                            node.setProperty(HProp.ofInt(HPropName.COLUMNS, ObjEx.of(v).asInt().get()));
                            return true;
                        }
                        case "rows": {
                            node.setProperty(HProp.ofInt(HPropName.ROWS, ObjEx.of(v).asInt().get()));
                            return true;
                        }
                    }
                }
            }
        }
        return super.processArg(id, node, e, f, context);
    }
}
