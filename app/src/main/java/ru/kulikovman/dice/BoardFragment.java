package ru.kulikovman.dice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

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
import ru.kulikovman.dice.util.SoundManager;

public class BoardFragment extends Fragment {

    private FragmentBoardBinding binding;
    private DiceViewModel model;

    private Settings settings;

    private SoundManager soundManager;

    private boolean isReadyForThrow;
    private int delayAfterThrow;

    private int throwOnScreen = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(DiceViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Делаем всякие штуки при первом входе...



        // Задержка после броска
        int[] delays = getResources().getIntArray(R.array.delay_after_throw);
        delayAfterThrow = delays[settings.getDelayAfterThrow()];

        // Показать результат последнего броска



        // Обновление переменной в макете
        binding.setModel(this);

        // Готовность к броску
        isReadyForThrow = true;

        // Запрос отзыв
        showRateDialog();

    }

    private void showRateDialog() {
        // Если диалог еще не показывался и было сделано достаточно бросков
        if (model.isNeedShowRateDialog()) {
            RateDialog rateDialog = new RateDialog();
            rateDialog.setCancelable(false);
            rateDialog.show(this.getChildFragmentManager(), "rateDialog");

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
        }
    }



    // Показать результат последнего броска
    private void showLastResult() {

    }

    // Показать предыдущий бросок из истории бросков
    public void showLastResult() {

    }


    // Сделать бросок кубиков
    public void throwCubes() {
        // Если время задержки не прошло, то выходим
        if (!isReadyForThrow) {
            return;
        }

        // Подготовка к броску
        throwOnScreen = 0;
        clearBoards();
        int total = 0;

        for (Cube cube : model.getCubes()) {
            // Считаем сумму кубиков
            total += cube.getValue();

            // Размещаем вью на экране
            binding.topBoard.addView(new CubeView(getActivity(), cube, model.isDarkTheme()));
            binding.bottomBoard.addView(new ShadowView(getActivity(), cube, model.isDarkTheme()));
        }

        // Звук броска
        soundManager.playSound(SoundManager.THROW_CUBES_SOUND);

        // Сумма броска
        binding.total.setText(String.valueOf(total));

        // Задержка после броска
        isReadyForThrow = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isReadyForThrow = true;
            }
        }, delayAfterThrow);
    }

    private void clearBoards() {
        binding.topBoard.removeAllViews();
        binding.bottomBoard.removeAllViews();
    }

    public void openSetting() {
        soundManager.playSound(SoundManager.TOP_BUTTON_CLICK_SOUND);
        NavHostFragment.findNavController(BoardFragment.this).navigate(R.id.action_cubesOnBoardFragment_to_settingFragment);
    }










    private void showDividers(boolean isShow) {
        binding.verticalLine.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.horizontalLine.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void showTotal(boolean isShow) {
        binding.total.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }






    /*


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    private List<ThrowResult> throwResults;
    private boolean isReadyForThrow;

    private int resultOnScreen;
    public int total;


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
        calculations = model.getCalculations();

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



        // Засыпание экрана
        if (settings.isKeepScreenOn()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
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
        total = 0;

        // Размещаем кубики на доске + подсчет их суммы
        List<CubeLite> cubeLites = throwResults.get(rollResultNumber).getCubeLites();
        for (CubeLite cubeLite : cubeLites) {
            binding.topBoard.addView(new CubeView(activity, cubeLite));
            binding.bottomBoard.addView(new ShadowView(activity, cubeLite));
            total += cubeLite.getValue();
        }

        // Отображение суммы броска
        showThrowAmount();
    }

    private void showThrowAmount() {
        if (settings.isShownThrowAmount() && total != 0) {
            binding.throwAmount.setText(String.valueOf(total));
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
        total = 0;

        // Генирируем новые кубики
        List<Cube> cubes = CubeGenerator.get().getCubes(calculations, settings);

        for (Cube cube : cubes) {
            // Считаем сумму кубиков
            total += cube.getValue();

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
            throwResult.addCube(cube.getCubeLite());
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

    */
}
