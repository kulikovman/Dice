package ru.kulikovman.dice.di.module;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kulikovman.dice.util.ShakeDetector;

@Module(includes = ContextModule.class)
public class SensorModule {

    @Singleton
    @Provides
    ShakeDetector shakeDetector() {
        return new ShakeDetector();
    }

    @Singleton
    @Provides
    SensorManager sensorManager(Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Singleton
    @Provides
    Sensor accelerometer(SensorManager sensorManager) {
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
}
