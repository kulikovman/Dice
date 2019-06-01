package ru.kulikovman.dice.util;

import android.graphics.Point;

import ru.kulikovman.dice.data.model.Area;
import ru.kulikovman.dice.data.model.Cube;

public class Intersection {

    private Calculations calculations;

    public Intersection(Calculations calculations) {
        this.calculations = calculations;
    }

    // Проверка пересечения кубика с указанной областью
    public boolean withArea(Area area, int cubeOuterRadius, Point position) {
        return position.x > area.getMinX() - cubeOuterRadius && position.x < area.getMaxX() + cubeOuterRadius &&
                position.y > area.getMinY() - cubeOuterRadius && position.y < area.getMaxY() + cubeOuterRadius;
    }

    // Проверка пересечения кубика с кубиком
    public boolean betweenCubes(Cube c1, Cube c2) {
        // Расчет растояния между центрами кубиков
        int distance = (int) Math.sqrt((Math.pow(Math.abs(c1.getPosition().x - c2.getPosition().x), 2) + Math.pow(Math.abs(c1.getPosition().y - c2.getPosition().y), 2)));

        // ЭТАП 1: проверка по внешним радиусам
        // Если больше суммы внешних радиусов, то все ок
        if (distance > calculations.getCubeOuterRadius() * 2) {
            return false;
        }

        // ЭТАП 2: проверка по внутренним радиусам
        // Если меньше суммы радиусов, то кубики 100% пересекаются
        if (distance < calculations.getCubeInnerRadius() * 2) {
            return true;
        }

        // ЭТАП 3: проверка пересечения вершин и кубиков
        // Первый кубик и точки второго кубика
        for (Point point : c2.getCubeTops()) {
            if (isPointInsideCube(c1, point)) {
                return true;
            }
        }

        // Второй кубик и точки первого кубика
        for (Point point : c1.getCubeTops()) {
            if (isPointInsideCube(c2, point)) {
                return true;
            }
        }

        // Кубики не пересекаются
        return false;
    }

    private boolean isPointInsideCube(Cube cube, Point point) {
        // Делим куб на два треугольника и проверяем принадлежность
        // точек одного куба к треугольникам другого

        // Обозначение вершин куба
        // a - b
        // | \ |
        // d - c

        int pX = point.x;
        int pY = point.y;

        int ax = cube.getCubeTops().get(0).x;
        int bx = cube.getCubeTops().get(1).x;
        int cx = cube.getCubeTops().get(2).x;
        int dx = cube.getCubeTops().get(3).x;

        int ay = cube.getCubeTops().get(0).y;
        int by = cube.getCubeTops().get(1).y;
        int cy = cube.getCubeTops().get(2).y;
        int dy = cube.getCubeTops().get(3).y;

        // Треугольник ABC
        int ab = (ax - pX) * (by - ay) - (bx - ax) * (ay - pY);
        int bc = (bx - pX) * (cy - by) - (cx - bx) * (by - pY);
        int ca = (cx - pX) * (ay - cy) - (ax - cx) * (cy - pY);

        if ((ab >= 0 && bc >= 0 && ca >= 0) || (ab <= 0 && bc <= 0 && ca <= 0)) {
            //Log.d("myLog", "Точка внутри ABC!");
            return true;
        }

        // Треугольник ACD
        int ac = (ax - pX) * (cy - ay) - (cx - ax) * (ay - pY);
        int cd = (cx - pX) * (dy - cy) - (dx - cx) * (cy - pY);
        int da = (dx - pX) * (ay - dy) - (ax - dx) * (dy - pY);

        if ((ac >= 0 && cd >= 0 && da >= 0) || (ac <= 0 && cd <= 0 && da <= 0)) {
            //Log.d("myLog", "Точка внутри ACD!");
            return true;
        }

        // Точка находится вне куба
        return false;
    }
}
