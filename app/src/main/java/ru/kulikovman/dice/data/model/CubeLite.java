package ru.kulikovman.dice.data.model;

public class CubeLite {

    private String skin;
    private int value;
    private int angle;
    private int marginStart;
    private int marginTop;

    public CubeLite(String skin, int value, int angle, int marginStart, int marginTop) {
        this.skin = skin;
        this.value = value;
        this.angle = angle;
        this.marginStart = marginStart;
        this.marginTop = marginTop;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getMarginStart() {
        return marginStart;
    }

    public void setMarginStart(int marginStart) {
        this.marginStart = marginStart;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }
}
