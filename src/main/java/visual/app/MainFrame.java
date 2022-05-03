package visual.app;

import service.Iterations;
import service.manipulator.Manipulator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class MainFrame extends JFrame{
    private JPanel mainPanel;
    private JSlider slider1;
    private JSlider slider2;
    private JSlider slider5;
    private JSlider slider7;
    private JSlider slider8;
    private JButton resetTargetButton;
    private JButton buttonMove;
    private JButton addObstacleButton;
    private JTable statistics;
    static Manipulator manipulator = new Manipulator();
    static JFrame frame = new MainFrame("Manipulator");
    private Integer countOfRuns = 0;
    static boolean buttonClicked = false;
    private DefaultTableModel model = new DefaultTableModel(null, new String[]{"№", "Distance", "Iterations"});
    DecimalFormat df = new DecimalFormat("#.###");
    public MainFrame(String title) {
        super(title);
        actionListenersInit();
        setUpTable();
    }

    private void actionListenersInit() {
        slider5.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveElement(0, slider5.getValue());
                frame.repaint();
            }
        });
        slider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveElement(2, slider1.getValue());
                frame.repaint();
            }
        });
        slider2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveElement(4, slider2.getValue());
                frame.repaint();
            }
        });
        slider7.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveTarget(0, slider7.getValue());
                frame.repaint();
            }
        });
        slider8.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveTarget(1, (double)slider8.getValue() / 100.0);
                frame.repaint();
            }
        });
        resetTargetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slider7.setValue(0);
                slider8.setValue(100);
                manipulator.resetTarget();
                frame.repaint();
            }
        });
        buttonMove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Double result = manipulator.setToTarget(true);
                countOfRuns++;
                String[] data = {countOfRuns.toString(), df.format(result), Integer.toString(Iterations.get())};
                model.addRow(data);
            }
        });

        addObstacleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonClicked = true;
            }
        });
    }

    public static void main(String[] args) {
        createManipulator();
        MainFrameInit();
    }

    private static void createManipulator() {
        manipulator.addElement(0);
        manipulator.addElement(1);
        manipulator.addElement(0);
        manipulator.addElement(1);
        manipulator.addElement(0);
        manipulator.addElement(1);
    }

    private static void MainFrameInit() {
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(manipulator);
        frame.add(new MainFrame("Manipulator").mainPanel, BorderLayout.EAST);
        frame.addMouseListener(manipulator);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        //moveToCenterScreen(frame);
        frame.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("x: " + e.getPoint().x);
                System.out.println("y: " + e.getPoint().y);
                if(buttonClicked) {
                    manipulator.addObstacle(e.getPoint());
                }
                buttonClicked = false;
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
    }

    public static void moveToCenterScreen(JFrame frame) {
        Toolkit kit = frame.getToolkit();
        GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Insets in = kit.getScreenInsets(gs[0].getDefaultConfiguration());
        Dimension d = kit.getScreenSize();

        int max_width = (d.width - in.left - in.right);
        int max_height = (d.height - in.top - in.bottom);

        frame.setLocation((int) (max_width - frame.getWidth()) / 2, (int) (max_height - frame.getHeight() ) / 2);
    }

    private void setUpTable() {
        statistics.setModel(model);
    }
}
