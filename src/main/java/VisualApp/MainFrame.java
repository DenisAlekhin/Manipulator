package VisualApp;

import VisualApp.Manipulator.Manipulator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.beans.PropertyChangeListener;

public class MainFrame extends JFrame{
    private JPanel mainPanel;
    private JSlider slider1;
    private JSlider slider2;
    private JSlider slider3;
    private JSlider slider4;
    private JSlider slider5;
    private JSlider slider6;
    static Manipulator manipulator = new Manipulator();
    static JFrame frame = new MainFrame("Manipulator");
    public MainFrame(String title) {
        super(title);
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
        slider6.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveElement(1, (double)slider6.getValue() / 100.0);
                frame.repaint();
            }
        });
        slider3.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveElement(3, (double)slider3.getValue() / 100.0);
                frame.repaint();
            }
        });
        slider4.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                manipulator.moveElement(5, (double)slider4.getValue() / 100.0);
                frame.repaint();
            }
        });
    }

    public static void main(String[] args) {
        manipulator.addElement(0);
        manipulator.addElement(1);
        manipulator.addElement(0);
        manipulator.addElement(1);
        manipulator.addElement(0);
        manipulator.addElement(1);


        frame.setSize(1000,600);
        frame.add(manipulator);
        frame.add(new MainFrame("Manipulator").mainPanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
