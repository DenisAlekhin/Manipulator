package app.manipulator;

import app.exceptions.NoSolutionExceptions;
import app.globalsearch.multidimensional.MultidimensionalGlobalSearch;
import app.globalsearch.multidimensional.MultidimensionalGlobalSearchLib;
import app.manipulator.elements.Element;
import app.manipulator.elements.Hinge;
import app.manipulator.elements.Rod;
import app.model.Algorithm;
import app.service.Iterations;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.log4j.BasicConfigurator;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

import static app.utils.Constants.FRAME_START_X;
import static app.utils.Constants.FRAME_START_Y;
import static app.utils.Constants.MANIP_START_X;
import static app.utils.Constants.MANIP_START_Y;
import static app.utils.Constants.OBSTACLE_RADIUS;
import static app.utils.Constants.ROG_LENGTH;

@Slf4j
public class Manipulator extends JPanel implements MouseListener{
    final double R = 2;
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

    {
        BasicConfigurator.configure();
        targetConstruction.add(new Hinge(0, -1));
        targetConstruction.add(new Rod(1, -1));
    }
    /*
        0       1       2       3       4       5
        Hinge   Rod     Hinge   Rod     Hinge   Rod
     */

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

    public double setToTarget(boolean onlyHingesMoves, Algorithm algorithm, Double precision, Double localPrecision) {
        MultidimensionalGlobalSearch globalSearch = setUpGlobalSearch(onlyHingesMoves);
        List<Double> result = null;
        String functionStr = buildFunctionStr(onlyHingesMoves);
        try {
            switch (algorithm) {
                case LOCAL -> {
                    List<Pair<Integer, Double>> res = new ArrayList<>();
                    for(int p = 100; p <= 10000; p += 100) {
                        result = globalSearch.findMinimumLocal(targetPoint.getX(), targetPoint.getY(), buildExpression(functionStr, onlyHingesMoves), precision, localPrecision, p);
                        res.add(new Pair<Integer, Double>(Iterations.get(), result.get(2)));
                        Iterations.reset();
                    }

                    System.out.println("Res: ");
                    res.sort((p1, p2) -> Double.compare(p1.getKey(), p2.getKey()));
                    for(int i = 0; i < res.size(); i++){
                        System.out.print("(" + res.get(i).getKey() + "; " + res.get(i).getValue() + ") ");
                    }
                    System.out.println();
                }
                case JMETAL -> {
                    List<List<Pair<Integer, Double>>> resultList = new ArrayList<>();
                    for(int j = 0; j < 10; j++) {
                        resultList.add(new ArrayList<>());
                        for(int i = 100; i <= 10000; i += 100){
                            result = MultidimensionalGlobalSearchLib.findMinimumJmetal(targetPoint.getX(), targetPoint.getY(), buildExpression(functionStr, onlyHingesMoves), i);
                            resultList.get(j).add(new Pair<>(i, result.get(2)));
//                            log.info("({},{})", i, result.get(2));
                        }
                    }
                    List<Pair<Integer, Double>> min = new ArrayList<>();
                    List<Pair<Integer, Double>> max = new ArrayList<>();
                    List<Pair<Integer, Double>> avg = new ArrayList<>();
                    for(int j = 0; j < resultList.get(0).size(); j++) {
                        final int finalJ = j;
                        min.add(new Pair<>(resultList.get(0).get(finalJ).getKey(), resultList.stream().mapToDouble(list -> list.get(finalJ).getValue()).min().getAsDouble()));
                        max.add(new Pair<>(resultList.get(0).get(finalJ).getKey(), resultList.stream().mapToDouble(list -> list.get(finalJ).getValue()).max().getAsDouble()));
                        avg.add(new Pair<>(resultList.get(0).get(finalJ).getKey(), resultList.stream().mapToDouble(list -> list.get(finalJ).getValue()).average().getAsDouble()));
                        log.info("{} iterations: min={}, max={}, avg={}", resultList.get(0).get(finalJ).getKey(), min, max, avg);
                    }
                    System.out.println("Min : ");
                    for(int i = 0; i < min.size(); i++){
                        System.out.print("(" + min.get(i).getKey() + "; " + min.get(i).getValue() + ") ");
                    }
                    System.out.println();

                    System.out.println("Max : ");
                    for(int i = 0; i < max.size(); i++){
                        System.out.print("(" + max.get(i).getKey() + "; " + max.get(i).getValue() + ") ");
                    }
                    System.out.println();

                    System.out.println("Avg : ");
                    for(int i = 0; i < avg.size(); i++){
                        System.out.print("(" + avg.get(i).getKey() + "; " + avg.get(i).getValue() + ") ");
                    }
                    System.out.println();
                }
                case STRONGIN -> {
                    List<Pair<Double, Double>> res = new ArrayList<>();
                    for(double p = 0.01; p <= 1.0; p += 0.001) {
                        result = globalSearch.findMinimum(true, obstacles, p, 50000);
                        res.add(new Pair<Double, Double>(result.get(3), result.get(2)));
                    }

                    System.out.println("Res: ");
                    res.sort((p1, p2) -> Double.compare(p1.getKey(), p2.getKey()));
                    for(int i = 0; i < res.size(); i++){
                        System.out.print("(" + res.get(i).getKey() + "; " + res.get(i).getValue() + ") ");
                    }
                    System.out.println();
                }
            }
            moveManipulator(result, onlyHingesMoves);
            setTwoLastElements();
            repaint();
            printResult(result);
        } catch (NoSolutionExceptions e) {
            e.printStackTrace();
        }

        return result.get(result.size() - 1);
    }

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
