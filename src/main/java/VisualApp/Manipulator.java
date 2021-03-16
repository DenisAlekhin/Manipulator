package VisualApp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Vector;

public class Manipulator extends JPanel {
    private Vector<Element> mechanism = new Vector();
    private AffineTransform startPoint;

    public void addElement(double compression, double angle, int connection) {
        if(connection < 0 || connection > mechanism.size()) {
            try {
                throw new Exception("Wrong input");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mechanism.add(new Element(compression, angle, connection));
    }
    public void addElement(double compression, double angle) {
        mechanism.add(new Element(compression, angle, -1));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        paintMechanism(g2d);
    }

    public void moveElement(int element, double compression, double angle) {
        int connection = mechanism.get(element).connection;
        mechanism.set(element, new Element(compression, angle, connection));
    }

    private void paintBase(Graphics2D g2d) {
        g2d.translate(75, getHeight() / 2);
        startPoint = g2d.getTransform();
        g2d.setColor(Color.gray);
        g2d.fillRect(-70,-100,70,200);
        g2d.setColor(Color.black);
        g2d.drawRect(-70,-100,70,200);
    }

    private void paintMechanism(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        paintBase(g2d);
        for(int i = 0; i < mechanism.size(); i++) {
            if(mechanism.get(i).connection != -1) {
                g2d.setTransform(startPoint);
                for(int j = 0; j < mechanism.get(i).connection; j++) {
                    if(mechanism.get(j).connection != -1) {
                        g2d.setTransform(startPoint);
                    }
                    g2d.rotate(mechanism.get(j).angle);
                    g2d.translate((int)mechanism.get(j).compression * 125, 0);
                }
            }
            g2d.rotate(mechanism.get(i).angle);
            g2d.fillOval(-10,-10,20,20);
            g2d.setStroke(new BasicStroke(5f));
            g2d.drawLine(0,0, (int)(mechanism.get(i).compression * 125),0);
            g2d.translate((int)mechanism.get(i).compression * 125, 0);
        }
        g2d.translate(0,0);
    }
}
