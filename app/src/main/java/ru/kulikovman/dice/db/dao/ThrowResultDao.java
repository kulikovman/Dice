package ru.kulikovman.dice.db.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ru.kulikovman.dice.data.model.Cube;
import ru.kulikovman.dice.db.model.ThrowResult;

@Dao
public interface ThrowResultDao {

    // TODO: 08.06.2019 Сделать метод получения последнего элемента и количества записей
    // чтобы получать только последний результат броска

    // Возвращает количество элементов в базе
    //@Query("SELECT * FROM ThrowResult ORDER BY time DESC")
    int countEntries();

    // Возвращает последний добавленный элемент
    //@Query("SELECT * FROM ThrowResult ORDER BY time DESC")
    List<ThrowResult> getLast();

    // Возвращает список отсортированный по дате (по убыванию)
    @Query("SELECT * FROM ThrowResult ORDER BY time DESC")
    List<ThrowResult> getAll();

    // Если записей больше, чем limitRecords, то удаляет самые старые по времени
    @Query("DELETE FROM ThrowResult WHERE time IN (SELECT time FROM ThrowResult ORDER BY time DESC LIMIT -1 OFFSET :limitRecords)")
    void deleteOldestRecords(int limitRecords);

    @Insert()
    void insert(ThrowResult settings);

    @Update
    void update(ThrowResult settings);

    @Delete
    void delete(ThrowResult settings);
}
