package VisualApp;

import VisualApp.Manipulator.Manipulator;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
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
    }

    public static void main(String[] args) {
        manipulator.addElement(-15);
        manipulator.addElement(1);
        manipulator.addElement(30);
        manipulator.addElement(1);
        manipulator.addElement(-15);
        manipulator.addElement(1);


        frame.setSize(1000,600);
        frame.add(manipulator);
        frame.setVisible(true);
    }
}
