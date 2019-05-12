package ru.kulikovman.dice.di.module;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kulikovman.dice.db.model.Settings;
import ru.kulikovman.dice.util.Calculations;
import ru.kulikovman.dice.util.CubeGenerator;
import ru.kulikovman.dice.util.Intersection;

@Module(includes = ContextModule.class)
public class CubeUtilModule {

    @Singleton
    @Provides
    Calculations calculations(Resources resources) {
        return new Calculations(resources);
    }

    @Provides
    Resources resources(Context context) {
        return context.getResources();
    }

    @Singleton
    @Provides
    CubeGenerator cubeGenerator(Calculations calculations, Settings settings, Intersection intersection) {
        return new CubeGenerator(calculations, settings, intersection);
    }

    @Singleton
    @Provides
    Intersection intersection(Calculations calculations) {
        return new Intersection(calculations);
    }

}
