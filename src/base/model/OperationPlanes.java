package base.model;

import base.pojo.Plane;
import base.presenter.Contract;
import util.ValuesGlobals;

import java.awt.*;
import java.util.*;
import java.util.List;

public class OperationPlanes {
    private List<Plane> planes;
    private int SPEED = 3;
    private Contract.Model model;
    private boolean isPauseGame = false;
    private int landedPlanes = 0;
    private Object lock;
    private int id = 0;
    private Plane nextPlaneToRemove;
    private String colorNewPlane = ValuesGlobals.YELLOW_PLANE;

    public OperationPlanes(Contract.Model model) {
        lock = new Object();
        this.model = model;
        planes = new ArrayList<>();
    }

    public void startGame() {
        startThread();
        createPlanes();
        eliminatePlanes();
    }

    public void viewIsReady() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    private synchronized void startThread() {
        Thread thread = new Thread(() -> {
            while (!isPauseGame) {
                try {
                    synchronized (lock) {
                        advance();
                        landedPlanes();
                        crashPlanes();
                        model.setPlanes(planes);
                        lock.notifyAll();
                        lock.wait();
                    }
                    Thread.sleep(ValuesGlobals.TIME_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private synchronized void createPlanes() {
        Thread addPlanes = new Thread(() -> {
            while (!isPauseGame) {
                try {
                    synchronized (lock) {
                        lock.wait();
                        randomPositionGenerator();
                        lock.notifyAll();
                    }
                    Thread.sleep(ValuesGlobals.TIME_GENERATE_PLANE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        addPlanes.start();
    }

    private void eliminatePlanes() {
        Thread eliminatePlanes = new Thread(() -> {
            while (!isPauseGame) {
                try {
                    synchronized (lock) {
                        lock.wait();
                        if (nextPlaneToRemove != null) {
                            synchronized (planes) {
                                planes.remove(nextPlaneToRemove);
                            }
                            nextPlaneToRemove = null;
                        }
                        verifyPlanes();
                        lock.notifyAll();
                    }
                    Thread.sleep(ValuesGlobals.TIME_ELIMINATE_PLANE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        eliminatePlanes.start(); // Добавляем запуск потока
    }


    public void landedPlanes() {
        synchronized (planes) {
            Iterator<Plane> iterator = planes.iterator();
            while (iterator.hasNext()) {
                Plane plane = iterator.next();
                Point currentPosition = plane.getPosition();

                if (currentPosition != null && ((ValuesGlobals.LANDED_RECTANGLE.contains(currentPosition) && ValuesGlobals.CENTER_RECTANGLE.contains(currentPosition)) ||
                        (ValuesGlobals.LANDED_RECTANGLE_2.contains(currentPosition) && ValuesGlobals.CENTER_RECTANGLE_2.contains(currentPosition)))  ) {
                    landedPlanes++;
                    iterator.remove(); // Используем итератор для безопасного удаления элемента из списка
                    break;
                } else {
                    if (plane.getPath().isEmpty()) {
                        // Если у самолета нет маршрута, двигаем его дальше
                        moveToRoute(plane);
                    }
                }
            }
        }
        model.setLandedPlanes(landedPlanes);
    }

    public void crashPlanes() {
        int numPlanes = planes.size();
        for (int i = 0; i < numPlanes - 1; i++) {
            Plane plane1 = planes.get(i);
            Rectangle rectangle1 = getRectangle(plane1);
            for (int j = i + 1; j < numPlanes; j++) {
                Plane plane2 = planes.get(j);
                Rectangle rectangle2 = getRectangle(plane2);
                if (rectangle1.intersects(rectangle2)) {
                    System.out.println("Самолеты столкнулись!");
                    model.gameOver();
                    isPauseGame = true;
                    return;
                }
            }
        }
    }


    private void verifyPlanes() {
        synchronized (planes) {
            Iterator<Plane> iterator = planes.iterator();
            while (iterator.hasNext()) {
                Plane plane = iterator.next();
                if (getDistanceTo(plane, plane.getNextPosition()) == 0 && plane.getPosition().x == 0) {
                    System.out.println("Самолёт пропал с радара. Сбой сис-мы навигации самолёта V1");
                    iterator.remove();
                    break;
                } else if (getDistanceTo(plane, plane.getNextPosition()) == 0 && plane.getPosition().x == ValuesGlobals.WIDTH_FRAME) {
                    System.out.println("Самолёт пропал с радара. Сбой сис-мы навигации самолёта V2");
                    iterator.remove();
                    break;
                } else if (getDistanceTo(plane, plane.getNextPosition()) == 0 && plane.getPosition().y == 0) {
                    System.out.println("Самолёт пропал с радара. Сбой сис-мы навигации самолёта V3");
                    iterator.remove();
                    break;
                } else if (getDistanceTo(plane, plane.getNextPosition()) == 0 && plane.getPosition().y == ValuesGlobals.HEIGHT_FRAME) {
                    System.out.println("Самолёт пропал с радара. Сбой сис-мы навигации самолёта V4");
                    iterator.remove();
                    break;
                }
            }
        }
    }


    private void randomPositionGenerator() {
        Plane plane = new Plane();
        addNewPlane(plane);
        id++;
        plane.setFinalId(id);
        plane.setAngle(getAngle(plane));
        moveToRoute(plane);
    }

    private Point getInversePosition(Plane plane) {
        Point position = plane.getPosition();
        Point endPoint = new Point();
        if (position.y >= ValuesGlobals.HEIGHT_FRAME) {
            endPoint.x = ValuesGlobals.WIDTH_FRAME - position.x;
            endPoint.y = 0;
        } else if (position.y <= 0) {
            endPoint.x = ValuesGlobals.WIDTH_FRAME - position.x;
            endPoint.y = ValuesGlobals.HEIGHT_FRAME;
        } else if (position.x >= ValuesGlobals.WIDTH_FRAME) {
            endPoint.x = 0;
            endPoint.y = ValuesGlobals.HEIGHT_FRAME - position.y;
        } else if (position.x <= 0) {
            endPoint.x = ValuesGlobals.WIDTH_FRAME;
            endPoint.y = ValuesGlobals.HEIGHT_FRAME - position.y;
        }
        return endPoint;
    }

    private void addNewPlane(Plane plane) {
        Random random = new Random();
        switch (random.nextInt(4 - 1 + 1) + 1) {
            case 1:
                plane.addPoint(new Point(0, random.nextInt(ValuesGlobals.HEIGHT_FRAME - 10 + 1) + 10));
                break;
            case 2:
                plane.addPoint(new Point(1010, random.nextInt(ValuesGlobals.HEIGHT_FRAME - 10 + 1) + 10));
                break;
            case 3:
                plane.addPoint(new Point(random.nextInt(ValuesGlobals.WIDTH_FRAME - 10 + 1) + 10, 0));
                break;
            case 4:
                plane.addPoint(new Point(random.nextInt(ValuesGlobals.WIDTH_FRAME - 10 + 1) + 10, ValuesGlobals.HEIGHT_FRAME));
                break;
        }
        plane.setColor(this.colorNewPlane);
        plane.setPosition(plane.getPath().get(0));
        plane.setNextPosition(getInversePosition(plane));
        planes.add(plane);
    }

    private synchronized void moveToRoute(Plane plane) {
        if (plane.getSpeed() == 0) {
            plane.setSpeed(SPEED);
        }

        synchronized (plane.getPath()) {
            if (plane.getPath().size() >= 2) {
                plane.setNextPosition(plane.getPath().get(1));
                double distance = getDistanceTo(plane, plane.getNextPosition());
                double dx = plane.getNextPosition().x - plane.getPosition().x;
                double dy = plane.getNextPosition().y - plane.getPosition().y;

                if (distance > plane.getSpeed()) {
                    double angle = Math.atan2(dy, dx);
                    int deltaX = (int) Math.round(plane.getSpeed() * Math.cos(angle));
                    int deltaY = (int) Math.round(plane.getSpeed() * Math.sin(angle));

                    plane.getPosition().x += deltaX;
                    plane.getPosition().y += deltaY;
                } else {
                    moveForward(plane);
                }
            } else {
                // Если у самолета есть начальная позиция, вернуть его туда
                if (!plane.getPath().isEmpty()) {
                    plane.getPosition().setLocation(plane.getPath().get(0));
                    plane.setNextPosition(plane.getPath().get(0));
                    System.out.println("Plane returned to initial position: " + plane.getPosition());
                } else {
                    // Иначе остановить движение
                    plane.setSpeed(0);
                }
            }
        }
    }




    private void moveForward(Plane plane) {
        Point nextPosition = plane.getNextPosition();

        double angleRadians = Math.toRadians(plane.getAngle());
        double dx = Math.cos(angleRadians) * plane.getSpeed();
        double dy = Math.sin(angleRadians) * plane.getSpeed();

        nextPosition.x -= dx;
        nextPosition.y -= dy;
        plane.setNextPosition(nextPosition);

        int x = plane.getPosition().x;
        int y = plane.getPosition().y;

        if (x <= 0 || x >= ValuesGlobals.WIDTH_FRAME || y <= 0 || y >= ValuesGlobals.HEIGHT_FRAME) {
            System.out.println("MoveForward");
            nextPlaneToRemove = plane;
        }
    }

    private void advanceInPath(Plane planeSelected) {
        if (planeSelected.getPath().size() > 1) {
            planeSelected.setPosition(planeSelected.getPath().get(0));
            planeSelected.setNextPosition(planeSelected.getPath().get(1));
            planeSelected.setAngle(getAngle(planeSelected, planeSelected.getPath().get(1)));
            planeSelected.getPath().remove(0);
        } else {
            // Обработка случая, когда в пути осталась только одна точка или путь пуст
            System.out.println("Самолет с id " + planeSelected.getFinalId() + " достиг конечной точки пути.");
        }
    }


    public void advance() {
        for (Plane plane : planes) {
            if (plane.getPath().size() > 2) {
                advanceInPath(plane);
            } else {
                moveToRoute(plane);
            }
        }
    }

    private void getNextPosition(Plane plane) {
        setNextPlanePosition(plane);
    }

    private double getAngle(Plane plane) {
        getNextPosition(plane);
        int x1 = plane.getNextPosition().x;
        int y1 = plane.getNextPosition().y;
        int x2 = plane.getPosition().x;
        int y2 = plane.getPosition().y;

        double angle = Math.atan2(y2 - y1, x2 - x1);

        return Math.toDegrees(angle);
    }

    private double getAngle(Plane plane, Point point) {
        int x1 = point.x;
        int y1 = point.y;
        int x2 = plane.getPosition().x;
        int y2 = plane.getPosition().y;

        double angle = Math.atan2(y2 - y1, x2 - x1);

        return Math.toDegrees(angle);
    }

    private double getDistanceTo(Plane plane, Point point) {
        double dx = point.x - plane.getPosition().x;
        double dy = point.y - plane.getPosition().y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    private void setNextPlanePosition(Plane plane) {
        if (plane.isNewPlane()) {
            setNewNextPlanePosition(plane);
        } else {
            plane.getPath().remove(0);
            plane.setNextPosition(plane.getPath().get(0));
        }
    }

    private void setNewNextPlanePosition(Plane plane) {
        if (plane.getPosition().y >= ValuesGlobals.HEIGHT_FRAME) {
            plane.getNextPosition().x = ValuesGlobals.WIDTH_FRAME - plane.getPosition().x;
            plane.getNextPosition().y = 0;
        } else if (plane.getPosition().y <= 0) {
            plane.getNextPosition().x = ValuesGlobals.WIDTH_FRAME - plane.getPosition().x;
            plane.getNextPosition().y = ValuesGlobals.HEIGHT_FRAME;
        } else if (plane.getPosition().x >= ValuesGlobals.WIDTH_FRAME) {
            plane.getNextPosition().x = 0;
            plane.getNextPosition().y = ValuesGlobals.HEIGHT_FRAME - plane.getPosition().y;
        } else if (plane.getPosition().x <= 0) {
            plane.getNextPosition().x = ValuesGlobals.WIDTH_FRAME;
            plane.getNextPosition().y = ValuesGlobals.HEIGHT_FRAME - plane.getPosition().y;
        }
    }

    private Plane getPlaneById(int id) {
        for (Plane plane : planes) {
            if (plane.getFinalId() == id) {
                return plane;
            }
        }
        return null;
    }

    private Rectangle getRectangle(Plane plane) {
        Rectangle rectangle = new Rectangle();
        int drawX = plane.getPosition().x - 20;
        int drawY = plane.getPosition().y - 20;
        rectangle.setBounds(drawX, drawY, 40, 40);
        return rectangle;
    }

    public boolean isSelectedPlane(Point point) {
        for (Plane plane : planes) {
            if (getRectangle(plane).contains(point)) {
                TemporalPlanes.setId(plane.getFinalId());
                if (plane.getPath().size() < 2) {
                    plane.getPath().add(point);
                } else {
                    Point point1 = plane.getPath().get(0);
                    Point point2 = plane.getPath().get(1);
                    plane.getPath().clear();
                    plane.getPath().add(point1);
                    plane.getPath().add(point2);
                }
                plane.getPath().set(1, point);
                return true;
            }
        }
        return false;
    }

    private List<Point> calculateIntermediePoints(List<Point> points, Plane plane) {

        List<Point> intermediatePoints = new ArrayList<>();

        for (int i = 0; i < points.size() - 1; i++) {
            Point point1 = points.get(i);
            Point point2 = points.get(i + 1);
            double distance = point1.distance(point2);
            double numberOfPoints = distance / plane.getSpeed();
            double xIncrement = (point2.x - point1.x) / numberOfPoints;
            double yIncrement = (point2.y - point1.y) / numberOfPoints;
            for (int j = 1; j < numberOfPoints; j++) {
                int x = (int) (point1.x + j * xIncrement);
                int y = (int) (point1.y + j * yIncrement);
                intermediatePoints.add(new Point(x, y));
            }
        }
        return intermediatePoints;
    }

    public void addPointToPath(Point point) {
        if (TemporalPlanes.getId() > 0 && getPlaneById(TemporalPlanes.getId()) != null) {
            getPlaneById(TemporalPlanes.getId()).addPoint(point);
        }
    }

    public void pauseGame() {
        if (isPauseGame) {
            isPauseGame = false;
            Chronometer.getInstance().continueTime();
            startGame();
        } else {
            isPauseGame = true;
            Chronometer.getInstance().pauseTime();
        }
    }

    public void selectedPlaneNull() {
        if (TemporalPlanes.getId() > 0) {
            Plane planeSelected = getPlaneById(TemporalPlanes.getId());
            planeSelected.setPath(calculateIntermediePoints(planeSelected.getPath(), planeSelected));
            planeSelected.setFollowPath(true);
        }

        TemporalPlanes.setId(-1);
    }

    public void setSpeed(int speed) {
        if (TemporalPlanes.getId() > 0) {
            getPlaneById(TemporalPlanes.getId()).setSpeed(speed);
        }
    }

    public void setImageAllPlanes(String colorPlaneSelected) {
        for (Plane plane : planes) {
            plane.setColor(colorPlaneSelected);
        }

        colorNewPlane = colorPlaneSelected;
    }

    public void changeColorPlane(String colorPlaneSelected) {
        if (TemporalPlanes.getId() > 0) {
            getPlaneById(TemporalPlanes.getId()).setColor(colorPlaneSelected);
        }
    }

    public void setTotalSpeed(int speed) {
        for (Plane plane : planes) {
            plane.setSpeed(speed);
        }
        this.SPEED = speed;
    }
}