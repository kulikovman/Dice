package ru.kulikovman.dice.db;

import java.util.List;

import ru.kulikovman.cubes.model.Settings;
import ru.kulikovman.cubes.model.ThrowResult;

public class DatabaseRepository {

    private static DatabaseRepository instance;

    private final AppDatabase database;

    private DatabaseRepository() {
        database = App.getInstance().getDatabase();
    }

    public static DatabaseRepository get() {
        if (instance == null) {
            synchronized (DatabaseRepository.class) {
                if (instance == null) {
                    instance = new DatabaseRepository();
                }
            }
        }

        return instance;
    }

    public Settings getSettings() {
        // Получение настроек
        Settings settings = database.settingsDao().getById(0);
        if (settings == null) {
            settings = new Settings();
            database.settingsDao().insert(settings);
        }

        return settings;
    }

    public void saveSettings(Settings settings) {
        database.settingsDao().update(settings);
    }

    public void saveThrowResult(ThrowResult throwResult) {
        // Записываем результат броска и удаляем старые записи
        database.throwResultDao().insert(throwResult);
        database.throwResultDao().deleteOldestRecords(10);
    }

    public List<ThrowResult> getThrowResultList() {
        return database.throwResultDao().getAll();
    }
}
