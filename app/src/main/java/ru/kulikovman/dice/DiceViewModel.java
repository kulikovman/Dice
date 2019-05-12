package ru.kulikovman.dice;

import android.arch.lifecycle.ViewModel;

import ru.kulikovman.cubes.model.Calculation;
import ru.kulikovman.cubes.model.Settings;


public class DiceViewModel extends ViewModel {

    private DataRepository repository;
    private Calculation calculation;
    private Settings settings;

    public DiceViewModel() {
        super();

        // Получение репозитория
        repository = DataRepository.get();
    }

    public DataRepository getRepository() {
        return repository;
    }

    public Settings getSettings() {
        if (settings == null) {
            settings = repository.getSettings();
        }

        return settings;
    }

    public Calculation getCalculation() {
        if (calculation == null) {
            calculation = new Calculation(App.getContext().getResources());
        }

        return calculation;
    }

    void saveSettings() {
        repository.saveSettings(settings);
    }
}
