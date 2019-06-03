package ru.kulikovman.dice.ui.view;

import android.content.Context;
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
import ru.kulikovman.dice.databinding.ViewShadowBinding;

public class ShadowView extends FrameLayout {

    private ViewShadowBinding binding;
    private Context context;

    private Kind kind;
    public int angle;
    public int marginStart;
    public int marginTop;

    boolean isDarkTheme;

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
    public ShadowView(@NonNull Context context, Cube cube, boolean isDarkTheme) {
        super(context);

        // Инициализация
        this.isDarkTheme = isDarkTheme;
        init(context);

        if (!isInEditMode()) {
            // Подключение биндинга
            binding = DataBindingUtil.bind((findViewById(R.id.shadow_view_container)));

            // Ставим значения
            setCube(cube);
        }
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_shadow, this);
    }

    public void setCube(Cube cube) {
        kind = Kind.valueOf(cube.getKindOfCube());
        angle = cube.getDegrees();
        marginStart = cube.getMarginStart();
        marginTop = cube.getMarginTop();

        // Отрисовка тени
        drawShadow();
    }

    private void drawShadow() {
        // Назначение тени в соответствии с цветом
        String theme = isDarkTheme ? "dark" : "lite";
        String skinName = kind.name().toLowerCase();
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
