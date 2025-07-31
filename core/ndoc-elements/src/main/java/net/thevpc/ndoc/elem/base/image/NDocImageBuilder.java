/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.image;

import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.parser.ParseArgumentInfo;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.util.ToElementHelper;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;

import java.util.HashSet;
import java.util.Set;


/**
 * @author vpc
 */
public class NDocImageBuilder extends NDocNodeParserBase {

    public NDocImageBuilder() {
        super(false, NDocNodeType.IMAGE);
    }

    @Override
    protected boolean processArguments(ParseArgumentInfo info) {
        Set<Integer> processed = new HashSet<>();
        boolean found = false;
        for (int i = 0; i < info.getArguments().length; i++) {
            NElement currentArg = info.getArguments()[i];
            switch (currentArg.type()) {
                case DOUBLE_QUOTED_STRING:
                case SINGLE_QUOTED_STRING:
                case ANTI_QUOTED_STRING:
                case TRIPLE_DOUBLE_QUOTED_STRING:
                case TRIPLE_SINGLE_QUOTED_STRING:
                case TRIPLE_ANTI_QUOTED_STRING:
                case LINE_STRING:
                {
                    info.getNode().setProperty(NDocPropName.VALUE, NDocUtils.addCompilerDeclarationPath(currentArg, info.source()));
                    processed.add(i);
                    found = true;
                    break;
                }
                case PAIR: {
                    if (currentArg.isSimplePair()) {
                        NPairElement p = currentArg.asPair().get();
                        String sid = NDocUtils.uid(p.key().asStringValue().get());
                        switch (sid) {
                            case NDocPropName.VALUE:
                            case NDocPropName.FILE: {
                                info.getNode().setProperty(sid, info.getContext().asPathRef(p.value()));
                                processed.add(i);
                                found = true;
                                break;
                            }
                            case "content":
                            case "src": {
                                info.getNode().setProperty(NDocPropName.VALUE, info.getContext().asPathRef(p.value()));
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
            for (int i = 0; i < info.getArguments().length; i++) {
                if (!processed.contains(i)) {
                    NElement currentArg = info.getArguments()[i];
                    switch (currentArg.type()) {
                        case NAME: {
                            info.getNode().setProperty(NDocPropName.VALUE, info.getContext().asPathRef(currentArg));
                            processed.add(i);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < info.getArguments().length; i++) {
            if (!processed.contains(i)) {
                info.setCurrentArg(info.getArguments()[i]);
                info.setCurrentArgIndex(i);
                if (!processArgument(info)) {
                    return processArgumentAsCommonStyleProperty(info);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {

        switch (info.getCurrentArg().type()) {
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
                if (info.getCurrentArg().isSimplePair()) {
                    NPairElement p = info.getCurrentArg().asPair().get();
                    String sid = NDocUtils.uid(p.key().asStringValue().get());
                    switch (sid) {
                        case NDocPropName.TRANSPARENT_COLOR: {
                            info.getNode().setProperty(sid, p.value());
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
