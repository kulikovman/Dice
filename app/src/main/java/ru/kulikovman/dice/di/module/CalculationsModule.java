package ru.kulikovman.dice.di.module;

import dagger.Module;
import dagger.Provides;
import ru.kulikovman.dice.util.Calculations;

@Module
public class CalculationsModule {

    @Provides
    Calculations provideCalculations() {
        return new Calculations();
    }
}
