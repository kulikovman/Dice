package ru.kulikovman.dice.ui.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import ru.kulikovman.cubes.CubesViewModel;
import ru.kulikovman.cubes.MainActivity;
import ru.kulikovman.cubes.R;
import ru.kulikovman.cubes.data.CubeType;
import ru.kulikovman.cubes.databinding.ViewShadowBinding;
import ru.kulikovman.cubes.model.Cube;
import ru.kulikovman.cubes.model.CubeLite;

public class ShadowView  extends FrameLayout {

    private ViewShadowBinding binding;
    private Context context;

    private CubeType cubeType;
    public int angle;
    public int marginStart;
    public int marginTop;

    public ShadowView(@NonNull Context context) {
        super(context);
    }

    public ShadowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShadowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Конструктор для генерации тени через код
    public ShadowView(@NonNull Context context, Cube cube) {
        super(context);

        // Инициализация
        init(context);

        if (!isInEditMode()) {
            // Подключение биндинга
            binding = DataBindingUtil.bind((findViewById(R.id.shadow_view_container)));

            // Ставим значения
            setCube(cube);
        }
    }

    // Конструктор для генерации тени через код
    public ShadowView(@NonNull Context context, CubeLite cubeLite) {
        super(context);

        // Инициализация
        init(context);

        if (!isInEditMode()) {
            // Подключение биндинга
            binding = DataBindingUtil.bind((findViewById(R.id.shadow_view_container)));

            // Ставим значения
            setCube(cubeLite);
        }
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_shadow, this);
    }

    public void setCube(Cube cube) {
        cubeType = cube.getCubeType();
        angle = cube.getDegrees();
        marginStart = cube.getMarginStart();
        marginTop = cube.getMarginTop();

        // Отрисовка тени
        drawShadow();
    }

    public void setCube(CubeLite cubeLite) {
        cubeType = CubeType.valueOf(cubeLite.getSkin());
        angle = cubeLite.getAngle();
        marginStart = cubeLite.getMarginStart();
        marginTop = cubeLite.getMarginTop();

        // Отрисовка тени
        drawShadow();
    }

    private void drawShadow() {
        // Назначение тени в соответствии с цветом
        CubesViewModel model = ViewModelProviders.of((MainActivity) context).get(CubesViewModel.class);

        String theme = model.getSettings().isDarkTheme() ? "dark" : "lite";
        String skinName = cubeType.name().toLowerCase();
        binding.shadow.setImageResource(getDrawableIdByName(skinName + "_" + theme + "_0")); // 0 - тень

        // Обновление переменной в макете
        binding.setModel(this);
    }

    private int getDrawableIdByName(String resourceName) {
        return getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    @BindingAdapter({"android:layout_marginStart"})
    public static void setMarginStart(View view, int margin) {
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = margin;
        view.setLayoutParams(params);
    }

    @BindingAdapter({"android:layout_marginTop"})
    public static void setMarginTop(View view, int margin) {
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.topMargin = margin;
        view.setLayoutParams(params);
    }
}
