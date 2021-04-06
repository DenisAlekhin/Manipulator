package VisualApp.GlobalSearch;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GlobalSearch {
    private Expression function;
    private double a, b;
    private double r, epsilon;
    private double m;
    private ArrayList<Pair<Double, Double>> analysis;

    public GlobalSearch(Expression function, double a, double b,
                        double epsilon, double r) {
        try {
            if(a > b) {
                throw new Exception("Error: a > b");
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
        analysis = new ArrayList<Pair<Double, Double>>();
    }

    public ArrayList<Double> findMinimum(){
        ArrayList<Double> result = new ArrayList<Double>();
        if(function.getVariableNames().size() == 1) {
            Pair<Double, Double> pairResult = findOneDimensionalMinimum(function, "x");
            result.add(pairResult.getKey());
            result.add(pairResult.getValue());
        } else {
            result.add(0, 1.0);
            function.setVariable("x", result.get(0));
            result.add(1, findOneDimensionalMinimum(function, "y").getKey());
            function.setVariable("y", result.get(1));
            result.set(0, findOneDimensionalMinimum(function, "x").getKey());
            result.add(2, findOneDimensionalMinimum(function, "x").getValue());
        }
        return result;
    }

    /*
    else {
            for(int i = 0; i < function.getVariableNames().size(); i++) {
                for(int j = 0; j < function.getVariableNames().size() - i - 1; j++) {
                    //result.add(j, 1.0);
                    function.setVariable("x" + i, result.get(i));
                }
                function.setVariable("x" + i, result.get(i));
                result.add(1, findOneDimensionalMinimum(function, "y").getKey());
            }
            result.add(0, 0.1);
            function.setVariable("x", result.get(0));
            result.add(1, findOneDimensionalMinimum(function, "y").getKey());
            function.setVariable("y", result.get(1));
            result.set(0, findOneDimensionalMinimum(function, "x").getKey());
            result.add(2, findOneDimensionalMinimum(function, "x").getValue());
        }
     */

    private Pair<Double, Double> findOneDimensionalMinimum(Expression function, String variable){
        analysis.clear();
        analysis.add(new Pair<Double, Double>(a, function.setVariable(variable, a).evaluate()));
        analysis.add(new Pair<Double, Double>(b, function.setVariable(variable, b).evaluate()));

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
