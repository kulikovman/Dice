package ru.kulikovman.dice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import ru.kulikovman.dice.data.model.Cube;
import ru.kulikovman.dice.databinding.FragmentBoardBinding;
import ru.kulikovman.dice.db.model.Settings;
import ru.kulikovman.dice.ui.dialog.RateDialog;
import ru.kulikovman.dice.ui.view.CubeView;
import ru.kulikovman.dice.ui.view.ShadowView;
import ru.kulikovman.dice.util.Calculations;
import ru.kulikovman.dice.util.ShakeDetector;
import ru.kulikovman.dice.util.SoundManager;

public class BoardFragment extends Fragment {

    private FragmentBoardBinding binding;
    private MainActivity activity;
    private CubesViewModel model;

    private Settings settings;
    private Calculations calculations;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(CubesViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (MainActivity) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
        initUI();
    }

    private void init() {
        // Обновление темы оформления
        activity.updateTheme();

        // Режим засыпания
        activity.updateScreenState();

        // Подключение детектора тряски устройства
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                // Действие при встряхивании устройства
                Log.d("log", "Обнаружена тряска - " + count);
                throwCubes();
            }
        });

        // Обновление переменной в макете
        binding.setModel(this);
    }

    private void initUI() {
        // Показать результат последнего броска

        // Отрисовываем предыдущий бросок
        showLastThrowResult();

        // Отображение суммы кубиков и разделительных линий
        showTotal(settings.isShowTotal());
        showDividers(settings.isDivideScreen());

        // Долгое нажатие по экрану
        binding.buttonOfThrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPreviousThrowResult();
                return true;
            }
        });

        // Запрос отзыв
        showRateDialog();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Подключаем детектор тряски
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Отключаем детектор тряски
        sensorManager.unregisterListener(shakeDetector);
    }

    public void openSetting() {
        model.playSound(SoundManager.TOP_BUTTON_CLICK_SOUND);
        NavHostFragment.findNavController(BoardFragment.this).navigate(R.id.action_cubesOnBoardFragment_to_settingFragment);
    }

    private void showRateDialog() {
        // Если диалог еще не показывался и было сделано достаточно бросков
        if (model.isNeedShowRateDialog()) {
            RateDialog rateDialog = new RateDialog();
            rateDialog.setCancelable(false);

            // Обрабатываем выбор пользователя
            rateDialog.setListener(new RateDialog.Listener() {
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
            });

            rateDialog.show(this.getChildFragmentManager(), "rateDialog");
        }
    }

    public void throwCubes() {
        // Бросаем только если пауза закончилась
        if (!model.isReadyForThrow()) {
            return;
        }

        // Получаем кубики и размещаем на экране
        drawCubeOnScreen(model.getCubes());

        model.playSound(SoundManager.THROW_CUBES_SOUND);

        // Пауза после броска
        model.pauseAfterThrow();
    }

    // Получаем результат последнего боска и размещаем на экране
    public void showLastThrowResult() {
        List<Cube> cubes = model.getLastThrowResult();

        // Если список не пустой, то отрисовываем кубики
        if (cubes != null && cubes.size() > 0) {
            drawCubeOnScreen(model.getLastThrowResult());
        }
    }

    // Получаем результат предыдущего боска, относительно текущего
    public void showPreviousThrowResult() {
        final List<Cube> cubes = model.getPreviousThrowResult();

        // Если список не пустой, то делаем, что нужно и отрисовываем кубики
        if (cubes != null && cubes.size() > 0) {
            // Показываем иконку перемотки
            binding.rewindIcon.setVisibility(View.VISIBLE);

            model.playSound(SoundManager.TAPE_REWIND_SOUND);

            // Ждем когда проиграется звук перемотки
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Скрываем иконку перемотки
                    binding.rewindIcon.setVisibility(View.INVISIBLE);

                    // Отрисовываем кубики предыдущего броска
                    drawCubeOnScreen(cubes);
                }
            }, 600); // 600 - длительность звука перемотки
        }
    }

    private void drawCubeOnScreen(List<Cube> cubes) {
        // Очищаем доску и сумму
        clearBoards();
        int total = 0;

        // Подсчет суммы и размещение кубиков на экране
        for (Cube cube : cubes) {
            total += cube.getValue();

            binding.topBoard.addView(new CubeView(activity, cube, model.isDarkTheme()));
            binding.bottomBoard.addView(new ShadowView(activity, cube, model.isDarkTheme()));
        }

        // Установка суммы броска
        binding.total.setText(String.valueOf(total));
    }

    private void clearBoards() {
        binding.topBoard.removeAllViews();
        binding.bottomBoard.removeAllViews();
    }

    private void showTotal(boolean isShow) {
        binding.total.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void showDividers(boolean isShow) {
        binding.verticalLine.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.horizontalLine.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
