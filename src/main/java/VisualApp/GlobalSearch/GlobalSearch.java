package VisualApp.GlobalSearch;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GlobalSearch {
    private Expression function;
    private ArrayList<Double> a, b;
    private double r, epsilon;
    private int numOfIter;
    private double m;
    private boolean onlyHingesMoves;
    private ArrayList<Pair<Double, Double>> analysis;
    private ArrayList<Pair<Double, Double>> stepsOfOneDimensionalAlgorithm;
    private ArrayList<ArrayList<Double>> stepsOfAlgorithm;

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

    public ArrayList<Double> findMinimum(){
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

        // forward
        for(int n = 0; n < numOfIter; n++) {
            for(int i = 0; i < countOfDimensions; i++) {
                countOfCalc.set(i, countOfCalc.get(i) + 1);
                result.set(i, findOneDimensionalMinimum(function, "x" + i, i).getKey());
                function.setVariable("x" + i, result.get(i));
                saveStepsOfAlgorithm(result, i);
                if(i != 0 && countOfCalc.get(i) < 2) {
                    for(int k = i - 1; k > 0; k--) {
                        countOfCalc.set(k, 0);
                    }
                    i = -1;
                }
            }
        }


        // backward
        /*
        for(int n = 0; n < numOfIter; n++) {
            for(int i = countOfDimensions - 1; i >= 0; i--) {
                countOfCalc.set(i, countOfCalc.get(i) + 1);
                result.set(i, findOneDimensionalMinimum(function, "x" + i, i).getKey());
                function.setVariable("x" + i, result.get(i));
                saveStepsOfAlgorithm(result, i);
                if(i != countOfDimensions - 1 && countOfCalc.get(i) < 2) {
                    for(int k = i + 1; k < countOfDimensions - 1; k++) {
                        countOfCalc.set(k, 0);
                    }
                    i = countOfDimensions;
                }
            }
        }*/

        for(int j = 0; j < countOfDimensions; j++) {
            function.setVariable("x" + j, result.get(j));
        }
        result.add(countOfDimensions, function.evaluate());
        return result;
    }

    private Pair<Double, Double> findOneDimensionalMinimum(Expression function, String variable, int numberOfVariable){
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
