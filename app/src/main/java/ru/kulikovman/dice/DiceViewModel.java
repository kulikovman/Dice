package ru.kulikovman.dice;

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

    /*public Settings getSettings() {
        if (settings == null) {
            settings = databaseRepository.getSettings();
        }

        return settings;
    }

    public Calculations getCalculations() {
        if (calculations == null) {
            calculations = new Calculation(App.getContext().getResources());
        }

        return calculations;
    }

    void saveSettings() {
        databaseRepository.saveSettings(settings);
    }*/
}
