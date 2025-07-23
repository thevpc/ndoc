/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container.grid;

import net.thevpc.ndoc.api.document.elem2d.NDocInt2;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.base.parser.HParserUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NDocGridContainerParser extends NDocNodeParserBase {

    public NDocGridContainerParser() {
        super(true, NDocNodeType.GRID, "vgrid", "hgrid", "column", "row");
    }

    @Override
    protected void processImplicitStyles(ParseArgumentInfo info) {
        switch (info.uid) {
            case "vgrid":
            case "column": {
                if(info.node.getProperty(NDocPropName.COLUMNS).isNotPresent()) {
                    info.node.setProperty(NDocPropName.COLUMNS, NElement.ofInt(1));
                }
                if(info.node.getProperty(NDocPropName.ROWS).isNotPresent()) {
                    info.node.setProperty(NDocPropName.ROWS, NElement.ofInt(-1));
                }
                break;
            }
            case "hgrid":
            case "row": {
                if(info.node.getProperty(NDocPropName.COLUMNS).isNotPresent()) {
                    info.node.setProperty(NDocPropName.COLUMNS, NElement.ofInt(-1));
                }
                if(info.node.getProperty(NDocPropName.ROWS).isNotPresent()) {
                    info.node.setProperty(NDocPropName.ROWS, NElement.ofInt(1));
                }
                break;
            }
        }
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case UPLET:
            case NAMED_UPLET:
            {
                NOptional<NDocInt2> d = NDocObjEx.of(info.currentArg).asInt2();
                if (d.isPresent()) {
                    NDocInt2 dd = d.get();
                    info.node.setProperty(NDocPropName.COLUMNS, NDocObjEx.of(dd.getX()).asTsonInt().get());
                    info.node.setProperty(NDocPropName.ROWS, NDocObjEx.of(dd.getY()).asTsonInt().get());
                    return true;
                }
                return false;
            }
            case PAIR: {
                NPairElement pp = info.currentArg.asPair().get();
                NElement k = pp.key();
                NElement v = pp.value();
                NDocObjEx ph = NDocObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {

                    String id = NDocUtils.uid(info.id);
                    switch (NDocUtils.uid(n.get())) {
                        case "columns": {
                            if (id.equals("grid") || id.equals("hgrid") || id.equals("row")) {
                                info.node.setProperty(NDocPropName.COLUMNS, NDocObjEx.of(v).asTsonInt().get());
                                return true;
                            }
                            break;
                        }
                        case "rows": {
                            if (id.equals("grid") || id.equals("vgrid") || id.equals("column")) {
                                info.node.setProperty(NDocPropName.ROWS, NDocObjEx.of(v).asTsonInt().get());
                                return true;
                            }
                            break;
                        }
                    }

                }
            }
        }
        if(HParserUtils.isIntOrExprNonCommon(info.currentArg)){
            String id = NDocUtils.uid(info.id);
            if (id.equals("row")|| id.equals("hgrid")) {
                if(info.node.getProperty(NDocPropName.COLUMNS).isNotPresent()){
                    info.node.setProperty(NDocPropName.COLUMNS,info.currentArg);
                    return true;
                }
            }else if (id.equals("column")|| id.equals("vgrid")) {
                if(info.node.getProperty(NDocPropName.ROWS).isNotPresent()){
                    info.node.setProperty(NDocPropName.ROWS,info.currentArg);
                    return true;
                }
            }else  if (id.equals("grid")) {
                if(info.node.getProperty(NDocPropName.COLUMNS).isNotPresent()){
                    info.node.setProperty(NDocPropName.COLUMNS,info.currentArg);
                    return true;
                }else if(info.node.getProperty(NDocPropName.ROWS).isNotPresent()){
                    info.node.setProperty(NDocPropName.ROWS,info.currentArg);
                    return true;
                }
            }
        }
        return super.processArgument(info);
    }
}
