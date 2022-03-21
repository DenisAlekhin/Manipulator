package service.globalsearch;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

public class GlobalSearch {
    private final Expression function;
    private final ArrayList<Double> a, b;
    private final double r, epsilon;
    private double m;
    private final boolean onlyHingesMoves;
    private final ArrayList<Pair<Double, Double>> analysis;
    private final ArrayList<Pair<Double, Double>> stepsOfOneDimensionalAlgorithm;
    private final ArrayList<ArrayList<Double>> stepsOfAlgorithm;
    private final int scrCoordManipStartX = 78;
    private final int scrCoordManipStartY = 156;
    private final ArrayList<SortedSet<Integer>> I = new ArrayList<SortedSet<Integer>>();
    private final ArrayList<Double> u = new ArrayList<Double>(Arrays.asList(new Double[5]));
    private final ArrayList<Double> z = new ArrayList<Double>(Arrays.asList(new Double[5]));

    {
        Collections.fill(u, 0.0);
    }

    public GlobalSearch(Expression function, ArrayList<Double> a, ArrayList<Double> b,
                        double epsilon, double r, boolean onlyHingesMoves) {
        try {
            if (a.size() != b.size()) {
                throw new Exception("Error: wrong count of borders");
            }
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i) > b.get(i)) {
                    throw new Exception("Error: a > b");
                }
            }
            if (r <= 0) {
                throw new Exception("Error: r <= 1");
            }
            if (epsilon <= 0) {
                throw new Exception("Error: epsilon <= 0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.function = function;
        this.a = a;
        this.b = b;
        this.epsilon = epsilon;
        this.r = 3;
        this.onlyHingesMoves = onlyHingesMoves;
        analysis = new ArrayList<Pair<Double, Double>>();
        stepsOfOneDimensionalAlgorithm = new ArrayList<Pair<Double, Double>>();
        stepsOfAlgorithm = new ArrayList<ArrayList<Double>>();
    }

    public ArrayList<Double> findMinimum(ArrayList<Point2D> obstacles) {
        ArrayList<Double> result = new ArrayList<Double>();
        int countOfDimensions = function.getVariableNames().size();
        ArrayList<Integer> countOfCalc = new ArrayList<Integer>();

        for (int i = 0; i < countOfDimensions; i++) {
            if (!onlyHingesMoves && i % 2 != 0) {
                result.add(1.0);
            } else {
                result.add(0.0);
            }
            countOfCalc.add(0);
            function.setVariable("x" + i, result.get(i));
        }

        stepsOfAlgorithm.add(new ArrayList<Double>(result));
        stepsOfAlgorithm.get(stepsOfAlgorithm.size() - 1).add(function.evaluate());

        int p = 0;
        double previousRes = -1;
        do {
            for (int i = countOfDimensions - 1; i >= 0; i--) {
                countOfCalc.set(i, countOfCalc.get(i) + 1);
                result.set(i, findOneDimensionalMinimumWithLimitations("x" + i, i, result, obstacles).getKey());
                function.setVariable("x" + i, result.get(i));
                // УБРАТЬ ЕСЛИ НЕ БУДЕТ РАБОТАТЬ
                saveStepsOfAlgorithm(result, i);
                if (i != 0 && countOfCalc.get(i) < 2) {
                    for (int k = i + 1; k < countOfDimensions - 1; k++) {
                        countOfCalc.set(k, 0);
                    }
                    i = -1;
                }
            }
            // !!!КОСТЫЛЬ!!!
            /*if(previousRes != -1 && previousRes == function.evaluate()) {
                if(result.get(0) > 0){
                    result.set(0, -1.5708);
                    result.set(1, 1.5708);
                } else {
                    result.set(0, 1.5708);
                    result.set(1, -1.5708);
                }

                    function.setVariable("x" + 0, result.get(0));
                    function.setVariable("x" + 1, result.get(1));
            }
            previousRes = function.evaluate();*/
            p++;
        } while (function.evaluate() > 0.5 && p < 100);
        if(p == 100) {
            System.out.println("Точка недостижима");
        }
        for (int j = 0; j < countOfDimensions; j++) {
            function.setVariable("x" + j, result.get(j));
        }
        result.add(countOfDimensions, function.evaluate());
        return result;
    }

    public Pair<Double, Double> findOneDimensionalMinimum(String variable, int numberOfVariable) {
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
            analysis.add(new Pair<Double, Double>(newStudyX,
                    function.setVariable(variable, newStudyX).evaluate()));
            stepsOfOneDimensionalAlgorithm.add(new Pair<Double, Double>(newStudyX,
                    function.setVariable(variable, newStudyX).evaluate()));
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
            ArrayList<Double> points, ArrayList<Point2D> obstacles) {
        analysis.clear();
        resetSets();
        I.clear();
        initializeI(obstacles);
        stepsOfOneDimensionalAlgorithm.clear();
        analysis.add(new Pair<Double, Double>(a.get(numberOfVariable),
                function.setVariable(variable, a.get(numberOfVariable)).evaluate()));
        analysis.add(new Pair<Double, Double>(b.get(numberOfVariable),
                function.setVariable(variable, b.get(numberOfVariable)).evaluate()));
        //analysis.add(new Pair<Double, Double>((analysis.get(0).getKey() + analysis.get(1).getKey()) / 2,
        //        function.setVariable(variable, (analysis.get(0).getKey() - analysis.get(1).getKey()) / 2).evaluate()));
        int t;
        do {
            sortAnalysisByFirstValue();
            resetSets();
            calculateI(points, obstacles, numberOfVariable);
            try {
                calculate_u(points, numberOfVariable, obstacles);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            calculate_z(points, numberOfVariable, obstacles);
            t = calculateMaxIndexR(points, numberOfVariable, obstacles);

            double newStudyX = calculateNewStudyPoint(t, points, numberOfVariable, obstacles);
            if (newStudyX < analysis.get(t - 1).getKey() || newStudyX > analysis.get(t).getKey()) {
                System.err.println("Error: New study point(" + newStudyX +
                        ") out of range([" + analysis.get(t - 1).getKey() +
                        ", " + analysis.get(t).getKey() + "])");
            }
            if (analysis.contains(new Pair<Double, Double>(newStudyX, function.setVariable(variable, newStudyX).evaluate()))) {
                System.err.println("Error: new study point is the same");
            }
            for (double i = -3.04; i <= 3.14; i += 0.1) {
                if(function.setVariable(variable, i).evaluate() == function.setVariable(variable, i - 0.1).evaluate()) {
                    System.err.println("function error");
                    break;
                }
            }
            analysis.add(new Pair<Double, Double>(newStudyX,
                    function.setVariable(variable, newStudyX).evaluate()));
            stepsOfOneDimensionalAlgorithm.add(new Pair<Double, Double>(newStudyX,
                    function.setVariable(variable, newStudyX).evaluate()));

        } while (analysis.get(t).getKey() - analysis.get(t - 1).getKey() > epsilon);

        return getResult();
    }

    private Pair<Double, Double> getResult() {
        ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(3));
        ArrayList<Pair<Double, Double>> result = new ArrayList<Pair<Double, Double>>(Iv.size());
        for (int i = 0; i < Iv.size(); i++) {
            result.add(analysis.get(Iv.get(i)));
        }

        Collections.sort(result, new Comparator<Pair<Double, Double>>() {
            public int compare(Pair<Double, Double> o1, Pair<Double, Double> o2) {
                if (o1.getValue() < o2.getValue()) {
                    return -1;
                } else if (o1.getValue() > o2.getValue()) {
                    return 1;
                }
                return 0;
            }
        });
        if (result.size() == 0) {
            System.err.println("Точка не достижима ");
        }

        if(result.size() == 0) {
            System.err.println("Нет точки удовлетворяющей ограничениям");
        }
        return result.get(0);
    }

    private double calculateNewStudyPoint(int t, ArrayList<Double> points,
                                          int numberOfVariable, ArrayList<Point2D> obstacles) {
        double Xt = analysis.get(t).getKey();
        // j = t - 1
        double Xj = analysis.get(t - 1).getKey();
        double Zt = calcGv(analysis.get(t).getKey(), calculateVForPoint(t), points, numberOfVariable, obstacles);
        // j = t - 1
        double Zj = calcGv(analysis.get(t - 1).getKey(), calculateVForPoint(t - 1), points, numberOfVariable, obstacles);

        double u = this.u.get(calculateVForPoint(t));
        if (calculateVForPoint(t - 1) != calculateVForPoint(t)) {
            return (Xt + Xj) / 2;
        } else {
            return (Xt + Xj) / 2 -
                    (Zt - Zj) / (2 * r * u);
        }/*
        double Xt = analysis.get(t).getKey();
        // j = t - 1
        double Xj = analysis.get(t - 1).getKey();
        double Zt = analysis.get(t).getValue();
        // j = t - 1
        double Zj = analysis.get(t - 1).getValue();

        double u = this.u.get(calculateVForPoint(t));
        if(calculateVForPoint(t - 1) != calculateVForPoint(t)) {
            return (Xt + Xj) / 2;
        } else {
            return (Xt + Xj) / 2 -
                    (Zt - Zj)  / (2 * r * u);
        }*/
    }
    /*
    private int sgn(double x) {
        if(x < 0) {
            return -1;
        } else if(x > 0) {
            return 1;
        } else {
            return 0;
        }
    }*/

    private int calculateMaxIndexR(ArrayList<Double> points,
                                   int numberOfVariable, ArrayList<Point2D> obstacles) {
        ArrayList<Double> R = new ArrayList<Double>(analysis.size());

        for (int i = 1; i < analysis.size(); i++) {
            R.add(calculateIndexR(i, points, numberOfVariable, obstacles));
        }
        double maxR = Collections.max(R);
        int t = R.lastIndexOf(maxR);

        return t + 1;
    }

    private double calculateIndexR(int index, ArrayList<Double> points,
                                   int numberOfVariable, ArrayList<Point2D> obstacles) {
        int v = calculateVForPoint(index);
        //double u = Collections.max(this.u.subList(1, this.u.size() - 1));
        //double z = Collections.min(this.z.subList(1, this.u.size() - 1));
        //double u = this.u.get(v);
        //double z = this.z.get(v);
        if (calculateVForPoint(index - 1) == calculateVForPoint(index)) {
            return calculateIndexRFormula1(index, points, numberOfVariable, obstacles);
        } else if (calculateVForPoint(index - 1) > calculateVForPoint(index)) {
            return calculateIndexRFormula2(index, points, numberOfVariable, obstacles);
        } else {
            return calculateIndexRFormula3(index, points, numberOfVariable, obstacles);
        }
    }

    private double calculateIndexRFormula1(int index, ArrayList<Double> points,
                                           int numberOfVariable, ArrayList<Point2D> obstacles) {
        double u = this.u.get(calculateVForPoint(index));
        double z = this.z.get(calculateVForPoint(index));
        double deltaI = analysis.get(index).getKey() - analysis.get(index - 1).getKey();
        double Zi = calcGv(analysis.get(index).getKey(), calculateVForPoint(index), points, numberOfVariable, obstacles);
        // j = i - 1
        double Zj = calcGv(analysis.get(index - 1).getKey(), calculateVForPoint(index - 1), points, numberOfVariable, obstacles);


        //return u * r * deltaI + Math.pow(Zi - Zj, 2) / (u * r * deltaI) - 2 * (Zi + Zj);

        return deltaI +
                Math.pow(Zi - Zj, 2) / (Math.pow(r * u, 2) * deltaI) -
                (2 * (Zi + Zj - 2 * z)) / (r * u);
    }

    private double calculateIndexRFormula2(int index, ArrayList<Double> points,
                                           int numberOfVariable, ArrayList<Point2D> obstacles) {
        double u = this.u.get(calculateVForPoint(index - 1));
        double z = this.z.get(calculateVForPoint(index - 1));
        double deltaI = analysis.get(index).getKey() - analysis.get(index - 1).getKey();
        // j = i - 1
        double Zj = calcGv(analysis.get(index - 1).getKey(), calculateVForPoint(index - 1), points, numberOfVariable, obstacles);

        return 2 * deltaI -
                4 * (Zj - z) / (r * u);
    }

    private double calculateIndexRFormula3(int index, ArrayList<Double> points,
                                           int numberOfVariable, ArrayList<Point2D> obstacles) {
        double u = this.u.get(calculateVForPoint(index));
        double z = this.z.get(calculateVForPoint(index));
        double deltaI = analysis.get(index).getKey() - analysis.get(index - 1).getKey();
        double Zi = calcGv(analysis.get(index).getKey(), calculateVForPoint(index), points, numberOfVariable, obstacles);

        return 2 * deltaI -
                4 * (Zi - z) / (r * u);
    }

    private void calculate_z(ArrayList<Double> points, int numberOfVariable, ArrayList<Point2D> obstacles) {
        int maxV = 0;
        for (int i = 1; i < I.size(); i++) {
            if (I.get(i).size() != 0) {
                maxV = i;
            }
        }
        for (int i = 1; i < I.size(); i++) {
            if (i == maxV) {
                ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(i));
                double minZ = calcGv(analysis.get(Iv.get(0)).getKey(), maxV, points, numberOfVariable, obstacles);
                for (int j = 1; j < Iv.size(); j++) {
                    minZ = Math.min(calcGv(analysis.get(Iv.get(j)).getKey(), maxV, points, numberOfVariable, obstacles)
                            , minZ);
                }
                z.set(i, minZ);
            } else {
                z.set(i, 0.0);
            }
        }


        /*for (int i = 1; i < I.size(); i++) {
            ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(i));
            if (Iv.size() != 0) {
                double minZ = analysis.get(Iv.get(0)).getValue();
                for (int j = 1; j < Iv.size(); j++) {
                    minZ = Math.min(analysis.get(Iv.get(0)).getValue(), minZ);
                }
                z.set(i, minZ);
            } else {
                z.set(i, 0.0);
            }
        }*/
    }

    private void resetSets() {
        for(int i = 0; i < I.size(); i++) {
            I.get(i).clear();
        }
    }

    private void initializeI(ArrayList<Point2D> obstacles) {
        for(int i = 0; i < 4; i++) {
            I.add(new TreeSet<Integer>());
        }

    }

    private void calculateI(ArrayList<Double> points, ArrayList<Point2D> obstacles, int numberOfVariable) {
        for(int i = 0; i < analysis.size(); i++) {
            I.get(calculateV(points, obstacles, analysis.get(i).getKey(), numberOfVariable)).add(i);
        }
    }

    private int calculateV(ArrayList<Double> points, ArrayList<Point2D> obstacles,
                           double point, int numberOfVariable) {
        points.set(numberOfVariable, point);
        ArrayList<Double> pointsCopy = copyPoints(points);
        if(firstRodCorrespondLimitations(pointsCopy, obstacles)){
            if(secondRodCorrespondLimitations(pointsCopy, obstacles)) {
                return 3;
            } else{
                return 2;
            }
        } else {
            return 1;
        }
    }
    private int calculateVForPoint(int point) {
        for(int i = 1; i < I.size(); i++) {
            ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(i));
            for(int j = 0; j < Iv.size(); j++) {
                if(Iv.contains(point)) {
                    return i;
                }
            }
        }
        return -1;
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
    private double sumArrayListElem(ArrayList<Double> arrayList) {
        int sum = 0;
        for(int i = 0; i < arrayList.size(); i++) {
            sum += arrayList.get(i);
        }

        return sum;
    }

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
                new Point2D.Double(scrCoordManipStartX+125*points.get(1)*Math.cos(points.get(0))+
                        125*points.get(3)*Math.cos(points.get(0)+points.get(2)),
                        scrCoordManipStartY+125*points.get(1)*Math.sin(points.get(0))+
                                125*points.get(3)*Math.sin(points.get(0)+points.get(2))),
                new Point2D.Double(scrCoordManipStartX+125*points.get(1)*Math.cos(points.get(0))+
                        125*points.get(3)*Math.cos(points.get(0)+points.get(2))+
                        125*points.get(5)*Math.cos(points.get(0)+points.get(2)+points.get(4)),
                        scrCoordManipStartY+125*points.get(1)*Math.sin(points.get(0))+
                                125*points.get(3)*Math.sin(points.get(0)+points.get(2))+
                                125*points.get(5)*Math.sin(points.get(0)+points.get(2)+points.get(4))));
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

    private void calculate_u(ArrayList<Double> points,
                             int numberOfVariable, ArrayList<Point2D> obstacles) throws Exception{
        for(int i = 1; i < I.size(); i++) {
            ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(i));
            if(Iv.size() >= 2) {
                double temp_u = calculateTemp_u(1, 0, i, Iv, points, numberOfVariable, obstacles);
                for (int j = 1; j < Iv.size(); j++) {
                    for(int k = j + 1; k < Iv.size(); k++)
                        temp_u = Math.max(calculateTemp_u(k, j, i, Iv, points, numberOfVariable, obstacles), temp_u);
                }
                if(temp_u > 0) {
                    u.set(i, temp_u);
                } else if(temp_u == 0){
                    u.set(i, 1.0);
                } else {
                    throw new Exception("Error: u < 0");
                }
            } else {
                u.set(i, 1.0);
            }
        }
        /*for(int i = 0; i < 5; i++) {
            ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(i));
            if(Iv.size() >= 2) {
                double temp_u = calculateTemp_u(1, 0, i, Iv, points, numberOfVariable, obstacles);
                for (int j = 2; j < Iv.size(); j++) {
                    temp_u = Math.max(calculateTemp_u(j, j - 1, i, Iv, points, numberOfVariable, obstacles), temp_u);
                }
                if(temp_u > 0) {
                    u.set(i, temp_u);
                } else if(temp_u == 0){
                    u.set(i, 1.0);
                } else {
                    throw new Exception("Error: u < 0");
                }
            } else {
                u.set(i, 1.0);
            }
        }*/
    }
    // ЕСЛИ НЕ БУДЕТ РАБОТАТЬ, ТО ПРОВЕРИТЬ ПРАВИЛЬНОСТЬ ЭТОЙ ФУКЦИИ
    public double calcGv(double variable, int _v, ArrayList<Double> points,
                         int numberOfVariable, ArrayList<Point2D> obstacles) {
        ArrayList<Double> pointsCopy = copyPoints(points);
        pointsCopy.set(numberOfVariable, variable);

        switch(_v) {
            case 1: {
                return -sumArrayListElem(firstRodDistToObstacles(pointsCopy, obstacles));
            }
            case 2: {
                return -sumArrayListElem(secondRodDistToObstacles(pointsCopy, obstacles));
            }
            case 3: {
                for(int i = 0; i < analysis.size(); i++) {
                    if(analysis.get(i).getKey() == variable) {
                        return analysis.get(i).getValue();
                    }
                }
            }
            default: {
                return 0.0;
            }
        }
    }

    private double calculateTemp_u(int indexI, int indexJ, int _v,ArrayList<Integer> Iv, ArrayList<Double> points, int numberOfVariable, ArrayList<Point2D> obstacles) {
        if(indexI <= indexJ) {
            System.err.println("indexI <= indexJ");
        }

        double Xi = analysis.get(Iv.get(indexI)).getKey();
        double Xj = analysis.get(Iv.get(indexJ)).getKey();
        double Zi = calcGv(analysis.get(Iv.get(indexI)).getKey(), _v, points, numberOfVariable, obstacles);
        double Zj = calcGv(analysis.get(Iv.get(indexJ)).getKey(), _v, points, numberOfVariable, obstacles);
        //double Zi = analysis.get(Iv.get(indexI)).getValue();
        //double Zj = analysis.get(Iv.get(indexJ)).getValue();

        return Math.abs(Zi - Zj) / (Xi - Xj);
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
        ArrayList<Double> R = new ArrayList<Double>(analysis.size());

        for(int i = 1; i < analysis.size(); i++) {
            R.add(calculateR(i));
        }
        double maxR = Collections.max(R);
        int t = R.lastIndexOf(maxR);

        return t + 1;
    }

    ArrayList<Double> copyPoints(ArrayList<Double> points) {
        ArrayList<Double> pointsCopy = new ArrayList<Double>(points);
        if(onlyHingesMoves) {
            pointsCopy.add(1, 1.0);
            pointsCopy.add(3, 1.0);
            pointsCopy.add(4, 0.0);
            pointsCopy.add(5, 1.0);
        } else {
            pointsCopy.add(4, 0.0);
            pointsCopy.add(5, 1.0);
        }

        return pointsCopy;
    }

}