package ru.kulikovman.dice.di.module;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kulikovman.dice.util.SoundManager;

@Module(includes = ContextModule.class)
public class SoundModule {

    @Singleton
    @Provides
    SoundManager soundManager(Context context, SoundPool soundPool) {
        return new SoundManager(context, soundPool);
    }

    @Provides
    SoundPool soundPool(AudioAttributes audioAttributes) {
        return new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();
    }

    @Provides
    AudioAttributes audioAttributes() {
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
    }
}
