package base.pojo;

import util.ValuesGlobals;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Plane {
    private Double angle = 0.0;
    private List<Point> path;
    private boolean newPlane;
    boolean isFollowPath = false;
    private Point position;
    private Point nextPosition;
    private int finalId;
    private String color;
    private int speed;

    public Plane() {
        this.path = new ArrayList<Point>();
        this.newPlane = true;
    }

    public void addPoint(Point point) {
        path.add(point);
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public List<Point> getPath() {
        return path;
    }

    public void setPath(List<Point> path) {
        this.path = path;
    }

    // В классе Plane
    public Point getPosition() {
        if (!path.isEmpty()) {
            return path.get(0);
        } else {
            // Возвращаем null, если path пуст
            return new Point(0, 0);
        }
    }

    public boolean isNewPlane() {
        return newPlane;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getNextPosition() {
        if (!path.isEmpty() && path.size() > 1) {
            return path.get(1);
        } else {
            // Возвращаем текущую позицию, если path пуст или содержит только одну точку
            return getPosition();
        }
    }


    public void setNextPosition(Point nextPosition) {
        if (path.size() > 1) {
            path.set(1, nextPosition);
        } else {
            path.add(nextPosition);
        }
    }

    public void setFollowPath(boolean b) {
        this.isFollowPath = b;
    }

    public int getFinalId() {
        return finalId;
    }

    public void setFinalId(int finalId) {
        this.finalId = finalId;
    }

    public void setNewPlane(boolean newPlane) {
        this.newPlane = newPlane;
    }

    public boolean isFollowPath() {
        return isFollowPath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "angle=" + angle +
                ", path=" + path +
                ", position=" + getPosition() +
                '}';
    }
}