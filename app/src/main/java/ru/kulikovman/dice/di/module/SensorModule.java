package ru.kulikovman.dice.di.module;

import dagger.Module;
import dagger.Provides;
import ru.kulikovman.dice.util.ShakeDetector;

@Module
public class SensorModule {

    @Provides
    ShakeDetector shakeDetector() {
        return new ShakeDetector();
    }
}
