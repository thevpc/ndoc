/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.image;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;

import java.util.HashSet;
import java.util.Set;


/**
 * @author vpc
 */
public class NDocImageParser extends NDocNodeParserBase {

    public NDocImageParser() {
        super(false, NDocNodeType.IMAGE);
    }

    @Override
    protected boolean processArguments(ParseArgumentInfo info) {
        Set<Integer> processed = new HashSet<>();
        boolean found = false;
        for (int i = 0; i < info.arguments.length; i++) {
            NElement currentArg = info.arguments[i];
            switch (currentArg.type()) {
                case DOUBLE_QUOTED_STRING:
                case SINGLE_QUOTED_STRING:
                case ANTI_QUOTED_STRING:
                case TRIPLE_DOUBLE_QUOTED_STRING:
                case TRIPLE_SINGLE_QUOTED_STRING:
                case TRIPLE_ANTI_QUOTED_STRING:
                case LINE_STRING:
                {
                    info.node.setProperty(NDocPropName.VALUE, info.context.asPathRef(currentArg));
                    processed.add(i);
                    found = true;
                    break;
                }
                case PAIR: {
                    if (currentArg.isSimplePair()) {
                        NPairElement p = currentArg.asPair().get();
                        String sid = net.thevpc.ndoc.api.util.HUtils.uid(p.key().asStringValue().get());
                        switch (sid) {
                            case NDocPropName.VALUE:
                            case NDocPropName.FILE: {
                                info.node.setProperty(sid, info.context.asPathRef(p.value()));
                                processed.add(i);
                                found = true;
                                break;
                            }
                            case "content":
                            case "src": {
                                info.node.setProperty(NDocPropName.VALUE, info.context.asPathRef(p.value()));
                                processed.add(i);
                                found = true;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        if (!found) {
            for (int i = 0; i < info.arguments.length; i++) {
                if (!processed.contains(i)) {
                    NElement currentArg = info.arguments[i];
                    switch (currentArg.type()) {
                        case NAME: {
                            info.node.setProperty(NDocPropName.VALUE, info.context.asPathRef(currentArg));
                            processed.add(i);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < info.arguments.length; i++) {
            if (!processed.contains(i)) {
                info.currentArg = info.arguments[i];
                info.currentArgIndex = i;
                if (!processArgument(info)) {
                    return processArgumentAsCommonStyleProperty(info);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            case NAME: {
                break;
            }
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    NPairElement p = info.currentArg.asPair().get();
                    String sid = HUtils.uid(p.key().asStringValue().get());
                    switch (sid) {
                        case NDocPropName.TRANSPARENT_COLOR: {
                            info.node.setProperty(sid, p.value());
                            return true;
                        }
                        case NDocPropName.VALUE:
                        case NDocPropName.FILE:
                        case "content":
                        case "src": {
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArgument(info);
    }


    @Override
    public NElement toElem(NDocNode item) {
        return ToElementHelper
                .of(item, engine())
                .inlineStringProp(NDocPropName.VALUE)
                .build();
    }
}
