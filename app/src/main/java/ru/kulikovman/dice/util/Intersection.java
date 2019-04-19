package ru.kulikovman.dice.util;

import android.graphics.Point;

import javax.inject.Singleton;

import ru.kulikovman.dice.data.model.Area;
import ru.kulikovman.dice.util.Calculations;
import ru.kulikovman.dice.data.model.Cube;

@Singleton
public class Intersection {

    // Проверка пересечения кубика с указанной областью
    public boolean withArea(Area area, int cubeOuterRadius, Point position) {
        return position.x > area.getMinX() - cubeOuterRadius && position.x < area.getMaxX() + cubeOuterRadius &&
                position.y > area.getMinY() - cubeOuterRadius && position.y < area.getMaxY() + cubeOuterRadius;
    }

    // Проверка пересечения кубика с кубиком
    public static boolean betweenCubes(Cube c1, Cube c2) {
        // Расчет растояния между центрами кубиков
        int distance = (int) Math.sqrt((Math.pow(Math.abs(c1.getX() - c2.getX()), 2) + Math.pow(Math.abs(c1.getY() - c2.getY()), 2)));

        // ЭТАП 1: проверка по внешним радиусам
        // Если больше суммы внешних радиусов, то все ок
        if (distance > calculation.getCubeOuterRadius() * 2) {
            return false;
        }

        // ЭТАП 2: проверка по внутренним радиусам
        // Если меньше суммы радиусов, то кубики 100% пересекаются
        if (distance < calculation.getCubeInnerRadius() * 2) {
            return true;
        }

        // ЭТАП 3: проверка пересечения вершин и кубиков
        // Первый кубик и точки второго кубика
        if (isPointInsideCube(c1, c2.getA().x, c2.getA().y) || isPointInsideCube(c1, c2.getB().x, c2.getB().y) ||
                isPointInsideCube(c1, c2.getC().x, c2.getC().y) || isPointInsideCube(c1, c2.getD().x, c2.getD().y)) {
            return true;
        }

        // Второй кубик и точки первого кубика
        if (isPointInsideCube(c2, c1.getA().x, c1.getA().y) || isPointInsideCube(c2, c1.getB().x, c1.getB().y) ||
                isPointInsideCube(c2, c1.getC().x, c1.getC().y) || isPointInsideCube(c2, c1.getD().x, c1.getD().y)) {
            return true;
        }

        // Кубики не пересекаются
        return false;
    }

    private static boolean isPointInsideCube(Cube cube, int pX, int pY) {
        // Делим куб на два треугольника и проверяем принадлежность
        // точек одного куба к треугольникам другого

        // Обозначение вершин куба
        // a - b
        // | \ |
        // d - c

        int ax = cube.getA().x;
        int bx = cube.getB().x;
        int cx = cube.getC().x;
        int dx = cube.getD().x;

        int ay = cube.getA().y;
        int by = cube.getB().y;
        int cy = cube.getC().y;
        int dy = cube.getD().y;

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
