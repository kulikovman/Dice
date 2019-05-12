package ru.kulikovman.dice.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.kulikovman.dice.di.module.RepositoryModule;

@Singleton
@Component(modules = {RepositoryModule.class})
public interface AppComponent {
}
