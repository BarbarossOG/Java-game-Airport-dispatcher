package base.presenter;

import base.model.Timer;
import base.model.ManagerModel;
import base.view.MyFrame;

import javax.swing.*;

public class ManagerGeneral {
    private ManagerGeneral() {
    }

    private static ManagerGeneral instance;
    private Contract.View view;
    private Contract.Model model;
    private Contract.Presenter presenter;

    public static ManagerGeneral getInstance() {
        return instance == null ? instance = new ManagerGeneral() : instance;
    }

    private void CreateMVP() {
        model = new ManagerModel();
        presenter = new Presenter(this);
        view = new MyFrame();
        view.setPresenter(presenter);
        model.setPresenter(presenter);
        presenter.setView(view);
        presenter.setModel(model);
    }

    public void runProject() {
        CreateMVP();
        // Отложенный запуск главного окна и начала игры в потоке EDT
        SwingUtilities.invokeLater(() -> {
            view.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            presenter.startGame();
        });
        Timer.getInstance().start();
    }


    public void restartGame() {
        presenter = null;
        model = null;
        view.dispose();
        runProject();
    }
}
