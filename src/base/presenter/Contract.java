package base.presenter;

import base.pojo.Plane;

import java.awt.*;
import java.util.List;

public interface Contract {
    public interface View {
        void start();

        void setPresenter(Presenter presenter);

        void paintPlanes(List<Plane> planes);

        void setLandedPlanes(int landedPlanes);

        void gameOver();

        void dispose();
    }

    public interface Presenter {
        void setModel(Model model);

        void setView(View view);

        void startGame();

        boolean isSelectedPlane(Point point);
        void addPointToPath(Point point);
        View getView();

        void notifyModel();

        void pauseGame();

        void selectedPlaneNull();

        void restartGame();

        void setPlaneSpeed(int speed);

        void setImageAllPlanes(String colorPlaneSelected);

        void changeColorPlane(String colorPlaneSelected);

        void setTotalSpeed(int speed);
    }

    public interface Model {
        void setPresenter(Presenter presenter);
        void setPlanes(List<Plane> planes);

        void addPointToPath(Point point);

        void startGame();

        void pauseGame();

        boolean isSelectedPlane(Point point);

        void viewIsReady();


        void selectedPlaneNull();

        void setLandedPlanes(int landedPlanes);

        void gameOver();

        void setSpeed(int speed);

        void setImageAllPlanes(String colorPlaneSelected);

        void changeColorPlane(String colorPlaneSelected);

        void setTotalSpeed(int speed);
    }
}
