package ru.kulikovman.dice.di.module;

import dagger.Module;
import dagger.Provides;
import ru.kulikovman.dice.util.CubeGenerator;

@Module
public class CubeGeneratorModule {

    @Provides
    CubeGenerator provideCubeGenerator() {
        return new CubeGenerator();
    }
}
