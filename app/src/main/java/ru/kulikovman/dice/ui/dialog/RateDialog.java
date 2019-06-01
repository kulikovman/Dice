package ru.kulikovman.dice.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ru.kulikovman.dice.R;


public class RateDialog extends DialogFragment {

    private Listener listener;

    public interface Listener {
        void rateButtonPressed();

        void remindLaterButtonPressed();
    }

    public void setListener(RateDialog.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Подключаем слушатель
        try {
            listener = (Listener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement RateDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Получаем макет
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rateMessage = inflater.inflate(R.layout.dialog_rate_message, null);

        // Подключаем кнопки
        Button buttonRate = rateMessage.findViewById(R.id.button_rate);
        Button buttonAlreadyRated = rateMessage.findViewById(R.id.button_already_rated);
        Button buttonRemindLater = rateMessage.findViewById(R.id.button_remind_later);

        // Слушатели кнопок
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Закрываем диалог
                dismiss();

                // Сообщаем о нажатии
                listener.rateButtonPressed();

                // Открываем страницу приложения в маркете
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=ru.kulikovman.dices"));
                startActivity(intent);
            }
        });

        buttonAlreadyRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Закрываем диалог
                dismiss();

                // Сообщаем о нажатии
                listener.rateButtonPressed();
            }
        });

        buttonRemindLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Закрываем диалог
                dismiss();

                // Сообщаем о нажатии
                listener.remindLaterButtonPressed();
            }
        });

        // Формируем диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rateMessage);

        return builder.create();
    }
}
