package ru.kulikovman.dice;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.navigation.fragment.NavHostFragment;
import ru.kulikovman.cubes.databinding.FragmentBoardBinding;
import ru.kulikovman.cubes.dialog.RateDialog;
import ru.kulikovman.cubes.helper.CubeGenerator;
import ru.kulikovman.cubes.model.Calculation;
import ru.kulikovman.cubes.model.Cube;
import ru.kulikovman.cubes.model.CubeLite;
import ru.kulikovman.cubes.model.Settings;
import ru.kulikovman.cubes.model.ThrowResult;
import ru.kulikovman.cubes.view.CubeView;
import ru.kulikovman.cubes.view.ShadowView;


public class BoardFragment extends Fragment implements RateDialog.Listener {

    private static final int LIMIT_OF_THROW = 100; // После 100 бросков будут запрошен отзыв

    private FragmentBoardBinding binding;
    private MainActivity activity;
    private DiceViewModel model;
    public Settings settings;
    private Calculation calculation;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    private List<ThrowResult> throwResults;
    private boolean isReadyForThrow;
    private int delayAfterThrow;
    private int resultOnScreen;
    public int sumOfCubes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false);
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
        calculation = model.getCalculation();

        // Инициализация
        throwResults = new ArrayList<>();
        isReadyForThrow = true;

        // Подключение звука и ShakeDetector
        SoundManager.initialize();
        initShakeDetector();

        // Отрисовываем предыдущий бросок
        showLastThrowResult();

        // Применение настроек
        loadSettings();

        // Долгое нажатие по экрану
        binding.buttonOfThrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPreviousThrowResult();
                return true;
            }
        });

        // Показ диалога с оценкой
        showRateDialog();

        // Обновление переменной в макете
        binding.setModel(this);
    }

    private void loadSettings() {
        // Применение темы оформления
        activity.changeTheme();

        // Задержка после броска
        int[] delays = getResources().getIntArray(R.array.delay_after_throw);
        delayAfterThrow = delays[settings.getDelayAfterThrow()];

        // Засыпание экрана
        if (settings.isKeepScreenOn()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showRateDialog() {
        // Если диалог еще не показывался и было сделано достаточно бросков
        if (!settings.isRated() && settings.getNumberOfThrow() > LIMIT_OF_THROW) {
            RateDialog rateDialog = new RateDialog();
            rateDialog.setCancelable(false);
            rateDialog.show(this.getChildFragmentManager(), "rateDialog");
        }
    }

    @Override
    public void rateButtonPressed() {
        // Отмечаем, что приложение оценено
        settings.setRated(true);
    }

    @Override
    public void remindLaterButtonPressed() {
        // Сбрасываем счетчик, чтобы показать диалог позже
        settings.setNumberOfThrow(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Отключаем shakeDetector
        sensorManager.unregisterListener(shakeDetector);

        // Сохранение настроек
        model.saveSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Подключаем shakeDetector
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void initShakeDetector() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                // Действие при встряхивании устройства
                Log.d("log", "Обнаружена тряска - " + count);
                throwCubes();
            }
        });
    }

    public void openSetting() {
        SoundManager.get().playSound(SoundManager.TOP_BUTTON_CLICK_SOUND);
        NavHostFragment.findNavController(BoardFragment.this).navigate(R.id.action_cubesOnBoardFragment_to_settingFragment); // Здесь происходит ошибка!
    }

    public void showLastThrowResult() {
        // Номер текущего броска
        resultOnScreen = 0;

        // Получаем историю бросков
        throwResults.clear();
        throwResults = model.getRepository().getThrowResultList();

        if (throwResults.size() != 0) {
            // Отрисовываем последний бросок
            drawCubeFromHistory(resultOnScreen);
        }
    }

    public void showPreviousThrowResult() {
        // Получаем список последних бросков
        if (resultOnScreen == 0) {
            throwResults.clear();
            throwResults = model.getRepository().getThrowResultList();
        }

        // Номер предыдущего броска
        resultOnScreen++;

        // Если в истории есть броски и текущий бросок не последний в списке
        if (throwResults.size() != 0 && resultOnScreen < throwResults.size()) {
            // Показываем иконку перемотки
            binding.rewindIcon.setVisibility(View.VISIBLE);

            // Звук перемотки
            SoundManager.get().playSound(SoundManager.TAPE_REWIND_SOUND);

            // Ждем когда проиграется звук перемотки
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Скрываем иконку перемотки
                    binding.rewindIcon.setVisibility(View.INVISIBLE);

                    // Удаляем кубики с доски
                    clearBoards();

                    // Отрисовываем кубики предыдущего броска
                    drawCubeFromHistory(resultOnScreen);
                }
            }, 600); // 600 - длительность звука перемотки
        }
    }

    private void drawCubeFromHistory(int rollResultNumber) {
        // Сбрасываем сумму
        sumOfCubes = 0;

        // Размещаем кубики на доске + подсчет их суммы
        List<CubeLite> cubeLites = throwResults.get(rollResultNumber).getCubeLites();
        for (CubeLite cubeLite : cubeLites) {
            binding.topBoard.addView(new CubeView(activity, cubeLite));
            binding.bottomBoard.addView(new ShadowView(activity, cubeLite));
            sumOfCubes += cubeLite.getValue();
        }

        // Отображение суммы броска
        showThrowAmount();
    }

    private void showThrowAmount() {
        if (settings.isShownThrowAmount() && sumOfCubes != 0) {
            binding.throwAmount.setText(String.valueOf(sumOfCubes));
        } else {
            binding.throwAmount.setText(null);
        }
    }

    public void throwCubes() {
        // Если время задержки не прошло, то выходим
        if (!isReadyForThrow) {
            return;
        }

        // Подготовка к броску
        clearBoards();
        resultOnScreen = 0;
        sumOfCubes = 0;

        // Генирируем новые кубики
        List<Cube> cubes = CubeGenerator.get().getCubes(calculation, settings);

        for (Cube cube : cubes) {
            // Считаем сумму кубиков
            sumOfCubes += cube.getValue();

            // Создаем вью кубика + тень
            CubeView cubeView = new CubeView(activity, cube);
            ShadowView shadowView = new ShadowView(activity, cube);

            // Размещаем вью на экране
            binding.topBoard.addView(cubeView);
            binding.bottomBoard.addView(shadowView);
        }

        // Воспроизводим звук броска + отображение суммы
        SoundManager.get().playSound(SoundManager.THROW_CUBES_SOUND);
        showThrowAmount();

        // Засчитываем бросок
        settings.setNumberOfThrow(settings.getNumberOfThrow() + 1);

        // Сохраняем результаты текущего броска в базу
        ThrowResult throwResult = new ThrowResult();
        for (Cube cube : cubes) {
            throwResult.addCubeLite(cube.getCubeLite());
        }

        model.getRepository().saveThrowResult(throwResult);

        // Задержка после броска
        isReadyForThrow = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isReadyForThrow = true;
            }
        }, delayAfterThrow);

        Log.d("myLog", "---------------------------");
    }

    private void clearBoards() {
        binding.topBoard.removeAllViews();
        binding.bottomBoard.removeAllViews();
    }
}
