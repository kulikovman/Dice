package ru.kulikovman.dice.di.module;

import android.content.Context;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import ru.kulikovman.dice.db.AppDatabase;

@Module(includes = {ContextModule.class})
public class DatabaseModule {

    @Singleton
    @Provides
    AppDatabase provideAppDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "database")
                //.allowMainThreadQueries() // разрешает операции в основном потоке
                .fallbackToDestructiveMigration() // обнуляет базу, если нет подходящей миграции
                .build();
    }

}
