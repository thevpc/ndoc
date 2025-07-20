package net.thevpc.ndoc.extension.shapes2d.arrow;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
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
                    switch (NDocUtils.uid(p.key().asStringValue().get())) {
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
        NDocProp width = item.getProperty(NDocPropName.WIDTH).orNull();
        NDocProp height = item.getProperty(NDocPropName.HEIGHT).orNull();
        NDocProp base = item.getProperty("base").orNull();
        NDocProp hat = item.getProperty("hat").orNull();

        return ToElementHelper.of(
                item,
                engine()
        ).addChildren(
                width == null ? null : NElement.ofPair("width", NDocUtils.toElement(width.getValue())),
                height == null ? null : NElement.ofPair("height", NDocUtils.toElement(height.getValue())),
                base == null ? null : NElement.ofPair("base", NDocUtils.toElement(base.getValue())),
                hat == null ? null : NElement.ofPair("hat", NDocUtils.toElement(hat.getValue()))
        ).build();
    }

}
