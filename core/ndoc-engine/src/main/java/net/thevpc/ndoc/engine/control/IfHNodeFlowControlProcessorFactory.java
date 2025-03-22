package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.spi.NDocNodeFlowControlProcessor;

import java.util.*;

public class IfHNodeFlowControlProcessorFactory {
    private List<NDocNodeFlowControlProcessor> map = new ArrayList<>();

    public IfHNodeFlowControlProcessorFactory() {
        ServiceLoader<NDocNodeFlowControlProcessor> serviceLoader = ServiceLoader.load(NDocNodeFlowControlProcessor.class);
        for (NDocNodeFlowControlProcessor element3DPrimitiveBuilder : serviceLoader) {
            register(element3DPrimitiveBuilder);
        }
    }

    void register(NDocNodeFlowControlProcessor f) {
        map.add(f);
    }

    public List<NDocNodeFlowControlProcessor> list() {
        return map;
    }

}
