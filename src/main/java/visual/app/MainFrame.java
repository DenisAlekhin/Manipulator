package visual.app;

import app.manipulator.Manipulator;
import app.model.Algorithm;
import app.service.Iterations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    private JComboBox algorithm;
    private JTextField textFieldPrecision;
    private JTextField textFieldLocalPrecision;
    static Manipulator manipulator = new Manipulator();
    static JFrame frame = new MainFrame("Manipulator");
    private Integer countOfRuns = 0;
    static boolean buttonClicked = false;
    private final DefaultTableModel model = new DefaultTableModel(null, new String[]{"№", "Distance", "Iterations"});
    DecimalFormat df = new DecimalFormat("#.###");
    public MainFrame(String title) {
        super(title);
        actionListenersInit();
        setUpTable();
        setUpList();
        setUpTextFields();
    }

    private void actionListenersInit() {
        slider5.addChangeListener(e -> {
            manipulator.moveElement(0, slider5.getValue());
            frame.repaint();
        });
        slider1.addChangeListener(e -> {
            manipulator.moveElement(2, slider1.getValue());
            frame.repaint();
        });
        slider2.addChangeListener(e -> {
            manipulator.moveElement(4, slider2.getValue());
            frame.repaint();
        });
        slider7.addChangeListener(e -> {
            manipulator.moveTarget(0, slider7.getValue());
            frame.repaint();
        });
        slider8.addChangeListener(e -> {
            manipulator.moveTarget(1, (double)slider8.getValue() / 100.0);
            frame.repaint();
        });
        resetTargetButton.addActionListener(e -> {
            slider7.setValue(0);
            slider8.setValue(100);
            manipulator.resetTarget();
            frame.repaint();
        });
        buttonMove.addActionListener(e -> {
            Algorithm algorithm = getAlgorithm();
            Double precision = Double.valueOf(textFieldPrecision.getText());
            Double localPrecision = Double.valueOf(textFieldLocalPrecision.getText());
            Double result = manipulator.setToTarget(true, algorithm, precision, localPrecision);
            countOfRuns++;
            String[] data = {countOfRuns.toString(), df.format(result), Integer.toString(Iterations.get())};
            model.addRow(data);
        });

        addObstacleButton.addActionListener(e -> buttonClicked = true);
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
        frame.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
//                System.out.println("x: " + e.getPoint().x);
//                System.out.println("y: " + e.getPoint().y);
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

    private void setUpTable() {
        statistics.setModel(model);
    }

    private void setUpList(){
        String[] items = {"Jmetal", "Local", "Strongin"};
        algorithm.setModel(new DefaultComboBoxModel(items));
    }

    private Algorithm getAlgorithm() {
        String selectedItem = (String)algorithm.getSelectedItem();
        return switch (selectedItem) {
            case "Jmetal" -> Algorithm.JMETAL;
            case "Local" -> Algorithm.LOCAL;
            case "Strongin" -> Algorithm.STRONGIN;
            default -> Algorithm.JMETAL;
        };
    }

    private void setUpTextFields() {
        textFieldLocalPrecision.setText("0.0000000001");
        textFieldPrecision.setText("0.01");
    }
}
