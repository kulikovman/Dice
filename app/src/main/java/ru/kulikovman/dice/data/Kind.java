package ru.kulikovman.dice.data;

public enum Kind {

    WHITE(6),
    BLACK(6),
    RED(6);

    private int numberOfSides;

    Kind(int numberOfSides) {
        this.numberOfSides = numberOfSides;
    }

    public int getNumberOfSides() {
        return numberOfSides;
    }
}
