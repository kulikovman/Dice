package ru.kulikovman.dice.db.model;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import ru.kulikovman.dice.data.model.Cube;

@Entity
public class ThrowResult {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long time;
    private List<Cube> cubes;

    public ThrowResult() {
        time = System.currentTimeMillis();
        cubes = new ArrayList<>();
    }

    public ThrowResult(List<Cube> cubes) {
        time = System.currentTimeMillis();
        this.cubes = cubes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<Cube> getCubes() {
        return cubes;
    }

    public void setCubes(List<Cube> cubes) {
        this.cubes = cubes;
    }

    public void addCube(Cube cube) {
        cubes.add(cube);
    }

    public void addCubes(List<Cube> cubes) {
        this.cubes = cubes;
    }
}
