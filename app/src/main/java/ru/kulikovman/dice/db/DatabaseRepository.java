package ru.kulikovman.dice.db;


import java.util.List;

import ru.kulikovman.dice.data.Constant;
import ru.kulikovman.dice.data.model.Cube;
import ru.kulikovman.dice.db.model.Settings;
import ru.kulikovman.dice.db.model.ThrowResult;

public class DatabaseRepository {

    private final AppDatabase database;

    public DatabaseRepository(AppDatabase database) {
        this.database = database;
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
        database.throwResultDao().deleteOldestRecords(Constant.LIMIT_THROW_RESULTS);
    }

    public List<Cube> getLastThrowResult() {
        return database.throwResultDao().getAll().get(0).getCubes();
    }

    public List<ThrowResult> getThrowResultList() {
        return database.throwResultDao().getAll();
    }

    public List<Cube> getThrowResultByNumber(int throwResultNumber) {
        List<ThrowResult> results = database.throwResultDao().getAll();
        return results.size() > throwResultNumber ? results.get(throwResultNumber).getCubes() : null;
    }
}
