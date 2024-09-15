package net.thevpc.halfa.engine.control;

import net.thevpc.halfa.spi.HNodeFlowControlProcessor;

import java.util.*;

public class IfHNodeFlowControlProcessorFactory {
    private List<HNodeFlowControlProcessor> map = new ArrayList<>();

    public IfHNodeFlowControlProcessorFactory() {
        ServiceLoader<HNodeFlowControlProcessor> serviceLoader = ServiceLoader.load(HNodeFlowControlProcessor.class);
        for (HNodeFlowControlProcessor element3DPrimitiveBuilder : serviceLoader) {
            register(element3DPrimitiveBuilder);
        }
    }

    void register(HNodeFlowControlProcessor f) {
        map.add(f);
    }

    public List<HNodeFlowControlProcessor> list() {
        return map;
    }

}
