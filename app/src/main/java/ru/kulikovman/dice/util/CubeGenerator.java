package ru.kulikovman.dice.util;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.kulikovman.dice.data.Kind;
import ru.kulikovman.dice.data.model.Cube;
import ru.kulikovman.dice.db.model.Settings;

public class CubeGenerator {

    private final int FAILURE_LIMIT = 30; // Лимит неудачных бросков (с пересечением кубиков)

    private Calculations calculations;
    private Settings settings;
    private Intersection intersection;

    public CubeGenerator(Calculations calculations, Settings settings, Intersection intersection) {
        this.calculations = calculations;
        this.settings = settings;
        this.intersection = intersection;
    }

    public List<Cube> getListOfCubes() {
        return settings.isNotRolling() ? getRowCubes() : getRollCubes();
    }

    private List<Cube> getRollCubes() {
        List<Cube> cubes = new ArrayList<>();
        int numberOfCubes = settings.getNumberOfCubes();

        // Генерируем необходимое количество кубиков
        while (cubes.size() < numberOfCubes) {
            Cube cube = createCube();

            if (cubes.isEmpty()) {
                cubes.add(cube);
                Log.d("myLog", "Add cube " + cubes.size() + ": " + cube.getPosition().x + ", " + cube.getPosition().y);
            } else {
                // Проверяем пересечение с другими кубиками
                int count = 0;
                boolean intersection = true;
                while (intersection) {
                    for (Cube currentCube : cubes) {
                        if (Intersection.betweenCubes(cube, currentCube)) {
                            intersection = true;
                            break;
                        } else {
                            intersection = false;
                        }
                    }

                    if (intersection) {
                        // Если есть пересечение
                        count++;
                        Log.d("myLog", "cube intersection: " + count);

                        if (count < FAILURE_LIMIT) { // Защита от неудачного разброса кубиков
                            // Двигаем кубик
                            cube.setNewPosition(getCubePosition());
                        } else {
                            Log.d("myLog", "Сработала защита от неудачного разброса!");
                            Log.d("myLog", "---------------------------");

                            // Очищаем списки и начинаем заново
                            cubes.clear();

                            cubes.add(cube);
                            Log.d("myLog", "Add cube " + cubes.size() + ": " + cube.getPosition().x + ", " + cube.getPosition().y);
                            intersection = false;
                        }
                    } else {
                        // Если нет пересечений
                        cubes.add(cube);
                        Log.d("myLog", "Add cube " + cubes.size() + ": " + cube.getPosition().x + ", " + cube.getPosition().y);
                    }
                }
            }
        }

        return cubes;
    }


    private List<Cube> getRowCubes() {
        Kind cubeType = Kind.valueOf(settings.getKindOfCube());
        List<List<Point>> points = getListOfPoints();

        // Создаем кубики на основе сгенерированных координат
        List<Cube> cubes = new ArrayList<>();
        for (List<Point> line : points) {
            for (Point point : line){
                cubes.add(new Cube(calculations, cubeType, point));
            }
        }

        return cubes;
    }

    private List<List<Point>> getListOfPoints() {
        List<List<Point>> points = new ArrayList<>();

        int cubes = settings.getNumberOfCubes();
        int rows = calculations.getMaxCubesPerHeight();
        while (cubes > 0) {
            int remainder = cubes / rows < 1 || cubes % rows != 0 ? 1 : 0;
            int cubesPerRow = cubes / rows + remainder;

            // 7 / 3 = 2 + (1) = 3
            // 4 / 2 = 2 + (0) = 2
            // 2 / 1 = 2 + (0) = 2

            // 8 / 3 = 2 + (1) = 3
            // 5 / 2 = 2 + (1) = 3
            // 2 / 1 = 2 + (0) = 2

            // 2 / 3 = 0 + (1) = 1
            // 1 / 2 = 0 + (1) = 1

            // Получаем Y последнего ряда и размер кубика
            int space = calculations.getSpaceBetweenCentersOfCubes();
            int y = points.size() != 0 ? points.get(points.size() - 1).get(0).y + space : 0;

            // Создаем линию точек и добавляем в общий список
            List<Point> line = new ArrayList<>();
            for (int i = 0; i < cubesPerRow; i++) {
                int x = line.size() != 0 ? line.get(line.size() - 1).x + space : 0;
                line.add(new Point(x, y));
            }

            points.add(line);

            // Вычитаем добавленные кубики и созданный ряд
            cubes -= cubesPerRow;
            rows--;
        }

        // Вычисляем отступы для центрирования массива точек
        int width = points.get(0).get(points.get(0).size() - 1).x;
        int height = points.get(points.size() - 1).get(0).y;
        int title = calculations.getTitleHeight();
        int offsetX = (calculations.getScreenWidth() - width) / 2;
        int offsetY = title + (calculations.getScreenHeight() - title * 2 - height) / 2;

        // Центрируем массив точек
        for (List<Point> line : points) {
            // Внутренний отступ относительно первого ряда
            int widthCurrentLine = line.get(line.size() - 1).x;
            int innerOffsetX = (width - widthCurrentLine) / 2;

            // Отступы относительно первого ряда и центра экрана
            for (Point point : line){
                point.offset(innerOffsetX, 0);
                point.offset(offsetX, offsetY);
            }
        }

        return points;
    }












    private Cube createCube() {
        Kind kind = Kind.valueOf(settings.getKindOfCube());

        // Количество точек и угол поворота
        int value = Utils.getRandomValueWithinLimits(1, kind.getNumberOfSides());
        int  degrees = Utils.getRandomValueWithinLimits(0, 359);
        int  cubeSize = calculations.getCubeSize();
        int  cubeViewSize = calculations.getCubeViewSize();

        // Расположение кубика
        Point position = getCubePosition();

        return new Cube(kind.name(), value, degrees, cubeSize, cubeViewSize, position);
    }

    private Point getCubePosition() {
        // Границы области выпадения кубиков
        int minX = calculations.getRollArea().getMinX();
        int maxX = calculations.getRollArea().getMaxX();
        int minY = calculations.getRollArea().getMinY();
        int maxY = calculations.getRollArea().getMaxY();

        // Генерация координат кубика
        Point position = new Point();

        do {
            position.x = Utils.getRandomValueWithinLimits(minX, maxX);
            position.y = Utils.getRandomValueWithinLimits(minY, maxY);
        } while (intersection.withArea(calculations.getTotalArea(), calculations.getCubeOuterRadius(), position));

        return position;
    }







}
