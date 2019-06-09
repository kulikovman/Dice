package ru.kulikovman.dice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProviders;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private CubesViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = ViewModelProviders.of(this).get(CubesViewModel.class);

        init();
    }

    private void init() {
        // Кнопки громкости меняют громкость медиа
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void updateTheme() {
        // Обновляем тему оформления (темная или светлая)
        getDelegate().setLocalNightMode(model.isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void updateScreenState() {
        // Проверяем должен ли экран быть всегда активным
        if (model.getSettings().isKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
