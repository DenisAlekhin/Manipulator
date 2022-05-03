package service.manipulator;

import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.log4j.BasicConfigurator;
import service.exceptions.NoSolutionExceptions;
import service.globalsearch.MultidimensionalGlobalSearch;
import service.globalsearch.MultidimensionalGlobalSearchLib;
import service.manipulator.elements.Element;
import service.manipulator.elements.Hinge;
import service.manipulator.elements.Rod;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static service.utils.Constants.FRAME_START_X;
import static service.utils.Constants.FRAME_START_Y;
import static service.utils.Constants.MANIP_START_X;
import static service.utils.Constants.MANIP_START_Y;
import static service.utils.Constants.OBSTACLE_RADIUS;
import static service.utils.Constants.ROG_LENGTH;

@Slf4j
public class Manipulator extends JPanel implements MouseListener{
    final double R = 3;
    final double EPSILON = 0.01;
    private ArrayList<Element> mechanism = new ArrayList();
    private ArrayList<Point2D> obstacles = new ArrayList();
    private AffineTransform Default;
    private AffineTransform startPoint;
    Point2D selectedPoint;
    Point2D targetPoint;
    Point2D currentPoint;
    private Vector<Element> targetConstruction = new Vector();
    int selectedHinge = -1;
    Timer timer;

    {
        BasicConfigurator.configure();
        targetConstruction.add(new Hinge(0, -1));
        targetConstruction.add(new Rod(1, -1));
    }
    /*
        0       1       2       3       4       5
        Hinge   Rod     Hinge   Rod     Hinge   Rod
     */

