package ru.kulikovman.dice;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;
import ru.kulikovman.dice.db.AppDatabase;


public class App extends Application {

    private static App instance;

    private AppDatabase database;

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Подключение Dagger
        component = DaggerAppComponent.create();

        // Подключение базы данных
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                //.allowMainThreadQueries() // разрешает операции в основном потоке
                .fallbackToDestructiveMigration() // обнуляет базу, если нет подходящей миграции
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static AppComponent getComponent() {
        return component;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
