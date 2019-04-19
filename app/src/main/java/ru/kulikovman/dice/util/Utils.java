package ru.kulikovman.dice.util;

import java.util.Random;

public class Utils {

    // Конвертирует градусы в радианы
    public static double getRadiansFromDegrees(int angleInDegrees) {
        return angleInDegrees * Math.PI / 180;
    }

    // Случайное значение в заданных пределах
    public static int getRandomValueWithinLimits(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}
