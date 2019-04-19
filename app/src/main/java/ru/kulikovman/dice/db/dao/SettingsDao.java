package ru.kulikovman.dice.db.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import ru.kulikovman.dice.db.model.Settings;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM Settings")
    List<Settings> getAll();

    @Query("SELECT * FROM Settings WHERE id = :id")
    Settings getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Settings settings);

    @Update
    void update(Settings settings);

    @Delete
    void delete(Settings settings);

}
