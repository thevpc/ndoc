package net.thevpc.ndoc.extension.shapes2d.arrow;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;


/**
 *
 */
public class NDocArrowImpl extends NDocNodeParserBase {

    public NDocArrowImpl() {
        super(false, NDocNodeType.ARROW);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    NPairElement p = info.currentArg.asPair().get();
                    switch (HUtils.uid(p.key().asStringValue().get())) {
                        case "width": {
                            info.node.setProperty(NDocPropName.WIDTH, p.value());
                            return true;
                        }
                        case "height": {
                            info.node.setProperty(NDocPropName.HEIGHT, p.value());
                            return true;
                        }
                        case "base": {
                            info.node.setProperty("base", p.value());
                            return true;
                        }
                        case "hat": {
                            info.node.setProperty("hat", p.value());
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
        HProp width = item.getProperty(NDocPropName.WIDTH).orNull();
        HProp height = item.getProperty(NDocPropName.HEIGHT).orNull();
        HProp base = item.getProperty("base").orNull();
        HProp hat = item.getProperty("hat").orNull();

        return ToElementHelper.of(
                item,
                engine()
        ).addChildren(
                width == null ? null : NElement.ofPair("width", net.thevpc.ndoc.api.util.HUtils.toElement(width.getValue())),
                height == null ? null : NElement.ofPair("height", net.thevpc.ndoc.api.util.HUtils.toElement(height.getValue())),
                base == null ? null : NElement.ofPair("base", net.thevpc.ndoc.api.util.HUtils.toElement(base.getValue())),
                hat == null ? null : NElement.ofPair("hat", net.thevpc.ndoc.api.util.HUtils.toElement(hat.getValue()))
        ).build();
    }

}
