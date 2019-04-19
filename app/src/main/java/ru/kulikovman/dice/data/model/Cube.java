package ru.kulikovman.dice.data.model;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import ru.kulikovman.dice.util.Utils;

public class Cube {

    // Параметры для вью
    private String kindOfCube;
    private int value;
    private int degrees;
    private int marginStart;
    private int marginTop;

    // Координаты центра кубика и его размеры
    private Point position;
    private int cubeSize;
    private int cubeViewSize;

    // Вершины кубика для проверки пересечений
    private List<Point> cubeTops;

    // Только то, что необходимо для вывода кубика на экран
    public Cube(String kindOfCube, int value, int degrees, int marginStart, int marginTop) {
        this.kindOfCube = kindOfCube;
        this.value = value;
        this.degrees = degrees;
        this.marginStart = marginStart;
        this.marginTop = marginTop;
    }

    // Конструктор с координатами вершин для проверки пересечений между кубиками
    public Cube(String kindOfCube, int value, int degrees, int cubeSize, int cubeViewSize, Point position) {
        this.kindOfCube = kindOfCube;
        this.value = value;
        this.degrees = degrees;
        this.position = position;
        this.cubeSize = cubeSize;
        this.cubeViewSize = cubeViewSize;

        // Расчет отступов
        calculateMargins();

        // Получение координат вершин кубика
        calculateCubeTops();
    }

    private void calculateMargins() {
        marginStart = position.x - cubeViewSize / 2;
        marginTop = position.y - cubeViewSize / 2;
    }

    private void calculateCubeTops() {
        cubeTops = new ArrayList<>();

        // Максимальные и минимальные координаты вершин
        int minX = position.x - cubeSize / 2;
        int maxX = position.x + cubeSize / 2;
        int minY = position.y - cubeSize / 2;
        int maxY = position.y + cubeSize / 2;

        // Начальные координаты вершин
        cubeTops.add(new Point(minX, minY)); // a - верхний левый угол
        cubeTops.add(new Point(maxX, minY)); // b - верхний правый угол
        cubeTops.add(new Point(maxX, maxY)); // c - нижний правый угол
        cubeTops.add(new Point(minX, maxY)); // d - нижний левый угол

        // Вращение полученных точек
        rotateTops();
    }

    private void rotateTops() {
        // Перевод угла из градусов в радианы
        double radians = Utils.getRadiansFromDegrees(degrees);

        // Поворот точек вокруг центра на заданный угол
        for (Point point : cubeTops) {
            int tempX = getXRotation(radians, point.x, point.y);
            int tempY = getYRotation(radians, point.x, point.y);

            point.x = tempX;
            point.y = tempY;
        }
    }

    private int getXRotation(double radians, int px, int py) {
        return (int) (position.x + (px - position.x) * Math.cos(radians) - (py - position.y) * Math.sin(radians));
    }

    private int getYRotation(double radians, int px, int py) {
        return (int) (position.y + (py - position.y) * Math.cos(radians) + (px - position.x) * Math.sin(radians));
    }

    public void setNewPosition(Point position) {
        this.position = position;

        // Расчет отступов
        calculateMargins();

        // Получение координат вершин кубика
        calculateCubeTops();
    }

    public String getKindOfCube() {
        return kindOfCube;
    }

    public int getValue() {
        return value;
    }

    public int getDegrees() {
        return degrees;
    }

    public int getMarginStart() {
        return marginStart;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public List<Point> getCubeTops() {
        return cubeTops;
    }

    public Point getPosition() {
        return position;
    }
}
