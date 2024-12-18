package base.model;

import base.pojo.Plane;
import base.presenter.Contract;

import java.awt.*;
import java.util.List;

public class ManagerModel implements Contract.Model {
    private Contract.Presenter presenter;
    private OperationPlanes operationPlanes;

    public ManagerModel() {
        operationPlanes = new OperationPlanes(this);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addPointToPath(Point point) {
        operationPlanes.addPointToPath(point);
    }

    @Override
    public void setPlanes(List<Plane> planes) {
        presenter.getView().paintPlanes(planes);
    }

    @Override
    public void startGame() {
        operationPlanes.startGame();
    }

    @Override
    public void pauseGame() {
        operationPlanes.pauseGame();
    }

    @Override
    public boolean isSelectedPlane(Point point) {
        return operationPlanes.isSelectedPlane(point);
    }

    @Override
    public void viewIsReady() {
        operationPlanes.viewIsReady();
    }

    @Override
    public void selectedPlaneNull() {
        operationPlanes.selectedPlaneNull();
    }

    @Override
    public void setLandedPlanes(int landedPlanes) {
        presenter.getView().setLandedPlanes(landedPlanes);
    }

    @Override
    public void gameOver() {
        presenter.getView().gameOver();
    }

    @Override
    public void setSpeed(int speed) {
        operationPlanes.setSpeed(speed);
    }

    @Override
    public void setImageAllPlanes(String colorPlaneSelected) {
        operationPlanes.setImageAllPlanes(colorPlaneSelected);
    }

    @Override
    public void changeColorPlane(String colorPlaneSelected) {
        operationPlanes.changeColorPlane(colorPlaneSelected);
    }

    @Override
    public void setTotalSpeed(int speed) {
        operationPlanes.setTotalSpeed(speed);
    }
}
