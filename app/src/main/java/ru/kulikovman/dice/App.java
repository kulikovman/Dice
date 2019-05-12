package ru.kulikovman.dice;

import android.app.Application;

import ru.kulikovman.dice.di.AppComponent;
import ru.kulikovman.dice.di.module.ContextModule;


public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        // Подключаем Dagger
        component = DaggerAppComponent.builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
