package ru.kulikovman.dice.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import ru.kulikovman.dice.db.converter.CubeLiteConverter;
import ru.kulikovman.dice.db.dao.SettingsDao;
import ru.kulikovman.dice.db.dao.ThrowResultDao;
import ru.kulikovman.dice.db.model.Settings;
import ru.kulikovman.dice.db.model.ThrowResult;


@Database(entities = {Settings.class, ThrowResult.class}, version = 7)
@TypeConverters(CubeLiteConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SettingsDao settingsDao();

    public abstract ThrowResultDao throwResultDao();


    // Пустая миграция, при повышении версии без изменений в схеме базы
    /*static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };*/

    // Добавление в настройки маркера темной/светлой темы
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE settings ADD COLUMN isDarkTheme INTEGER NOT NULL DEFAULT 0");
        }
    };

    // Добавление поля с информацией о разбросе кубиков по полю
    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE settings ADD COLUMN isNotRolling INTEGER NOT NULL DEFAULT 0");
        }
    };
}
