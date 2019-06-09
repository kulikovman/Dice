package ru.kulikovman.dice.data;

public enum Type {

    WHITE(6),
    BLACK(6),
    RED(6);

    private int numberOfSides;

    Type(int numberOfSides) {
        this.numberOfSides = numberOfSides;
    }

    public int getNumberOfSides() {
        return numberOfSides;
    }
}
