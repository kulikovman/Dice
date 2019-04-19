package ru.kulikovman.dice.db.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;
import ru.kulikovman.dice.data.model.CubeLite;

public class CubeLiteConverter {

    @TypeConverter
    public static List<CubeLite> fromString(String data) {
        Type listType = new TypeToken<List<CubeLite>>() {
        }.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String fromList(List<CubeLite> cubeLiteHistories) {
        Gson gson = new Gson();
        return gson.toJson(cubeLiteHistories);
    }
}
