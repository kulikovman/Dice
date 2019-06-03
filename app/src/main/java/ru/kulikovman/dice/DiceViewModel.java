package ru.kulikovman.dice;

import android.content.res.Resources;

import java.util.List;

import androidx.lifecycle.ViewModel;
import ru.kulikovman.dice.data.model.Cube;
import ru.kulikovman.dice.db.DatabaseRepository;
import ru.kulikovman.dice.db.model.Settings;
import ru.kulikovman.dice.db.model.ThrowResult;
import ru.kulikovman.dice.util.Calculations;
import ru.kulikovman.dice.util.CubeGenerator;


public class DiceViewModel extends ViewModel {

    private static final int LIMIT_OF_THROW = 300; // После 300 бросков будут запрошен отзыв

    private DatabaseRepository databaseRepository;
    private Calculations calculations;
    private Settings settings;
    //private Resources resources;
    private CubeGenerator cubeGenerator;

    public DiceViewModel(DatabaseRepository databaseRepository, Calculations calculations, Settings settings) {
        this.databaseRepository = databaseRepository;
        this.calculations = calculations;
        this.settings = settings;
    }

    public List<Cube> getCubes() {
        // Получаем список кубиков
        List<Cube> cubes = cubeGenerator.getListOfCubes();

        // Засчитываем бросок
        settings.setNumberOfThrow(settings.getNumberOfThrow() + 1);

        // Сохраняем результаты броска в базу
        databaseRepository.saveThrowResult(new ThrowResult(cubes));

        return cubes;
    }

    public boolean isNeedShowRateDialog() {
        return !settings.isRated() && settings.getNumberOfThrow() > LIMIT_OF_THROW;
    }

    public Calculations getCalculations() {
        return calculations;
    }

    public Settings getSettings() {
        return settings;
    }

    public boolean isDarkTheme() {
        return settings.isDarkTheme();
    }
}
