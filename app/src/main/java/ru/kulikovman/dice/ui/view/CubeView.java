package ru.kulikovman.dice.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import ru.kulikovman.dice.R;
import ru.kulikovman.dice.data.Kind;
import ru.kulikovman.dice.data.model.Cube;
import ru.kulikovman.dice.databinding.ViewCubeBinding;

public class CubeView extends FrameLayout {

    private ViewCubeBinding binding;
    private Context context;

    private Kind kind;
    private int value;
    public int angle;
    public boolean isShadow;
    public boolean isSelected;
    public int marginStart;
    public int marginTop;

    boolean isDarkTheme;

    public CubeView(@NonNull Context context) {
        super(context);
    }

    // Конструкторы для создания кубика через макет
    public CubeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CubeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public CubeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    // Конструктор для генерации кубика через код
    public CubeView(@NonNull Context context, Cube cube, boolean isDarkTheme) {
        super(context);

        // Инициализация
        this.isDarkTheme = isDarkTheme;
        init(context);

        if (!isInEditMode()) {
            // Подключение биндинга
            binding = DataBindingUtil.bind((findViewById(R.id.cube_view_container)));

            // Ставим значения
            setCube(cube);
        }
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_cube, this);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // Инициализация
        init(context);

        // Получение значений из аттрибутов вью
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CubeView, defStyleAttr, defStyleRes);
        value = a.getInt(R.styleable.CubeView_value, 1);
        angle = a.getInt(R.styleable.CubeView_angle, 0);
        isShadow = a.getBoolean(R.styleable.CubeView_shadow, false);
        isSelected = a.getBoolean(R.styleable.CubeView_selected, false);
        kind = Kind.values()[a.getInt(R.styleable.CubeView_type, 0)];
        a.recycle();

        if (!isInEditMode()) {
            // Подключение биндинга
            binding = DataBindingUtil.bind((findViewById(R.id.cube_view_container)));

            // Отрисовка кубика
            drawCube();
        }
    }

    public void setCube(Cube cube) {
        kind = Kind.valueOf(cube.getKindOfCube());
        value = cube.getValue();
        angle = cube.getDegrees();
        marginStart = cube.getMarginStart();
        marginTop = cube.getMarginTop();

        // Отрисовка кубика
        drawCube();
    }

    private void drawCube() {
        // Назначение картинок в соответствии с цветом
        String theme = isDarkTheme ? "dark" : "lite";
        String skinName = kind.name().toLowerCase();
        binding.cube.setImageResource(getDrawableIdByName(skinName + "_" + theme + "_" + value));

        // Показываем тень, если указана
        // Это собственная тень кубика, для отображения через макет
        // Для кубиков на поле отображаться не должна, там свои тени в другом слое
        if (isShadow) {
            binding.shadow.setImageResource(getDrawableIdByName(skinName + "_" + theme + "_0")); // 0 - тень
            binding.shadow.setVisibility(VISIBLE);
        }

        // Показываем маркер, если есть
        binding.selection.setVisibility(isSelected ? VISIBLE : INVISIBLE);

        // Обновление переменной в макете
        binding.setModel(this);
    }

    private int getDrawableIdByName(String resourceName) {
        return getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    public void setChooseMarker(boolean isSelected) {
        // Показываем маркер
        this.isSelected = isSelected;

        // Показываем маркер, если есть
        binding.selection.setVisibility(isSelected ? VISIBLE : INVISIBLE);
    }

    public String getCubeColor() {
        return kind.name();
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
