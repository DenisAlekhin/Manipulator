package VisualApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame{
    private JPanel mainPanel;
    private JButton MoveMechanism;
    static Manipulator manipulator = new Manipulator();
    static JFrame frame = new MainFrame("Manipulator");
    public MainFrame(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        MoveMechanism.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manipulator.moveElement(2,0.7, 60);
                manipulator.moveElement(5,0.7, -60);
                frame.repaint();
            }
        });
    }

    public static void main(String[] args) {
        manipulator.addElement(1, -45);
        manipulator.addElement(1, 45);
        manipulator.addElement(1, 30);
        manipulator.addElement(1, 45, 0);
        manipulator.addElement(1, -45);
        manipulator.addElement(1, -30);

        frame.setSize(1000,600);
        frame.add(manipulator);
        frame.setVisible(true);
    }
}
