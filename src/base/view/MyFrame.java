package base.view;

import base.pojo.Plane;
import base.presenter.Contract;
import util.ValuesGlobals;
import base.view.panels.PrincipalPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyFrame extends JFrame implements Contract.View {
    private Contract.Presenter presenter;
    private PrincipalPanel principalPanel;

    public MyFrame() {
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(false);
        this.setTitle("Диспетчер");
        this.setIconImage(getAppIcon());
        initComponents();
        setSizes();
    }
    private Image getAppIcon() {
        ImageIcon icon = new ImageIcon((ValuesGlobals.ICON_GAME));
        return icon.getImage();
    }

    private void setSizes() {
        this.setSize(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME + 36);
        this.setPreferredSize(new Dimension(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME + 36));
        this.setMinimumSize(new Dimension(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME + 36));
        this.setMaximumSize(new Dimension(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME + 36));
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setBackground(new Color(0, 0, 0));
    }

    private void initComponents() {
        principalPanel = new PrincipalPanel(this);
        this.add(principalPanel);
    }

    @Override
    public void start() {
        this.setVisible(true);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void paintPlanes(List<Plane> planes) {
        principalPanel.getPanelGame().setPlanes(planes);
        principalPanel.getPanelGame().repaint();
    }

    @Override
    public void setLandedPlanes(int landedPlanes) {
        principalPanel.getPanelGame().setLandedPlanes(landedPlanes);
    }

    @Override
    public void gameOver() {
        principalPanel.getPanelGame().gameOver();
    }

    public Contract.Presenter getPresenter() {
        return presenter;
    }

    public void showMenu() {
        principalPanel.showMenu();
    }

    public void showGame() {
        principalPanel.showGame();
    }

    public void selectedPlaneNull() {
        presenter.selectedPlaneNull();
    }

    public PrincipalPanel getPrincipalPanel() {
        return principalPanel;
    }
}
