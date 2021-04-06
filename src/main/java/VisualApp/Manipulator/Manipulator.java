package VisualApp.Manipulator;

import VisualApp.GlobalSearch.GlobalSearch;
import VisualApp.Manipulator.Elements.Element;
import VisualApp.Manipulator.Elements.Hinge;
import VisualApp.Manipulator.Elements.Rod;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

public class Manipulator extends JPanel implements MouseListener{
    private Vector<Element> mechanism = new Vector();
    private AffineTransform Default;
    private AffineTransform startPoint;
    private AffineTransform zero;
    Point2D selectedPoint;
    Point2D targetPoint;
    Point2D currentPoint;
    private Vector<Element> targetConstruction = new Vector();
    int selectedHinge = -1;

    {
        targetConstruction.add(new Hinge(0, -1));
        targetConstruction.add(new Rod(1, -1));
    }


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
        Default = g2d.getTransform();
        paintMechanism(g2d);
    }

    private void paintBase(Graphics2D g2d) {
        zero = g2d.getTransform();
        g2d.translate(75, getHeight() / 2);
        startPoint = g2d.getTransform();
        g2d.setColor(Color.gray);
        g2d.fillRect(-70,-100,70,200);
        g2d.setColor(Color.black);
        g2d.drawRect(-70,-100,70,200);
    }

    private void checkSelectedHinge(int index, Graphics2D g2d) {
        if (selectedPoint != null) {
            AffineTransform at = g2d.getTransform();
            currentPoint = at.transform(new Point.Double(0, 0), new Point2D.Double());
            Ellipse2D ellipse = new Ellipse2D.Double(currentPoint.getX() - 10, currentPoint.getY() - 10, 20, 20);
            if (ellipse.contains(selectedPoint.getX(), selectedPoint.getY())) {
                selectedHinge = index;
            }
        }
    }

    private void paintTarget(Graphics2D g2d) {
        g2d.setTransform(startPoint);
        if(targetPoint != null) {
            g2d.setTransform(startPoint);
            Point2D screenPoint = g2d.getTransform().transform(new Point.Double(0, 0), new Point2D.Double());
            g2d.translate(targetPoint.getX() - (int)screenPoint.getX(), targetPoint.getY() - (int)screenPoint.getY());
            g2d.rotate(((Hinge)targetConstruction.get(0)).getAngle());
            g2d.setColor(Color.green);
            g2d.fillOval(
                    -10,
                    -10,
                    20,
                    20);
            g2d.drawLine(
                    0,
                    0,
                    (int)(((Rod)targetConstruction.get(1)).getCompression() * 125.0),
                    0);
            g2d.setColor(Color.black);
        }
    }

    public void moveTarget(int element, double value) {
        if(element == 0) {
            targetConstruction.set(element, new Hinge(value, -1));
        } else {
            targetConstruction.set(element, new Rod(value, -1));
        }
    }

    private void paintElement(int i, Graphics2D g2d) {
        if(mechanism.get(i) instanceof Rod) {
            g2d.setStroke(new BasicStroke(5f));
            if(i == selectedHinge + 1) {
                g2d.setColor(Color.red);
            }
            g2d.drawLine(0,0, (int)(((Rod)mechanism.get(i)).getCompression() * 125.0),0);
            g2d.translate((int)(((Rod)mechanism.get(i)).getCompression() * 125.0), 0);
        } else {
            g2d.rotate(((Hinge)mechanism.get(i)).getAngle());
            if(i == selectedHinge) {
                g2d.setColor(Color.red);
            }
            g2d.fillOval(-10,-10,20,20);
        }
        g2d.setColor(Color.black);
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
            if(mechanism.get(i) instanceof Hinge) {
                checkSelectedHinge(i, g2d);
            }
            paintElement(i, g2d);
        }
        paintTarget(g2d);
        zero = g2d.getTransform();
    }

    public void resetTarget(){
        targetPoint = null;
        selectedHinge = -1;
        selectedPoint = null;
    }
    private String buildMinimisationFunction() {

        String x1 = "(75+125*x1*cos(x0)+125*x3*cos(x0+x2))";
        String y1 = "(170+125*x1*sin(x0)+125*x3*sin(x0+x2))";
        String x2 = Double.toString(targetPoint.getX());
        String y2 = Double.toString(targetPoint.getY());
        return "sqrt((" + x2 + "-" + x1 + ")*" + "(" + x2 + "-" + x1 + ")+"
                + "(" + y2 + "-" + y1 + ")*" + "(" + y2 + "-" + y1 + "))";
    }

    private double convertAngleToDeg(double angle) {
        return angle * 180 / Math.PI;
    }

    private void moveManipulator(ArrayList<Double> elements) {
        for(int i = 0; i < elements.size() - 1; i++) {
            if(i % 2 == 0) {
                moveElement(i, convertAngleToDeg(elements.get(i)));
            } else {
                moveElement(i, elements.get(i));
            }
        }
    }

    public void setToTarget() {
        String funcSrt = buildMinimisationFunction();
        Expression function = new
                ExpressionBuilder(funcSrt)
                .variables("x0", "x1", "x2", "x3")
                .build();
        ArrayList<Double> boundA = new ArrayList<Double>();
        ArrayList<Double> boundB = new ArrayList<Double>();
        for(int i = 0; i < function.getVariableNames().size(); i++) {
            if(i % 2 == 0) {
                boundA.add(-Math.PI);
                boundB.add(Math.PI);
            } else {
                boundA.add(0.3);
                boundB.add(1.0);
            }

        }
        GlobalSearch globalSearch = new GlobalSearch(function, boundA, boundB, 0.0001, 1.5);
        ArrayList<Double> result = globalSearch.findMinimum();
        moveManipulator(result);
        repaint();

        System.out.printf("Result: (%.3f, %.3f, %.3f, %.3f, %.3f)\n",
                result.get(0), result.get(1), result.get(2), result.get(3), result.get(4));
        //globalSearch.printAnalysis();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        if(selectedHinge == -1) {
            selectedPoint = e.getPoint();
            selectedPoint.setLocation(
                    selectedPoint.getX() - 8,
                    selectedPoint.getY() - 31);
        } else {
            targetPoint = e.getPoint();
            targetPoint.setLocation(
                    targetPoint.getX() - 8,
                    targetPoint.getY() - 31);
        }
        repaint();
    }
}
