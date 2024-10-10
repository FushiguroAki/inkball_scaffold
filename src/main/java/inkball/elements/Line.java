package inkball.elements;

import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

public class Line {
    private List<PVector> points;   // store points of the line
    private int thickness;
    private int color;

    public Line(int thickness, int color) {
        this.points = new ArrayList<>();
        this.thickness = thickness;
        this.color = color;
    }

    public void addPoint(float x, float y) {
        points.add(new PVector(x, y));
    }

    public List<PVector> getPoints() {
        return points;
    }

    public int getThickness() {
        return thickness;
    }

    public int getColor() {
        return color;
    }
}