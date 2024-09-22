/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.container.grid;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem2d.Int2;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.parser.HParserUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
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
    protected void processImplicitStyles(ParseArgumentInfo info) {
        switch (info.uid) {
            case "vgrid":
            case "column": {
                if(info.node.getProperty(HPropName.COLUMNS).isNotPresent()) {
                    info.node.setProperty(HPropName.COLUMNS, Tson.of(1));
                }
                if(info.node.getProperty(HPropName.ROWS).isNotPresent()) {
                    info.node.setProperty(HPropName.ROWS, Tson.of(-1));
                }
                break;
            }
            case "hgrid":
            case "row": {
                if(info.node.getProperty(HPropName.COLUMNS).isNotPresent()) {
                    info.node.setProperty(HPropName.COLUMNS, Tson.of(-1));
                }
                if(info.node.getProperty(HPropName.ROWS).isNotPresent()) {
                    info.node.setProperty(HPropName.ROWS, Tson.of(1));
                }
                break;
            }
        }
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case UPLET: {
                NOptional<Int2> d = ObjEx.of(info.currentArg).asInt2();
                if (d.isPresent()) {
                    Int2 dd = d.get();
                    info.node.setProperty(HPropName.COLUMNS, ObjEx.of(dd.getX()).asTsonInt().get());
                    info.node.setProperty(HPropName.ROWS, ObjEx.of(dd.getY()).asTsonInt().get());
                    return true;
                }
                return false;
            }
            case PAIR: {
                TsonPair pp = info.currentArg.toPair();
                TsonElement k = pp.key();
                TsonElement v = pp.value();
                ObjEx ph = ObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {

                    String id = HUtils.uid(info.id);
                    switch (HUtils.uid(n.get())) {
                        case "columns": {
                            if (id.equals("grid") || id.equals("hgrid") || id.equals("row")) {
                                info.node.setProperty(HPropName.COLUMNS, ObjEx.of(v).asTsonInt().get());
                                return true;
                            }
                            break;
                        }
                        case "rows": {
                            if (id.equals("grid") || id.equals("vgrid") || id.equals("column")) {
                                info.node.setProperty(HPropName.ROWS, ObjEx.of(v).asTsonInt().get());
                                return true;
                            }
                            break;
                        }
                    }

                }
            }
        }
        if(HParserUtils.isIntOrExprNonCommon(info.currentArg)){
            String id = HUtils.uid(info.id);
            if (id.equals("row")|| id.equals("hgrid")) {
                if(info.node.getProperty(HPropName.COLUMNS).isNotPresent()){
                    info.node.setProperty(HPropName.COLUMNS,info.currentArg);
                    return true;
                }
            }else if (id.equals("column")|| id.equals("vgrid")) {
                if(info.node.getProperty(HPropName.ROWS).isNotPresent()){
                    info.node.setProperty(HPropName.ROWS,info.currentArg);
                    return true;
                }
            }else  if (id.equals("grid")) {
                if(info.node.getProperty(HPropName.COLUMNS).isNotPresent()){
                    info.node.setProperty(HPropName.COLUMNS,info.currentArg);
                    return true;
                }else if(info.node.getProperty(HPropName.ROWS).isNotPresent()){
                    info.node.setProperty(HPropName.ROWS,info.currentArg);
                    return true;
                }
            }
        }
        return super.processArgument(info);
    }
}
