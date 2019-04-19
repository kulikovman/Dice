package ru.kulikovman.dice.util;

import android.content.res.Resources;

import javax.inject.Singleton;

import ru.kulikovman.dice.App;
import ru.kulikovman.dice.R;
import ru.kulikovman.dice.data.model.Area;

@Singleton
public class Calculations {

    private Resources resources;

    public Calculations() {
        resources = App.getContext().getResources();
    }

    public int getScreenWidth() {
        return resources.getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return resources.getDisplayMetrics().heightPixels;
    }

    public int getTitleWidth() {
        return resources.getDimensionPixelSize(R.dimen.button_title_size);
    }

    public int getTitleHeight() {
        int height = resources.getDimensionPixelSize(R.dimen.title_container_height);
        int marginTop = resources.getDimensionPixelSize(R.dimen.margin_20);

        return height + marginTop;
    }

    public Area getTotalArea() {
        return new Area(getScreenWidth() / 2 - getTitleWidth() / 2, getScreenWidth() / 2 + getTitleWidth() / 2, 0, getTitleHeight());
    }

    public Area getRollArea() {
        return new Area(getShadowRadius(), getScreenWidth() - getShadowRadius(), getShadowRadius(), getScreenHeight() - getShadowRadius());
    }

    public int getCubeSize() {
        return resources.getDimensionPixelSize(R.dimen.cube_size);
    }

    public int getCubeHalfSize() {
        return getCubeSize() / 2;
    }

    public int getCubeViewSize() {
        return resources.getDimensionPixelSize(R.dimen.cube_view_size);
    }

    public int getCubeViewHalfSize() {
        return getCubeViewSize() / 2;
    }

    public int getCubeInnerRadius() {
        return getCubeHalfSize();
    }

    public int getCubeOuterRadius() {
        return (int) Math.sqrt((Math.pow(getCubeHalfSize(), 2) + Math.pow(getCubeHalfSize(), 2)));
    }

    public int getShadowRadius() {
        return (int) Math.sqrt((Math.pow(getCubeViewHalfSize(), 2) + Math.pow(getCubeViewHalfSize(), 2)));
    }

    public int getSpaceBetweenCentersOfCubes() {
        return getCubeSize() + getCubeSize() / 3; // Треть кубика
    }

    public int getMaxCubesPerWidth() {
        return (getScreenWidth() - getCubeViewSize()) / getSpaceBetweenCentersOfCubes() + 1;
    }

    public int getMaxCubesPerHeight() {
        return (getScreenWidth() - getCubeViewSize() - getTitleHeight() * 2) / getSpaceBetweenCentersOfCubes() + 1;
    }

    public int getMaxOrderedCubes() {
        return getMaxCubesPerWidth() * getMaxCubesPerHeight();
    }

    public int getMaxRollingCubes() {
        int uiArea = getTitleWidth() * getTitleHeight();
        int pureScreenArea = getScreenWidth() * getScreenHeight() - uiArea;
        int cubeViewArea = getCubeViewSize() * getCubeViewSize();

        return (pureScreenArea / 100 * 40) / cubeViewArea; // 40% площади экрана
    }
}
