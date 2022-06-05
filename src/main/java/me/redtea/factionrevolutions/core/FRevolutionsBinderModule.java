package me.redtea.factionrevolutions.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FRevolutionsBinderModule extends AbstractModule {

    private final FRevolutions plugin;

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        bind(FRevolutions.class).toInstance(plugin);
    }
}
