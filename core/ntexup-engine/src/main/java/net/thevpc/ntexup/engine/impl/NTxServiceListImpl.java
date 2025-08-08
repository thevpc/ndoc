package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.engine.NTxDependencyLoadedListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class NTxServiceListImpl<T> implements NTxDependencyLoadedListener {
    private Class<T> serviceType;
    private Set<Class<T>> alreadyLoaded = new HashSet<>();
    private Set<Class<T>> userLoaded = new HashSet<>();
    protected DefaultNTxEngine engine;

    public NTxServiceListImpl(Class<T> serviceType, DefaultNTxEngine engine) {
        this.engine = engine;
        this.serviceType = serviceType;
        engine.addNTxDependencyLoadedListener(this);
    }

    public void build(){
        List<T> newServices = engine.loadServices(serviceType);
        for (T newService : newServices) {
            if (!alreadyLoaded.contains(newService.getClass())) {
                alreadyLoaded.add(serviceType);
                onNewService(newService);
            }
        }
    }

    @Override
    public void onLoadDependencyLoaded() {
        build();
    }

    protected abstract void onNewService(T newService);
}
