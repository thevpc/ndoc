package net.thevpc.ndoc.extension.shapes2d.arrow;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.format.ToTsonHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonPair;

/**
 *
 */
public class NDocArrowImpl extends NDocNodeParserBase {

    public NDocArrowImpl() {
        super(false, HNodeType.ARROW);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    TsonPair p = info.currentArg.toPair();
                    switch (HUtils.uid(p.key().stringValue())) {
                        case "width": {
                            info.node.setProperty(HPropName.WIDTH, p.value());
                            return true;
                        }
                        case "height": {
                            info.node.setProperty(HPropName.HEIGHT, p.value());
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
    public NElement toElem(HNode item) {
        HProp width = item.getProperty(HPropName.WIDTH).orNull();
        HProp height = item.getProperty(HPropName.HEIGHT).orNull();
        HProp base = item.getProperty("base").orNull();
        HProp hat = item.getProperty("hat").orNull();

        return ToTsonHelper.of(
                item,
                engine()
        ).addChildren(
                width == null ? null : Tson.ofPair("width", net.thevpc.ndoc.api.util.HUtils.toElement(width.getValue())),
                height == null ? null : Tson.ofPair("height", net.thevpc.ndoc.api.util.HUtils.toElement(height.getValue())),
                base == null ? null : Tson.ofPair("base", net.thevpc.ndoc.api.util.HUtils.toElement(base.getValue())),
                hat == null ? null : Tson.ofPair("hat", net.thevpc.ndoc.api.util.HUtils.toElement(base.getValue()))
        ).build();
    }

}