    /*public void distThirdRod(){
        Line2D thirdRod = new Line2D.Double(
                new Point2D.Double(75+ROG_LENGTH*((Rod)mechanism.get(1)).getCompression()*Math.cos(((Hinge)mechanism.get(0)).getAngle())+
                        ROG_LENGTH*((Rod)mechanism.get(3)).getCompression()*Math.cos(((Hinge)mechanism.get(0)).getAngle()+((Hinge)mechanism.get(2)).getAngle()),
                        130+ROG_LENGTH*((Rod)mechanism.get(1)).getCompression()*Math.sin(((Hinge)mechanism.get(0)).getAngle())+
                                ROG_LENGTH*((Rod)mechanism.get(3)).getCompression()*Math.sin(((Hinge)mechanism.get(0)).getAngle()+((Hinge)mechanism.get(2)).getAngle())),
                new Point2D.Double(75+ROG_LENGTH*((Rod)mechanism.get(1)).getCompression()*Math.cos(((Hinge)mechanism.get(0)).getAngle())+
                ROG_LENGTH*((Rod)mechanism.get(3)).getCompression()*Math.cos(((Hinge)mechanism.get(0)).getAngle()+((Hinge)mechanism.get(2)).getAngle())+
                        ROG_LENGTH*((Rod)mechanism.get(3)).getCompression()*Math.cos(((Hinge)mechanism.get(0)).getAngle()+((Hinge)mechanism.get(2)).getAngle()+((Hinge)mechanism.get(4)).getAngle()),
                130+ROG_LENGTH*((Rod)mechanism.get(1)).getCompression()*Math.sin(((Hinge)mechanism.get(0)).getAngle())+
                        ROG_LENGTH*((Rod)mechanism.get(3)).getCompression()*Math.sin(((Hinge)mechanism.get(0)).getAngle()+((Hinge)mechanism.get(2)).getAngle())+
                        ROG_LENGTH*((Rod)mechanism.get(3)).getCompression()*Math.sin(((Hinge)mechanism.get(0)).getAngle()+((Hinge)mechanism.get(2)).getAngle()+((Hinge)mechanism.get(4)).getAngle())));
        for(int i = 0; i < obstacles.size(); i++) {
            System.out.println(thirdRod.ptSegDist(obstacles.get(i)) - 25);
        }
    }*/


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
        paintObstacles(g2d);
    }

    private void paintBase(Graphics2D g2d) {
        g2d.translate(75, getHeight() / 2);
        startPoint = g2d.getTransform();
        g2d.setColor(Color.gray);
        g2d.fillRect(-70,-100,70,200);
        g2d.setColor(Color.black);
        g2d.drawRect(-70,-100,70,200);
    }

    private void checkSelectedHinge(int index, Graphics2D g2d) {
        if (selectedPoint != null) {
            currentPoint = g2d.getTransform().transform(new Point.Double(0, 0), new Point2D.Double());
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
                    (int)(((Rod)targetConstruction.get(1)).getCompression() * ROG_LENGTH),
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
            g2d.drawLine(0,0, (int)(((Rod)mechanism.get(i)).getCompression() * ROG_LENGTH),0);
            g2d.translate((int)(((Rod)mechanism.get(i)).getCompression() * ROG_LENGTH), 0);
        } else {
            g2d.rotate(((Hinge)mechanism.get(i)).getAngle());
            if(i == selectedHinge) {
                g2d.setColor(Color.red);
            }
            g2d.fillOval(-10,-10,20,20);
        }
        g2d.setColor(Color.black);
    }

    private void paintObstacles(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setTransform(Default);
        g2d.setColor(Color.RED);
        for(int i = 0; i < obstacles.size();i++) {
            g2d.fillOval((int)obstacles.get(i).getX()- OBSTACLE_RADIUS - FRAME_START_X,
                    (int)obstacles.get(i).getY() - OBSTACLE_RADIUS - FRAME_START_Y,
                    OBSTACLE_RADIUS * 2,OBSTACLE_RADIUS * 2);
        }
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
                        g2d.translate((int)((Rod)mechanism.get(j)).getCompression() * ROG_LENGTH, 0);
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
    }

    public void resetTarget(){
        targetPoint = null;
        selectedHinge = -1;
        selectedPoint = null;
    }

    private String buildFunctionStr(boolean onlyHingesMoves) {
        String x2 = Double.toString(targetPoint.getX());
        String y2 = Double.toString(targetPoint.getY());
        if(onlyHingesMoves) {
            String x1 = "(" + MANIP_START_X + "+" + ROG_LENGTH + "*" + ((Rod)mechanism.get(1)).getCompression() +
                    "*cos(x0)+" + ROG_LENGTH + "*" + ((Rod)mechanism.get(3)).getCompression() + "*cos(x0+x1))";
            String y1 = "(" + MANIP_START_Y + "+" + ROG_LENGTH + "*" + ((Rod)mechanism.get(1)).getCompression() +
                    "*sin(x0)+" + ROG_LENGTH + "*" + ((Rod)mechanism.get(3)).getCompression() + "*sin(x0+x1))";
//            System.out.println("sqrt((" + x2 + "-" + x1 + ")*" + "(" + x2 + "-" + x1 + ")+"
//                    + "(" + y2 + "-" + y1 + ")*" + "(" + y2 + "-" + y1 + "))");
            return "sqrt((" + x2 + "-" + x1 + ")*" + "(" + x2 + "-" + x1 + ")+"
                    + "(" + y2 + "-" + y1 + ")*" + "(" + y2 + "-" + y1 + "))";
        } else {
            String x1 = "(" + MANIP_START_X + "+" + ROG_LENGTH + "*x1*cos(x0)+" + ROG_LENGTH + "*x3*cos(x0+x2))";
            String y1 = "(" + MANIP_START_Y + "+" + ROG_LENGTH + "*x1*sin(x0)+" + ROG_LENGTH + "*x3*sin(x0+x2))";
            return "sqrt((" + x2 + "-" + x1 + ")*" + "(" + x2 + "-" + x1 + ")+"
                    + "(" + y2 + "-" + y1 + ")*" + "(" + y2 + "-" + y1 + "))";
        }
    }

    private Expression buildExpression(String functionStr, boolean onlyHingesMoves) {
        if(onlyHingesMoves) {
            return new ExpressionBuilder(functionStr)
                    .variables("x0", "x1")
                    .build();
        } else {
            return new ExpressionBuilder(functionStr)
                    .variables("x0", "x1", "x2", "x3")
                    .build();
        }
    }

    private double convertAngleToDeg(double angle) {
        return angle * 180 / Math.PI;
    }

    private void moveManipulator(List<Double> elements, boolean onlyHingesMoves) {
        if(onlyHingesMoves) {
            for(int i = 0; i < elements.size() - 1; i++) {
                moveElement(i*2, convertAngleToDeg(elements.get(i)));
            }
        } else {
            for(int i = 0; i < elements.size() - 1; i++) {
                if(i % 2 == 0) {
                    moveElement(i, convertAngleToDeg(elements.get(i)));
                } else {
                    moveElement(i, elements.get(i));
                }
            }
        }
    }
    private boolean resultIsReachable(ArrayList<Double> result) {
        if(result.get(0) == -100) {
            return false;
        }
        return true;
    }


    public double setToTarget(boolean onlyHingesMoves) {
        MultidimensionalGlobalSearch globalSearch = setUpGlobalSearch(onlyHingesMoves);
        List<Double> result = null;
        String functionStr = buildFunctionStr(onlyHingesMoves);
        try {


//            result = globalSearch.findMinimum(true, obstacles);
//            result = MultidimensionalGlobalSearchLib.findMinimum(targetPoint.getX(), targetPoint.getY());
            result = MultidimensionalGlobalSearchLib.findMinimumJmetal(targetPoint.getX(), targetPoint.getY(), buildExpression(functionStr, onlyHingesMoves));


            moveManipulator(result, onlyHingesMoves);
            setTwoLastElements();
            repaint();
            printResult(result);
        } catch (NoSolutionExceptions e) {
            e.printStackTrace();
        }

        return result.get(result.size() - 1);
    }

