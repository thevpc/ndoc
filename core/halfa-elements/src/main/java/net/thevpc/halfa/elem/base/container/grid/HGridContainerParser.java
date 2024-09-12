/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.container.grid;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem2d.Int2;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

/**
 * @author vpc
 */
public class HGridContainerParser extends HNodeParserBase {

    public HGridContainerParser() {
        super(true, HNodeType.GRID, "vgrid", "hgrid", "column", "row");
    }

    @Override
    protected void processImplicitStyles(String id, HNode p, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (id) {
            case "vgrid":
            case "column": {
                p.setProperty(HPropName.COLUMNS, 1);
                p.setProperty(HPropName.ROWS, -1);
                break;
            }
            case "hgrid":
            case "row": {
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
                TsonElement k = pp.key();
                TsonElement v = pp.value();
                ObjEx ph = ObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {

                    switch (HUtils.uid(n.get())) {
                        case "columns": {
                            if (HUtils.uid(id).equals("grid") || HUtils.uid(id).equals("hgrid") || HUtils.uid(id).equals("row")) {
                                node.setProperty(HProp.ofInt(HPropName.COLUMNS, ObjEx.of(v).asInt().get()));
                                return true;
                            }
                            break;
                        }
                        case "rows": {
                            if (HUtils.uid(id).equals("grid") || HUtils.uid(id).equals("vgrid") || HUtils.uid(id).equals("column")) {
                                node.setProperty(HProp.ofInt(HPropName.ROWS, ObjEx.of(v).asInt().get()));
                                return true;
                            }
                            break;
                        }
                    }

                }
            }
        }
        return super.processArg(id, node, e, f, context);
    }
}
