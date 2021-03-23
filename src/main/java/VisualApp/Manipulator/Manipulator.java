package VisualApp.Manipulator;

import VisualApp.Manipulator.Elements.Element;
import VisualApp.Manipulator.Elements.Hinge;
import VisualApp.Manipulator.Elements.Rod;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Vector;

public class Manipulator extends JPanel {
    private Vector<Element> mechanism = new Vector();
    private AffineTransform startPoint;

    // automatically selects the type of the next element
    public void addElement(double value, int connection) {
        if(connection > mechanism.size()) {
            try {
                throw new Exception("Connection is out of range!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(mechanism.size() == 0) {
            mechanism.add(new Hinge(value, connection));
        } else {
             if(connection == -1)
             {
                 if(mechanism.get(mechanism.size() - 1) instanceof Rod) {
                     mechanism.add(new Hinge(value, connection));
                 } else {
                     mechanism.add(new Rod(value, connection));
                 }
             } else {
                 connection += 1;
                 if(mechanism.get(connection) instanceof Rod) {
                     mechanism.add(new Hinge(value, connection));
                 } else {
                     mechanism.add(new Rod(value, connection));
                 }
             }
        }
    }
    // automatically selects the type of the next element
    public void addElement(double value) {
        addElement(value, -1);
    }

    public void moveElement(int element, double value) {
        int connection = mechanism.get(element).getConnection();
        if(mechanism.get(element) instanceof Rod) {
            mechanism.set(element, new Rod(value, connection));
        } else {
            mechanism.set(element, new Hinge(value, connection));
        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        paintMechanism(g2d);
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
            if(mechanism.get(i).getConnection() != -1) {
                g2d.setTransform(startPoint);
                for(int j = 0; j < mechanism.get(i).getConnection() - 1; j++) {
                    if(mechanism.get(j).getConnection() != -1) {
                        g2d.setTransform(startPoint);
                    }
                    if(mechanism.get(j) instanceof Rod) {
                        g2d.translate((int)((Rod)mechanism.get(j)).getCompression() * 125, 0);
                    } else {
                        g2d.rotate(((Hinge)mechanism.get(j)).getAngle());
                    }
                }
            }
            if(mechanism.get(i) instanceof Rod) {
                g2d.setStroke(new BasicStroke(5f));
                g2d.drawLine(0,0, (int)(((Rod)mechanism.get(i)).getCompression() * 125.0),0);
                g2d.translate((int)(((Rod)mechanism.get(i)).getCompression() * 125.0), 0);
            } else {
                g2d.rotate(((Hinge)mechanism.get(i)).getAngle());
                g2d.fillOval(-10,-10,20,20);
            }
        }
        g2d.translate(0,0);
    }
}
