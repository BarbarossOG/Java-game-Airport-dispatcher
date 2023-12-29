package base.view.panels;

import base.pojo.Plane;
import base.model.Chronometer;
import base.view.MyFrame;
import util.ValuesGlobals;
import util.UtilImages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class PanelGame extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    private MyFrame frame;
    private Graphics2D g2d;
    private JLayeredPane layeredPane;
    private List<Plane> planes;
    private ImageIcon imagePlane;
    private ImageIcon imageAirPort;
    private JLabel imageLabel;
    private Font font;
    private int landedPlanes;
    private RenderingHints renderingHints;
    private ImageIcon iconPlaneRed;
    private ImageIcon iconPlaneBlue;
    private ImageIcon iconPlaneYellow;

    public PanelGame(MyFrame myFrame) {
        super();
        this.frame = myFrame;
        planes = new ArrayList<Plane>();
        iconPlaneBlue = getImagePlane(ValuesGlobals.BLUE_PLANE);
        iconPlaneRed = getImagePlane(ValuesGlobals.RED_PLANE);
        iconPlaneYellow = getImagePlane(ValuesGlobals.YELLOW_PLANE);
        imageAirPort = getImageAirPort();
        font = new Font("Arial", Font.BOLD, 12);
        this.setBackground(new Color(142, 173, 184));
        initComponents();
        setSizes();
        layeredPane = new JLayeredPane();
        add(layeredPane);

    }

    private void initComponents() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        renderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.setVisible(true);
    }





    public void chargeBackground(Graphics2D g2d) {

        g2d.drawImage(imageAirPort.getImage(), 280, 180, null);
        g2d.setColor(Color.GREEN);
        g2d.drawRect(ValuesGlobals.LANDED_RECTANGLE.x, ValuesGlobals.LANDED_RECTANGLE.y, ValuesGlobals.LANDED_RECTANGLE.width, ValuesGlobals.LANDED_RECTANGLE.height);
        g2d.setColor(Color.RED);
        g2d.drawRect(ValuesGlobals.CENTER_RECTANGLE.x, ValuesGlobals.CENTER_RECTANGLE.y, ValuesGlobals.CENTER_RECTANGLE.width, ValuesGlobals.CENTER_RECTANGLE.height);
        g2d.drawRect(ValuesGlobals.LANDED_RECTANGLE_2.getBounds().x, ValuesGlobals.LANDED_RECTANGLE_2.getBounds().y,
                ValuesGlobals.LANDED_RECTANGLE_2.getBounds().width, ValuesGlobals.LANDED_RECTANGLE_2.getBounds().height);
        g2d.drawRect(ValuesGlobals.CENTER_RECTANGLE_2.getBounds().x, ValuesGlobals.CENTER_RECTANGLE_2.getBounds().y,
                ValuesGlobals.CENTER_RECTANGLE_2.getBounds().width, ValuesGlobals.CENTER_RECTANGLE_2.getBounds().height);

        g2d.setColor(Color.GREEN);

        g2d.drawRect(ValuesGlobals.LANDED_RECTANGLE_2.x, ValuesGlobals.LANDED_RECTANGLE_2.y, ValuesGlobals.LANDED_RECTANGLE_2.width, ValuesGlobals.LANDED_RECTANGLE_2.height);
        g2d.setColor(Color.RED);
        g2d.drawRect(ValuesGlobals.CENTER_RECTANGLE_2.x, ValuesGlobals.CENTER_RECTANGLE_2.y, ValuesGlobals.CENTER_RECTANGLE_2.width, ValuesGlobals.CENTER_RECTANGLE_2.height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2d = (Graphics2D) g;

        chargeBackground(g2d);
        drawAllPlanes(g2d);
        drawAllPaths(g2d);
        g2d.drawRect(0, 0, ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME);
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHints(renderingHints);
        g2d.setFont(font);
        printNumberPlanes();
        frame.getPresenter().notifyModel();
    }

    private void printNumberPlanes() {
        g2d.drawString("Количество самолетов: " + planes.size(), 10, 20);
        g2d.drawString("Количество приземлившихся самолетов: " + landedPlanes, 220, 20);
        g2d.drawString("Время игры: " + Chronometer.getInstance().getTime(), 10, 40);
        g2d.drawString("Нажмите Esc, чтобы перейти в главное меню:", 220, 40);
    }
    public void drawAllPlanes(Graphics2D g2d) {
        for (Plane plane : planes) {
            drawImage(plane, g2d);
        }
    }

    private void drawAllPaths(Graphics2D g2d) {
        for (Plane plane : planes) {
            drawPath(plane, g2d);
        }
    }

    private void drawPath(Plane plane, Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        for (Plane plane1 : planes) {
            for (Point point : plane1.getPath()) {
                g2d.drawLine(point.x, point.y, point.x, point.y);
            }
        }
    }

    private void drawImage(Plane plane, Graphics2D g2d) {
        double rotationRequired = Math.toRadians(plane.getAngle());
        AffineTransform tx = g2d.getTransform();

        int imageWidth = 40;
        int imageHeight = 40;
        int drawX = plane.getPosition().x - imageWidth / 2;
        int drawY = plane.getPosition().y - imageHeight / 2;

        g2d.rotate(rotationRequired, plane.getPosition().x, plane.getPosition().y);

        if (plane.getColor().equals(ValuesGlobals.RED_PLANE)) {
            g2d.drawImage(iconPlaneRed.getImage(), drawX, drawY, null);
        }

        if (plane.getColor().equals(ValuesGlobals.BLUE_PLANE)) {
            g2d.drawImage(iconPlaneBlue.getImage(), drawX, drawY, null);
        }

        if (plane.getColor().equals(ValuesGlobals.YELLOW_PLANE)) {
            g2d.drawImage(iconPlaneYellow.getImage(), drawX, drawY, null);
        }

        g2d.setTransform(tx);
    }


    private ImageIcon getImageAirPort() {
        UtilImages utilImages = new UtilImages();
        JLabel imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, 370, 280);
        Icon img = utilImages.loadScaleImage(ValuesGlobals.PHAT_AIRPORT_IMAGE_ORIGINAL, imageLabel.getWidth(), imageLabel.getHeight());
        imageLabel.setIcon(img);
        return new ImageIcon(((ImageIcon) img).getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_DEFAULT));

    }

    private ImageIcon getImagePlane(String imagePlaneSelected) {
        UtilImages utilImages = new UtilImages();
        imageLabel = new JLabel();
        imageLabel.setBounds(10, 10, 40, 40);
        Icon img = utilImages.loadScaleImage(imagePlaneSelected, imageLabel.getWidth(), imageLabel.getHeight());
        imageLabel.setIcon(img);
        return new ImageIcon(((ImageIcon) img).getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_DEFAULT));
    }

    private void setSizes() {
        this.setSize(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME);
        this.setPreferredSize(new Dimension(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME));
        this.setMinimumSize(new Dimension(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME));
        this.setMaximumSize(new Dimension(ValuesGlobals.WIDTH_FRAME, ValuesGlobals.HEIGHT_FRAME));
        this.setLayout(new BorderLayout());
        this.setLocation(0, 0);
        this.setVisible(true);
    }

    public void setLandedPlanes(int landedPlanes) {
        this.landedPlanes = landedPlanes;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        frame.getPresenter().isSelectedPlane(e.getPoint());
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        frame.selectedPlaneNull();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        frame.getPresenter().addPointToPath(e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            frame.showMenu();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void setPlanes(List<Plane> planes) {
        this.planes = planes;
    }

    public void gameOver() {
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage("GAME OVER");
        if (optionPane.showConfirmDialog(frame, "Количество приземлившихся самолетов составляет " + landedPlanes + "\nВремя игры: " + Chronometer.getInstance().getTime() + "\nВы хотите сыграть заново?", "GAME OVER", optionPane.YES_NO_OPTION) == optionPane.YES_OPTION) {
            frame.setVisible(false);
            frame.getPresenter().restartGame();
        } else {
            System.exit(0);
        }
    }

    public void setImagePlaneSelected(String colorPlaneSelected) {
        frame.getPresenter().setImageAllPlanes(colorPlaneSelected);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            if(frame.getPresenter().isSelectedPlane(e.getPoint())) {
                frame.getPresenter().pauseGame();
                frame.getPresenter().isSelectedPlane(e.getPoint());
                showPopupMenu(e.getPoint());
            }
        }
    }

    public void showPopupMenu(Point point) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenu menuChangeColor = menuChangeColor();

        JMenuItem menuItemBlue = menuItemBlue();
        JMenuItem menuItemRed = menuItemRed();
        JMenuItem menuItemYellow = menuItemYellow();


        menuChangeColor.add(menuItemBlue);
        menuChangeColor.add(menuItemRed);
        menuChangeColor.add(menuItemYellow);

        popupMenu.add(menuChangeColor);
        JMenuItem changeVelocity = changeVelocity();
        popupMenu.add(changeVelocity);

        popupMenu.show(this, point.x, point.y);
    }

    private JMenu menuChangeColor() {
        JMenu menuChangeColor = new JMenu("Изменить Цвет");
        menuChangeColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getPresenter().pauseGame();
            }
        });

        return menuChangeColor;
    }

    private JMenuItem changeVelocity() {
        JMenuItem changeVelocity = new JMenuItem("Изменить Скорость");
        changeVelocity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialogVelocity = dialogVelocity();
                dialogVelocity.setSize(300, 300);
                dialogVelocity.setVisible(true);
            }
        });

        return changeVelocity;
    }

    private JDialog dialogVelocity() {
        JDialog dialogVelocity = new JDialog();

        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("<html><body>Выберите скорость самолета<br>0 - самая низкая скорость, а 10 - самая высокая</body></html>");
        label.setForeground(Color.BLACK);

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
        speedSliderFeatures(speedSlider);

        JButton button = new JButton("Принять");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogVelocity.setVisible(false);
                frame.getPresenter().setPlaneSpeed(speedSlider.getValue());
                frame.getPresenter().pauseGame();
            }
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(speedSlider, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);

        dialogVelocity.getContentPane().add(panel);

        return dialogVelocity;
    }

    private void speedSliderFeatures(JSlider speedSlider) {
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setBackground(Color.black);
        speedSlider.setForeground(Color.white);
        speedSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int speed = source.getValue();
                frame.getPresenter().setPlaneSpeed(speed);
            }
        });
    }

    private JMenuItem menuItemBlue() {
        JMenuItem menuItemBlue = new JMenuItem("Синий");
        menuItemBlue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getPresenter().changeColorPlane(ValuesGlobals.BLUE_PLANE);
                frame.getPresenter().pauseGame();
            }
        });

        return menuItemBlue;
    }

    private JMenuItem menuItemRed() {
        JMenuItem menuItemRed = new JMenuItem("Красный");
        menuItemRed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getPresenter().changeColorPlane(ValuesGlobals.RED_PLANE);
                frame.getPresenter().pauseGame();
            }
        });

        return menuItemRed;
    }

    private JMenuItem menuItemYellow() {
        JMenuItem menuItemYellow = new JMenuItem("Желтый");
        menuItemYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getPresenter().changeColorPlane(ValuesGlobals.YELLOW_PLANE);
                frame.getPresenter().pauseGame();
            }
        });

        return menuItemYellow;
    }
}
