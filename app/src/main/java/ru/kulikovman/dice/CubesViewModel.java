package ru.kulikovman.dice;

import android.content.res.Resources;
import android.os.Handler;

import java.util.List;

import androidx.lifecycle.ViewModel;
import ru.kulikovman.dice.data.Constant;
import ru.kulikovman.dice.data.model.Cube;
import ru.kulikovman.dice.db.DatabaseRepository;
import ru.kulikovman.dice.db.model.Settings;
import ru.kulikovman.dice.db.model.ThrowResult;
import ru.kulikovman.dice.util.Calculations;
import ru.kulikovman.dice.util.CubeGenerator;
import ru.kulikovman.dice.util.SoundManager;


public class CubesViewModel extends ViewModel {

    private DatabaseRepository databaseRepository;
    private Calculations calculations;
    private Settings settings;
    private SoundManager soundManager;
    private Resources resources;
    private CubeGenerator cubeGenerator;

    private boolean isReadyForThrow;

    private int currentThrowResult = 0;
    private List<ThrowResult> throwResults;

    public CubesViewModel(DatabaseRepository databaseRepository, Calculations calculations, Settings settings) {
        this.databaseRepository = databaseRepository;
        this.calculations = calculations;
        this.settings = settings;

        init();
    }

    private void init() {

    }

    public boolean isNeedShowRateDialog() {
        return !settings.isRated() && settings.getNumberOfThrow() > Constant.LIMIT_OF_THROW;
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

    public void playSound(int sound) {
        soundManager.playSound(sound);
    }

    public void saveSettingsToDatabase() {
        databaseRepository.saveSettings(settings);
    }

    public int getMaxCubes() {
        // Максимальное количество кубиков в зависимости от режима разброса
        int maxCubes = settings.isNotRolling() ? calculations.getMaxOrderedCubes() : calculations.getMaxRollingCubes();

        // Текущее значение не должно быть больше максимального
        if (settings.getNumberOfCubes() > maxCubes - 1) {
            settings.setNumberOfCubes(maxCubes);
        }

        return maxCubes;
    }

    public boolean isReadyForThrow() {
        return isReadyForThrow;
    }

    public List<Cube> getCubes() {
        clearThrowResult();

        // Получаем список кубиков
        List<Cube> cubes = cubeGenerator.getListOfCubes();

        // Засчитываем бросок
        settings.setNumberOfThrow(settings.getNumberOfThrow() + 1);

        // Сохраняем результаты броска в базу
        databaseRepository.saveThrowResult(new ThrowResult(cubes));

        return cubes;
    }

    public void pauseAfterThrow() {
        // Задержка после броска
        isReadyForThrow = false;

        // Задержка после броска
        int[] delays = resources.getIntArray(R.array.delay_after_throw);
        int delayAfterThrow = delays[settings.getDelayAfterThrow()];

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isReadyForThrow = true;
            }
        }, delayAfterThrow);
    }

    public List<Cube> getLastThrowResult() {
        clearThrowResult();

        return databaseRepository.getLastThrowResult();
    }

    public List<Cube> getPreviousThrowResult() {
        // Получаем весь список предыдущих бросков (10 штук)
        if (throwResults == null) {
            throwResults = databaseRepository.getThrowResultList();
        }

        // Если элемент не выходит за границы и присутствует в списке
        if (++currentThrowResult < Constant.LIMIT_THROW_RESULTS && throwResults.size() > currentThrowResult) {
            return throwResults.get(currentThrowResult).getCubes();
        } else {
            return null;
        }
    }

    private void clearThrowResult() {
        currentThrowResult = 0;
        throwResults = null;
    }
}
