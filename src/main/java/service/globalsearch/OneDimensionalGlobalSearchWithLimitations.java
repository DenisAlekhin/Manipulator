package service.globalsearch;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class OneDimensionalGlobalSearchWithLimitations {
    private final Double A, B;
    private final Double R, E;
    private final List<Pair<Double, Double>> analysis = new ArrayList<>();
    private final ArrayList<SortedSet<Integer>> I = new ArrayList<>();
    private final Integer COUNT_OF_LIMITATIONS = 3;
    private Expression FUNC;
    private final DistanceToObstacles distanceToObstacles;
    private Boolean isLastIteration = false;
    private String variable;

    public OneDimensionalGlobalSearchWithLimitations(
            Expression func, Double a, Double b, Double r, Double e, DistanceToObstacles distanceToObstacles){
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
        this.distanceToObstacles = distanceToObstacles;
    }

    public Pair<Double, Double> findMinimum(Boolean oneIteration) {
        setUp(oneIteration);

        try {
            do {
                sortAnalysisByFirstValue();
                resetSets();
                calculateI();
                List<Double> u = calculate_u();
                List<Double> z = calculate_z();
                int t = calculateMaxIndexR(u, z);
                double newStudyX = calculateNewStudyPoint(t, u);
                addNewStudyPoint(newStudyX, t);
                isLastIteration = analysis.get(t).getKey() - analysis.get(t - 1).getKey() < E;
            } while (!isLastIteration && !oneIteration);


        if(oneIteration && !isLastIteration) {
            return analysis.get(analysis.size() - 1);
        } else {
            sortAnalysisBySecondValue();
            return analysis.get(0);
        }

        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<Double, Double>(0.0, 0.0);
        }
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
        analysis.add(new Pair<>(analysis.get(0).getKey() + analysis.get(1).getKey() / 2,
                FUNC.setVariable(variable, (analysis.get(0).getKey() - analysis.get(1).getKey()) / 2).evaluate()));
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

    private void addNewStudyPoint(final double newStudyX, final int t){
        if (newStudyX < analysis.get(t - 1).getKey() || newStudyX > analysis.get(t).getKey()) {
            System.err.println("Error: новая точка исследовая выходит за границы!");
        }

        FUNC.setVariable(variable, newStudyX);
        analysis.add(new Pair<>(newStudyX, FUNC.evaluate()));
    }

    private void resetSets() {
        for(int i = 0; i < I.size(); i++) {
            I.get(i).clear();
        }
    }

    private void initializeI() {
        for(int i = 0; i < COUNT_OF_LIMITATIONS + 2; i++) {
            I.add(new TreeSet<>());
        }
    }

    private void calculateI() throws Exception{
        for(int i = 1; i < analysis.size() - 1; i++) {
            I.get(calculateV(analysis.get(i).getKey())).add(i);
        }

        // first and last point goes to I with v = 0 by default
        I.get(0).add(0);
        I.get(0).add(analysis.size() - 1);
    }

    private int calculateV(double point) throws Exception{
        if(distanceToObstacles.rodCorrespondLimitations(1, point)){
            if(distanceToObstacles.rodCorrespondLimitations(2, point)) {
                if(distanceToObstacles.rodCorrespondLimitations(3, point)){
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

    private List<Double> calculate_u() throws Exception{
        List<Double> u = new ArrayList<>();
        for(int i = 0; i < I.size(); i++) {
            List<Integer> Iv = new ArrayList<>(I.get(i));
            if(Iv.size() >= 2) {
                double max_u = Double.MIN_VALUE;
                for (int indexJ = 1; indexJ < Iv.size(); indexJ++) {
                    for(int indexI = indexJ + 1; indexI < Iv.size(); indexI++)
                        max_u = Math.max(calculateTemp_u(indexI, indexJ, Iv), max_u);
                }
                if(max_u > 0) {
                    u.add(max_u);
                } else if(max_u == 0){
                    u.add(1.0);
                } else {
                    throw new Exception("Error: u < 0");
                }
            } else {
                u.add(1.0);
            }
        }

        return u;
    }

    private double calculateTemp_u(int indexI, int indexJ, List<Integer> Iv) throws Exception{
        if(indexI <= indexJ) {
            throw new Exception("indexI <= indexJ");
        }

        double Xi = analysis.get(Iv.get(indexI)).getKey();
        double Xj = analysis.get(Iv.get(indexJ)).getKey();
        double Zi = analysis.get(Iv.get(indexI)).getValue();
        double Zj = analysis.get(Iv.get(indexJ)).getValue();

        return Math.abs(Zi - Zj) / (Xi - Xj);
    }

    private List<Double> calculate_z() {
        List<Double> z = new ArrayList<>();
        for(int i = 0; i < I.size(); i++) {
            ArrayList<Integer> Iv = new ArrayList<>(I.get(i));
            if(Iv.size() != 0) {
                double min_z = Double.MAX_VALUE;
                for(Integer pointIndex: Iv) {
                    min_z = Math.min(analysis.get(pointIndex).getValue(), min_z);
                }
                z.add(min_z);
            } else {
                z.add(0.0);
            }
        }

        return z;
    }

    private int calculateMaxIndexR(List<Double> u, List<Double> z) throws Exception{
        ArrayList<Double> R = new ArrayList<Double>(analysis.size());

        for(int i = 1; i < analysis.size(); i++) {
            R.add(calculateIndexR(i, u, z));
        }
        double maxR = Collections.max(R);
        int t = R.lastIndexOf(maxR);

        return t + 1;
    }

    private double calculateIndexR(int index, List<Double> u, List<Double> z) throws Exception{
//        int v = calculateVForPoint(index);
        double max_u = Collections.max(u);
        double min_z = Collections.min(z);

        return calculateIndexRFormula1(index, max_u, min_z);
    }

    private double calculateIndexRFormula1(int index, double u, double z) {
        double deltaI = analysis.get(index).getKey() - analysis.get(index - 1).getKey();
        double Zi = analysis.get(index).getValue();
        double Zj = analysis.get(index - 1).getValue();


        //return u * r * deltaI + Math.pow(Zi - Zj, 2) / (u * r * deltaI) - 2 * (Zi + Zj);

        return deltaI +
                Math.pow(Zi - Zj, 2) / (Math.pow(R * u, 2) * deltaI) -
                (2 * (Zi + Zj - 2 * z)) / (R * u);
    }

    private int calculateVForPoint(int point) throws Exception{
        for(int i = 0; i < I.size(); i++) {
            ArrayList<Integer> Iv = new ArrayList<>(I.get(i));
            for(int j = 0; j < Iv.size(); j++) {
                if(Iv.contains(point)) {
                    return i;
                }
            }
        }

        throw new Exception("Точка не найдена");
    }

    private double calculateNewStudyPoint(int t, List<Double> u) throws Exception{
        double Xt = analysis.get(t).getKey();
        double Xj = analysis.get(t - 1).getKey();
        double Zt = analysis.get(t).getValue();
        double Zj = analysis.get(t - 1).getValue();

        double selected_u = u.get(calculateVForPoint(t));
        if(calculateVForPoint(t - 1) != calculateVForPoint(t)) {
            return (Xt + Xj) / 2;
        } else {
            return (Xt + Xj) / 2 -
                    (Zt - Zj)  / (2 * R * selected_u);
        }
    }

    public void setDistanceToObstaclesVariables(List<Double> fixedVariables) {
        distanceToObstacles.setFixedVariables(fixedVariables);
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

    private void setUp(boolean oneIteration) {
        if(!oneIteration || analysis.size() < 2) {
            analysis.clear();
            initAnalysis();
            I.clear();
            initializeI();
        }
    }
}
