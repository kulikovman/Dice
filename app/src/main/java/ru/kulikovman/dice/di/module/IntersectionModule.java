package ru.kulikovman.dice.di.module;

import dagger.Provides;
import ru.kulikovman.dice.util.Intersection;

public class IntersectionModule {

    @Provides
    Intersection provideIntersection() {
        return new Intersection();
    }
}
