package ru.kulikovman.dice;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.fragment.NavHostFragment;
import ru.kulikovman.cubes.databinding.FragmentSettingBinding;
import ru.kulikovman.cubes.helper.sweet.SweetOnSeekBarChangeListener;
import ru.kulikovman.cubes.model.Calculation;
import ru.kulikovman.cubes.model.Settings;
import ru.kulikovman.cubes.view.CubeView;


public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private MainActivity activity;
    private DiceViewModel model;
    private Settings settings;
    private Calculation calculation;

    private List<CubeView> cubeViews;
    private int maxCubes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (MainActivity) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Получение вью модел
        model = ViewModelProviders.of(activity).get(DiceViewModel.class);
        settings = model.getSettings();
        calculation = model.getCalculations();

        // Подключение звука
        SoundManager.initialize();

        initCubeList();
        restoreSettings();
        initUI();

        // Обновление переменной в макете
        binding.setModel(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Сохранение настроек
        model.saveSettings();
    }

    private void initCubeList() {
        cubeViews = new ArrayList<>();
        cubeViews.add(binding.whiteCube);
        cubeViews.add(binding.redCube);
        cubeViews.add(binding.blackCube);
    }

    private void restoreSettings() {
        // Обновление шкалы выбора количества кубиков
        updateNumberOfCubes();

        // Востанавливаем состояние элементов на экране
        binding.delay.setProgress(settings.getDelayAfterThrow());
        binding.doNotRollCubes.setChecked(settings.isNotRolling());
        binding.keepScreenOn.setChecked(settings.isKeepScreenOn());
        binding.showThrowAmount.setChecked(settings.isShownThrowAmount());
        binding.enableDarkTheme.setChecked(settings.isDarkTheme());
        binding.divideScreen.setChecked(settings.isDivideScreen());

        // Отмечаем сохраненный кубик
        String color = settings.getCubeType();
        for (CubeView cubeView : cubeViews) {
            if (cubeView.getCubeColor().equals(color)) {
                cubeView.setChooseMarker(true);
                break;
            }
        }
    }

    private void initUI() {
        // Выбор количества кубиков
        binding.cubes.setOnSeekBarChangeListener(new SweetOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Воспроизводим соответствующий звук
                SoundManager.get().playSound(SoundManager.SEEKBAR_CLICK_SOUND);

                // Сохраняем состояние
                String numberOfCubes = getString(R.string.label_number_of_cubes, progress + 1, maxCubes);
                binding.numberOfCubes.setText(numberOfCubes);
                settings.setNumberOfCubes(progress + 1);
            }
        });

        // Выбор задержки
        binding.delay.setOnSeekBarChangeListener(new SweetOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Воспроизводим соответствующий звук
                SoundManager.get().playSound(SoundManager.SEEKBAR_CLICK_SOUND);

                // Сохраняем состояние
                settings.setDelayAfterThrow(progress);
            }
        });

        // Отключение разбрасывания кубиков
        binding.doNotRollCubes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Воспроизводим соответствующий звук
                SoundManager.get().playSound(SoundManager.SWITCH_CLICK_SOUND);

                // Сохраняем состояние
                settings.setNotRolling(isChecked);

                // Обновление шкалы выбора количества кубиков
                updateNumberOfCubes();
            }
        });

        // Блокировка спящего режима
        binding.keepScreenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Воспроизводим соответствующий звук
                SoundManager.get().playSound(SoundManager.SWITCH_CLICK_SOUND);

                // Сохраняем состояние
                settings.setKeepScreenOn(isChecked);
            }
        });

        // Отображения суммы броска
        binding.showThrowAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Воспроизводим соответствующий звук
                SoundManager.get().playSound(SoundManager.SWITCH_CLICK_SOUND);

                // Сохраняем состояние
                settings.setShownThrowAmount(isChecked);
            }
        });

        // Деления экрана
        binding.divideScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Воспроизводим соответствующий звук
                SoundManager.get().playSound(SoundManager.SWITCH_CLICK_SOUND);

                // Сохраняем состояние
                settings.setDivideScreen(isChecked);
            }
        });

        // Темная тема
        binding.enableDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Воспроизводим соответствующий звук
                SoundManager.get().playSound(SoundManager.SWITCH_CLICK_SOUND);

                // Сохраняем состояние
                settings.setDarkTheme(isChecked);

                // Применение темной/светлой темы
                activity.changeTheme();
            }
        });
    }

    private void updateNumberOfCubes() {
        // Максимальное количество кубиков в зависимости от режима разброса
        maxCubes = settings.isNotRolling() ? calculation.getMaxOrderedCubes() : calculation.getMaxRolledCubes();

        // Устанавливаем максимум
        binding.cubes.setMax(maxCubes - 1);

        // Если сохраненное начение больше максимального
        if (settings.getNumberOfCubes() > maxCubes - 1) {
            settings.setNumberOfCubes(maxCubes);
        }

        // Устанавливаем текущий прогресс
        binding.cubes.setProgress(settings.getNumberOfCubes() - 1);
        String numberOfCubes = getString(R.string.label_number_of_cubes, settings.getNumberOfCubes(), maxCubes);
        binding.numberOfCubes.setText(numberOfCubes);
    }

    public void clickComeBackButton() {
        SoundManager.get().playSound(SoundManager.TOP_BUTTON_CLICK_SOUND);

        NavHostFragment.findNavController(this).popBackStack();
    }

    public void clickChooseCube(View view) {
        // Сначала снимаем все галки
        for (CubeView cubeView : cubeViews) {
            cubeView.setChooseMarker(false);
        }

        // Потом ставим галку на выбранном кубике
        CubeView cubeView = (CubeView) view;
        cubeView.setChooseMarker(true);

        // Воспроизводим соответствующий звук
        SoundManager.get().playSound(SoundManager.CUBE_CLICK_SOUND);

        // Сохраняем выбранный цвет
        settings.setCubeType(cubeView.getCubeColor());
    }
}
