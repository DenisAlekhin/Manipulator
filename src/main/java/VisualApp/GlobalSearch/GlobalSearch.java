package VisualApp.GlobalSearch;

import VisualApp.Manipulator.Elements.Hinge;
import VisualApp.Manipulator.Elements.Rod;
import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;
import sun.reflect.generics.tree.Tree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

public class GlobalSearch {
    private final Expression function;
    private final ArrayList<Double> a, b;
    private final double r, epsilon;
    private final int numOfIter;
    private double m;
    private final boolean onlyHingesMoves;
    private final ArrayList<Pair<Double, Double>> analysis;
    private final ArrayList<Pair<Double, Double>> stepsOfOneDimensionalAlgorithm;
    private final ArrayList<ArrayList<Double>> stepsOfAlgorithm;
    int scrCoordManipStartX = 78;
    int scrCoordManipStartY = 156;
    ArrayList<SortedSet<Integer>> I = new ArrayList<SortedSet<Integer>>();
    int M;
    int v = 4;
    double z, u;


    public GlobalSearch(Expression function, ArrayList<Double> a, ArrayList<Double> b,
                        double epsilon, double r, boolean onlyHingesMoves) {
        try {
            if(a.size() != b.size()) {
                throw new Exception("Error: wrong count of borders");
            }
            for(int i = 0; i < a.size(); i++) {
                if(a.get(i) > b.get(i)) {
                    throw new Exception("Error: a > b");
                }
            }
            if(r <= 0) {
                throw new Exception("Error: r <= 1");
            }
            if(epsilon <= 0) {
                throw new Exception("Error: epsilon <= 0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.function = function;
        this.a = a;
        this.b = b;
        this.epsilon = epsilon;
        this.r = r;
        this.onlyHingesMoves = onlyHingesMoves;
        this.numOfIter = (int) (1 / (epsilon * 10));
        analysis = new ArrayList<Pair<Double, Double>>();
        stepsOfOneDimensionalAlgorithm = new ArrayList<Pair<Double, Double>>();
        stepsOfAlgorithm = new ArrayList<ArrayList<Double>>();
    }

    public ArrayList<Double> findMinimum(ArrayList<Point2D> obstacles){
        ArrayList<Double> result = new ArrayList<Double>();
        int countOfDimensions = function.getVariableNames().size();
        ArrayList<Integer> countOfCalc= new ArrayList<Integer>();

        for(int i = 0; i < countOfDimensions; i++) {
            if(!onlyHingesMoves && i % 2 != 0) {
                result.add(1.0);
            } else {
                result.add(0.0);
            }
            countOfCalc.add(0);
            function.setVariable("x" + i, result.get(i));
        }

        stepsOfAlgorithm.add(new ArrayList<Double>(result));
        stepsOfAlgorithm.get(stepsOfAlgorithm.size() - 1).add(function.evaluate());

        int  p = 0;
        double previousRes = -1;
        do {
            for(int i = countOfDimensions - 1; i >= 0; i--) {
                countOfCalc.set(i, countOfCalc.get(i) + 1);
                result.set(i, findOneDimensionalMinimumWithLimitations("x" + i, i, result, obstacles).getKey());
                function.setVariable("x" + i, result.get(i));
                saveStepsOfAlgorithm(result, i);
                if(i != 0 && countOfCalc.get(i) < 2) {
                    for(int k = i + 1; k < countOfDimensions - 1; k++) {
                        countOfCalc.set(k, 0);
                    }
                    i = -1;
                }
            }
            if(previousRes != -1 && previousRes == function.evaluate()) {
                for(int i = 0; i < result.size(); i++) {
                    result.set(i, result.get(i) + 0.1);
                    function.setVariable("x" + i, result.get(i));
                }
            }
            previousRes = function.evaluate();
            p++;
        } while (function.evaluate() > 0.5 && p < 500);
        if(p == 500) {
            System.out.println("Unreachable");
        }
        for(int j = 0; j < countOfDimensions; j++) {
            function.setVariable("x" + j, result.get(j));
        }
        result.add(countOfDimensions, function.evaluate());
        return result;
    }

    public Pair<Double, Double> findOneDimensionalMinimum(String variable, int numberOfVariable){
        analysis.clear();
        stepsOfOneDimensionalAlgorithm.clear();
        analysis.add(new Pair<Double, Double>(a.get(numberOfVariable),
                function.setVariable(variable, a.get(numberOfVariable)).evaluate()));
        analysis.add(new Pair<Double, Double>(b.get(numberOfVariable),
                function.setVariable(variable, b.get(numberOfVariable)).evaluate()));

        int t = 0;
        do {
            sortAnalysisByFirstValue();
            try {
                calculate_m();
            } catch (Exception e) {
                e.printStackTrace();
            }
            t = calculateMaxR();

            double newStudyX = (analysis.get(t).getKey() + analysis.get(t - 1).getKey()) / 2 -
                    (analysis.get(t).getValue() - analysis.get(t - 1).getValue()) / (2 * m);
            analysis.add(new Pair<Double, Double>(newStudyX, function.setVariable(variable, newStudyX).evaluate()));
            stepsOfOneDimensionalAlgorithm.add(new Pair<Double, Double>(newStudyX, function.setVariable(variable, newStudyX).evaluate()));
            if (newStudyX < analysis.get(t - 1).getKey() || newStudyX > analysis.get(t).getKey())
                try {
                    throw new Exception("Error: New study out of range");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } while (analysis.get(t).getKey() - analysis.get(t - 1).getKey() > epsilon);

        sortAnalysisBySecondValue();

        return analysis.get(0);
    }

    public Pair<Double, Double> findOneDimensionalMinimumWithLimitations(
            String variable, int numberOfVariable,
            ArrayList<Double> points, ArrayList<Point2D> obstacles){
        analysis.clear();
        resetSets();
        I.clear();
        initializeI(obstacles);
        stepsOfOneDimensionalAlgorithm.clear();
        analysis.add(new Pair<Double, Double>(a.get(numberOfVariable),
                function.setVariable(variable, a.get(numberOfVariable)).evaluate()));
        analysis.add(new Pair<Double, Double>(b.get(numberOfVariable),
                function.setVariable(variable, b.get(numberOfVariable)).evaluate()));
        analysis.add(new Pair<Double, Double>((analysis.get(0).getKey() + analysis.get(1).getKey()) / 2,
                function.setVariable(variable, (analysis.get(0).getKey() - analysis.get(1).getKey()) / 2).evaluate()));
        int t;
        do {
            sortAnalysisByFirstValue();
            resetSets();
            calculateI(points, obstacles);
            try {
                calculate_u();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            calculate_z(points, obstacles);
            t = calculateMaxIndexR(points, obstacles);

            double newStudyX = calculateNewStudyPoint(points, obstacles, t);
            analysis.add(new Pair<Double, Double>(newStudyX, function.setVariable(variable, newStudyX).evaluate()));
            stepsOfOneDimensionalAlgorithm.add(new Pair<Double, Double>(newStudyX, function.setVariable(variable, newStudyX).evaluate()));
            if (newStudyX < analysis.get(t - 1).getKey() || newStudyX > analysis.get(t).getKey())
                try {
                    throw new Exception("Error: New study out of range");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } while (analysis.get(t).getKey() - analysis.get(t - 1).getKey() > epsilon);

        sortAnalysisBySecondValue();

        return analysis.get(0);
    }

    private double calculateNewStudyPoint(ArrayList<Double> points, ArrayList<Point2D> obstacles, int t) {
        if(calculateV(points, obstacles, analysis.get(t - 1).getKey()) !=
                calculateV(points, obstacles, analysis.get(t).getKey())) {
            return (analysis.get(t).getKey() + analysis.get(t - 1).getKey()) / 2;
        } else {
            return (analysis.get(t).getKey() + analysis.get(t - 1).getKey()) / 2 -
                    (analysis.get(t).getValue() - analysis.get(t - 1).getValue())  /
                            (2 * r * u);
        }
    }

    private int sgn(double x) {
        if(x < 0) {
            return -1;
        } else if(x > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    private int calculateMaxIndexR(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        double R = calculateIndexR(points, obstacles, 1);
        double tempR;
        int indexR = 1;
        for (int i = 2; i < analysis.size(); i++)
        {
            tempR = calculateIndexR(points, obstacles, i);
            if (tempR > R) {
                R = tempR;
                indexR = i;
            }
        }
        return indexR;
    }

    private double calculateIndexR(ArrayList<Double> points, ArrayList<Point2D> obstacles, int index) {
        if(calculateV(points, obstacles, analysis.get(index - 1).getKey()) ==
                calculateV(points, obstacles, analysis.get(index).getKey())) {
            return calculateIndexRFormula1(index);
        } else if(calculateV(points, obstacles, analysis.get(index - 1).getKey()) <
                calculateV(points, obstacles, analysis.get(index).getKey())) {
            return calculateIndexRFormula2(index);
        } else {
            return calculateIndexRFormula3(index);
        }
    }

    private double calculateIndexRFormula1(int index) {
        return (analysis.get(index).getKey() - analysis.get(index - 1).getKey()) +
                Math.pow((analysis.get(index).getValue() - analysis.get(index - 1).getValue()), 2) /
                        (Math.pow(r * u,2) * (analysis.get(index).getKey() - analysis.get(index - 1).getKey())) -
                2 * (analysis.get(index).getValue() + analysis.get(index - 1).getValue() - 2 * z) /
                        (r * u);
    }

    private double calculateIndexRFormula2(int index) {
        return 2 * (analysis.get(index).getKey() - analysis.get(index - 1).getKey()) -
                4 * (analysis.get(index).getValue() - z) /
                        (r * u);
    }

    private double calculateIndexRFormula3(int index) {
        return 2 * (analysis.get(index).getKey() - analysis.get(index - 1).getKey()) -
                4 * (analysis.get(index - 1).getValue() - z) /
                        (r * u);
    }

    private void calculate_z(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        /*ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(M));
        double minZ = analysis.get(Iv.get(0)).getValue();
        for(int i = 1; i < Iv.size(); i++) {
            if(analysis.get(Iv.get(i)).getValue() < minZ) {
                minZ = analysis.get(Iv.get(i)).getValue();
            }
        }

        z = minZ;*/
        ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(M));
        double minZ = calculateG(points, obstacles, analysis.get(Iv.get(0)).getValue());
        for(int i = 1; i < Iv.size(); i++) {
            if(calculateG(points, obstacles, analysis.get(Iv.get(i)).getValue()) < minZ) {
                minZ = calculateG(points, obstacles, analysis.get(Iv.get(i)).getValue());
            }
        }

        z = minZ;
    }


     private void resetSets() {
        for(int i = 0; i < I.size(); i++) {
            I.get(i).clear();
        }
    }

    private void initializeI(ArrayList<Point2D> obstacles) {
        for(int i = 0; i < 5; i++) {
            I.add(new TreeSet<Integer>());
        }
    }


    private ArrayList<Double> copyPoints(ArrayList<Double> points) {
        ArrayList<Double> pointsCopy = new ArrayList<Double>(points);
        if(pointsCopy.size() == 2) {
            pointsCopy.add(1, 1.0);
            pointsCopy.add(3, 1.0);
        }

        return pointsCopy;
    }

    private void calculateI(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        ArrayList<Double> pointsCopy = copyPoints(points);

        pointsCopy.add(analysis.get(0).getKey());

        int maxM = -1;
        for(int i = 1; i < analysis.size() - 1; i++) {
            I.get(calculateV(points, obstacles, analysis.get(i).getKey())).add(i);
            if(calculateV(points, obstacles, analysis.get(i).getKey()) > maxM) {
                maxM = calculateV(points, obstacles, analysis.get(i).getKey());
            }
        }
        M = maxM;

        I.get(0).add(0);
        I.get(0).add(analysis.size() - 1);
    }

    private int calculateV(ArrayList<Double> points, ArrayList<Point2D> obstacles, double point) {
        ArrayList<Double> pointsCopy = copyPoints(points);
        pointsCopy.add(analysis.get(0).getKey());
        pointsCopy.set(3, point);
        if(firstRodCorrespondLimitations(pointsCopy, obstacles)){
            if(secondRodCorrespondLimitations(pointsCopy, obstacles)) {
                if(thirdRodCorrespondLimitations(pointsCopy, obstacles)){
                    return 4;
                } else {
                    return 3;
                }
            } else{
                return 2;
            }
        } else {
            return 1;
        }
    }

    private double calculateG(ArrayList<Double> points, ArrayList<Point2D> obstacles, double point) {
        ArrayList<Double> pointsCopy = copyPoints(points);
        pointsCopy.add(analysis.get(0).getKey());
        pointsCopy.set(3, point);

        Line2D firstRod = new Line2D.Double(
                new Point2D.Double(scrCoordManipStartX,scrCoordManipStartY),
                new Point2D.Double(scrCoordManipStartX+125*points.get(1)*Math.cos(points.get(0)),
                        scrCoordManipStartY+125*points.get(1)*Math.sin(points.get(0))));

        return firstRod.ptSegDist(obstacles.get(0)) - 25;
    }

    private boolean firstRodCorrespondLimitations(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        ArrayList<Double> distToObstacles = firstRodDistToObstacles(points, obstacles);
        for(int i = 0; i < distToObstacles.size(); i++) {
            if(distToObstacles.get(i) < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean secondRodCorrespondLimitations(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        ArrayList<Double> distToObstacles = secondRodDistToObstacles(points, obstacles);
        for(int i = 0; i < distToObstacles.size(); i++) {
            if(distToObstacles.get(i) < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean thirdRodCorrespondLimitations(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        ArrayList<Double> distToObstacles = thirdRodDistToObstacles(points, obstacles);
        for(int i = 0; i < distToObstacles.size(); i++) {
            if(distToObstacles.get(i) < 0) {
                return false;
            }
        }
        return true;
    }
    /*
        0       1       2       3       4       5
        Hinge   Rod     Hinge   Rod     Hinge   Rod
    */
    private ArrayList<Double> firstRodDistToObstacles(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        ArrayList<Double> distToObstacles = new ArrayList<Double>();
        Line2D firstRod = new Line2D.Double(
                new Point2D.Double(scrCoordManipStartX,scrCoordManipStartY),
                new Point2D.Double(scrCoordManipStartX+125*points.get(1)*Math.cos(points.get(0)),
                        scrCoordManipStartY+125*points.get(1)*Math.sin(points.get(0))));

        for(int i = 0; i < obstacles.size(); i++) {
            distToObstacles.add(firstRod.ptSegDist(obstacles.get(i)) - 25);
        }
        return distToObstacles;
    }

    private ArrayList<Double> secondRodDistToObstacles(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        ArrayList<Double> distToObstacles = new ArrayList<Double>();
        Line2D secondRod = new Line2D.Double(
                new Point2D.Double(scrCoordManipStartX+125*points.get(1)*Math.cos(points.get(0)),
                        scrCoordManipStartY+125*points.get(1)*Math.sin(points.get(0))),
                new Point2D.Double(scrCoordManipStartX+125*points.get(1)*Math.cos(points.get(0))+
                        125*points.get(3)*Math.cos(points.get(0)+points.get(2)),
                        scrCoordManipStartY+125*points.get(1)*Math.sin(points.get(0))+
                                125*points.get(3)*Math.sin(points.get(0)+points.get(2))));

        for(int i = 0; i < obstacles.size(); i++) {
            distToObstacles.add(secondRod.ptSegDist(obstacles.get(i)) - 25);
        }
        return distToObstacles;
    }

    private ArrayList<Double>thirdRodDistToObstacles(ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        ArrayList<Double> distToObstacles = new ArrayList<Double>();
        Line2D thirdRod = new Line2D.Double(
                new Point2D.Double(75+125*points.get(1)*Math.cos(points.get(0))+
                        125*points.get(3)*Math.cos(points.get(0)+points.get(2)),
                        130+125*points.get(1)*Math.sin(points.get(0))+
                                125*points.get(3)*Math.sin(points.get(0)+points.get(2))),
                new Point2D.Double(75+125*points.get(1)*Math.cos(points.get(0))+
                        125*points.get(3)*Math.cos(points.get(0)+points.get(2))+
                        125*points.get(3)*Math.cos(points.get(0)+points.get(2)+points.get(4)),
                        130+125*points.get(1)*Math.sin(points.get(0))+
                                125*points.get(3)*Math.sin(points.get(0)+points.get(2))+
                                125*points.get(3)*Math.sin(points.get(0)+points.get(2)+points.get(4))));
        for(int i = 0; i < obstacles.size(); i++) {
            distToObstacles.add(thirdRod.ptSegDist(obstacles.get(i)) - 25);
        }
        return distToObstacles;
    }

    private void saveStepsOfAlgorithm(ArrayList<Double> result, int numberOfVariable) {
        for(int p = 0; p < stepsOfOneDimensionalAlgorithm.size(); p++) {
            result.set(numberOfVariable, stepsOfOneDimensionalAlgorithm.get(p).getKey());
            stepsOfAlgorithm.add(new ArrayList<Double>(result));
            stepsOfAlgorithm.get(stepsOfAlgorithm.size() - 1).add(function.evaluate());
        }
    }

    public ArrayList<ArrayList<Double>> getStepsOfAlgorithm() { return stepsOfAlgorithm; }

    public void printAnalysis() {
        sortAnalysisByFirstValue();
        System.out.println("The count of tests: " + (analysis.size() - 1));
        for (int i = 0; i < analysis.size(); i++) {
            System.out.printf("(%.3f, %.3f)\n", analysis.get(i).getKey(), analysis.get(i).getValue());
        }
    }

    private void sortAnalysisByFirstValue() {
        Collections.sort(analysis, new Comparator<Pair<Double, Double>>() {
            public int compare(Pair<Double, Double> o1, Pair<Double, Double> o2) {
                if(o1.getKey() < o2.getKey()) {
                    return -1;
                } else if(o1.getKey() > o2.getKey()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private void sortAnalysisBySecondValue() {
        Collections.sort(analysis, new Comparator<Pair<Double, Double>>() {
            public int compare(Pair<Double, Double> o1, Pair<Double, Double> o2) {
                if(o1.getValue() < o2.getValue()) {
                    return -1;
                } else if(o1.getValue() > o2.getValue()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private void calculate_m() throws Exception{
        // searching the maximum M value
        double M = calculateM(1);
        for (int i = 2; i < analysis.size(); i++) {
            double tempM = calculateM(i);
            if (tempM > M) {
                M = tempM;
            }
        }

        // define m value
        if (M > 0)
            this.m = r * M;
        else if (M == 0) this.m = 1;
        else throw new Exception("Error: M < 0");
    }

    private void calculate_u() throws Exception{
        ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(v));
        for(int i = 0; i < 3; i++) {

        }
        if(Iv.size() < 2) {
            u = 1.0;
        } else {
            double temp_u = calculateTemp_u(Iv, 1);
            for (int j = 2; j < Iv.size(); j++) {
                double temp = calculateTemp_u(Iv, j);
                if (temp > temp_u) {
                    temp_u = temp;
                }
            }

            if (temp_u > 0) {
                u = temp_u;
            } else if (temp_u == 0) {
                u = 1.0;
            }
            else throw new Exception("Error: u < 0");
        }
    }

    private double calculateTemp_u(ArrayList<Integer> Iv, int index) {
        return Math.abs(analysis.get(Iv.get(index)).getValue() - analysis.get(Iv.get(index - 1)).getValue()) /
                (analysis.get(Iv.get(index)).getKey() - analysis.get(Iv.get(index - 1)).getKey());
    }

    private double calculateM(int index) {
        return Math.abs(analysis.get(index).getValue() - analysis.get(index - 1).getValue()) /
                (analysis.get(index).getKey() - analysis.get(index - 1).getKey());
    }

    double calculateR(int index) {
        double temp = m * (analysis.get(index).getKey() - analysis.get(index - 1).getKey());
        double result = temp + Math.pow(analysis.get(index).getValue() - analysis.get(index - 1).getValue(), 2) / temp -
                2 * (analysis.get(index).getValue() + analysis.get(index - 1).getValue());
        return result;

    }

    int calculateMaxR() {
        double R = calculateR(1);
        int indexR = 1;
        for (int i = 2; i < analysis.size(); i++)
        {
            double tempR = calculateR(i);
            if (tempR > R) {
                R = tempR;
                indexR = i;
            }
        }
        return indexR;
    }
}