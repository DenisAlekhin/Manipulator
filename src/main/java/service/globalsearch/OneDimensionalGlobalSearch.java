package service.globalsearch;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class OneDimensionalGlobalSearch {
    private final Double A, B;
    private final Double R, E;
    private final List<Pair<Double, Double>> analysis = new ArrayList<>();
    private Expression FUNC;
    private Boolean isLastIteration = false;
    private String variable;


    public OneDimensionalGlobalSearch(Expression func, Double a, Double b,
                                      Double r, Double e){
        this.FUNC = func;
        A = a;
        B = b;
        R = r;
        E = e;
        try {
            variable = getVariableName();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Pair<Double, Double> findMinimum(Boolean oneIteration) {
        if(!oneIteration) {
            analysis.clear();
        }
        if(analysis.size() < 2) {
            initAnalysis();
        }

        do {
            sortAnalysisByFirstValue();
            double m = calculate_m();
            int t = calculateMaxR(m);
            double newStudyX = calculateNewStudyPoint(t, m);
            addNewStudyPoint(newStudyX, t);
            isLastIteration = analysis.get(t).getKey() - analysis.get(t - 1).getKey() < E;
        } while (!isLastIteration && !oneIteration);

        if(oneIteration && !isLastIteration) {
            return analysis.get(analysis.size() - 1);
        } else {
            sortAnalysisBySecondValue();
            return analysis.get(0);
        }
    }

    public void setFunction(Expression func){
        FUNC = func;
        try {
            variable = getVariableName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isLastIteration() {
        return isLastIteration;
    }

    private String getVariableName() throws Exception{
        if(FUNC.getVariableNames().size() != 1) {
            throw new Exception("Error: количество переменных не равно одной!");
        } else {
            return FUNC.getVariableNames().stream().iterator().next();
        }
    }

    private void initAnalysis() {
        analysis.add(new Pair<>(A, FUNC.setVariable(variable, A).evaluate()));
        analysis.add(new Pair<>(B, FUNC.setVariable(variable, B).evaluate()));
    }

    private void sortAnalysisByFirstValue() {
        analysis.sort((o1, o2) -> {
            if (o1.getKey() < o2.getKey()) {
                return -1;
            } else if (o1.getKey() > o2.getKey()) {
                return 1;
            }
            return 0;
        });
    }

    private void sortAnalysisBySecondValue() {
        analysis.sort((o1, o2) -> {
            if (o1.getValue() < o2.getValue()) {
                return -1;
            } else if (o1.getValue() > o2.getValue()) {
                return 1;
            }
            return 0;
        });
    }

    private double calculate_m(){
        // searching the maximum M value
        double M = calculate_M(1);
        for (int i = 2; i < analysis.size(); i++) {
            double tempM = calculate_M(i);
            if (tempM > M) {
                M = tempM;
            }
        }

        // define m value
        if (M > 0) {
            return  R * M;
        } else if (M == 0) {
            return 1;
        } else {
            System.err.println("Error: M < 0");
            return 1;
        }
    }

    private double calculate_M(int index) {
        return Math.abs(analysis.get(index).getValue() - analysis.get(index - 1).getValue()) /
                (analysis.get(index).getKey() - analysis.get(index - 1).getKey());
    }

    private int calculateMaxR(double m) {
        ArrayList<Double> R = new ArrayList<>(analysis.size());

        for(int i = 1; i < analysis.size(); i++) {
            R.add(calculateR(i, m));
        }
        double maxR = Collections.max(R);
        int t = R.lastIndexOf(maxR);

        return t + 1;
    }

    private double calculateR(int index, double m) {
        double temp = m * (analysis.get(index).getKey() - analysis.get(index - 1).getKey());
        return temp + Math.pow(analysis.get(index).getValue() - analysis.get(index - 1).getValue(), 2) / temp -
                2 * (analysis.get(index).getValue() + analysis.get(index - 1).getValue());
    }

    private double calculateNewStudyPoint(int t, double m) {
        return (analysis.get(t).getKey() + analysis.get(t - 1).getKey()) / 2 -
                (analysis.get(t).getValue() - analysis.get(t - 1).getValue()) / (2 * m);
    }

    private void addNewStudyPoint(double newStudyX, int t){
        if (newStudyX < analysis.get(t - 1).getKey() || newStudyX > analysis.get(t).getKey()) {
            System.err.println("Error: новая точка исследовая выходит за границы!");
        }

        FUNC.setVariable(variable, newStudyX);
        analysis.add(new Pair<>(newStudyX, FUNC.evaluate()));
    }

    public List<Pair<Double, Double>> getAnalysis() {
        return analysis;
    }
}