//    public void animatedSetToTarget(int timeout, boolean onlyHingesMoves) {
//        GlobalSearch globalSearch = setUpGlobalSearch(onlyHingesMoves);
//        globalSearch.findMinimum(obstacles);
//
//        ArrayList<ArrayList<Double>> stepsOfAlgorithm = globalSearch.getStepsOfAlgorithm();
//        stepsAnimation(stepsOfAlgorithm, timeout, onlyHingesMoves);
//
//        repaint();
//        printResult(stepsOfAlgorithm.get(stepsOfAlgorithm.size() - 1));
//    }

    private void setTwoLastElements() {
        double hingeAngle = -(((Hinge)mechanism.get(0)).getAngle()
                + ((Hinge)mechanism.get(2)).getAngle())
                + ((Hinge)targetConstruction.get(0)).getAngle();
        hingeAngle = hingeAngle * 180 / Math.PI;
        moveElement(4, hingeAngle);
        moveElement(5, ((Rod)targetConstruction.get(1)).getCompression());
    }

    private void printResult(List<Double> result) {
        DecimalFormat df = new DecimalFormat("#.###");
        log.info("The distance to the target point is {}", df.format(result.get(2)));
    }

    private void printAlgorithmStep(int k, ArrayList<Double> step) {
        DecimalFormat df = new DecimalFormat("#.###");
        System.out.print("Step " + k + ": (");
        for(int i = 0; i < step.size(); i++) {
            System.out.print(df.format(step.get(i)));
            if(i != step.size() - 1) {
                System.out.print(",");
            } else {
                System.out.print(")\n");
            }
        }
    }

    private MultidimensionalGlobalSearch setUpGlobalSearch(boolean onlyHingesMoves) {
        String functionStr = buildFunctionStr(onlyHingesMoves);
        Expression function = buildExpression(functionStr, onlyHingesMoves);
        ArrayList<Double> boundA = new ArrayList<Double>();
        ArrayList<Double> boundB = new ArrayList<Double>();
        if(onlyHingesMoves) {
            for(int i = 0; i < function.getVariableNames().size(); i++) {
                boundA.add(-Math.PI);
                boundB.add(Math.PI);
            }
        } else {
            for(int i = 0; i < function.getVariableNames().size(); i++) {
                if(i % 2 == 0) {
                    boundA.add(-Math.PI);
                    boundB.add(Math.PI);
                } else {
                    boundA.add(0.3);
                    boundB.add(1.0);
                }
            }
        }
        return new MultidimensionalGlobalSearch(function, functionStr, boundA, boundB, R, EPSILON);
//        return new GlobalSearch(function, boundA, boundB, EPSILON, R, onlyHingesMoves);
    }

    private void stepsAnimation(final ArrayList<ArrayList<Double>> stepsOfAlgorithm,
                                int timeout, final boolean onlyHingesMoves) {
        timer = new Timer(timeout, new ActionListener() {
            int i = 0;
            public void actionPerformed(ActionEvent evt) {
                if(i < stepsOfAlgorithm.size()) {
                    printAlgorithmStep(i, stepsOfAlgorithm.get(i));
                    moveManipulator(stepsOfAlgorithm.get(i), onlyHingesMoves);
                    repaint();
                    i++;
                } else {
                    timer.stop();
                    setTwoLastElements();
                    repaint();
                }
            }
        });
        timer.start();
    }

    public void addObstacle(Point2D position) {
        obstacles.add(position);
        repaint();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        if(selectedHinge == -1) {
            selectedPoint = e.getPoint();
            selectedPoint.setLocation(
                    selectedPoint.getX() - FRAME_START_X,
                    selectedPoint.getY() - FRAME_START_Y);
        } else {
            targetPoint = e.getPoint();
            targetPoint.setLocation(
                    targetPoint.getX() - FRAME_START_X,
                    targetPoint.getY() - FRAME_START_Y);
        }
        repaint();
    }
}