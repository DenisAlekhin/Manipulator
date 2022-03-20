package service.globalsearch;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;
import service.exceptions.NoSolutionExceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class OneDimensionalGlobalSearchWithLimitations {
    private final Double A, B;
    private final Double R, E;
    private final List<Pair<Double, Double>> analysis = new ArrayList<>();
    private final ArrayList<SortedSet<Integer>> I = new ArrayList<>();
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

    public Pair<Double, Double> findMinimum(Boolean oneIteration) throws Exception{
        setUp(oneIteration);
        boolean newStudyPointCorrespondLimitations;

        do {
            sortAnalysisByFirstValue();
            resetSets();
            calculateI();
            List<Double> u = calculate_u();
            List<Double> z = calculate_z();
            int t = calculateMaxIndexR(u, z);
            double newStudyX = calculateNewStudyPoint(t, u);
            addNewStudyPoint(newStudyX, t);

            newStudyPointCorrespondLimitations = distanceToObstacles.pointCorrespondLimitations(analysis.get(t - 1).getKey());
            isLastIteration = analysis.get(t).getKey() - analysis.get(t - 1).getKey() < E;
        } while (!isLastIteration && !oneIteration && newStudyPointCorrespondLimitations);

        if(oneIteration && !isLastIteration) {
            return analysis.get(analysis.size() - 1);
        } else {
            return getResult();
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
        if (analysis.contains(new Pair<Double, Double>(newStudyX, FUNC.setVariable(variable, newStudyX).evaluate()))) {
            System.err.println("Error: new study point is the same");
        }
//        for (double i = -3.04; i <= 3.14; i += 0.1) {
//            if(FUNC.setVariable(variable, i).evaluate() == FUNC.setVariable(variable, i - 0.1).evaluate()) {
//                System.err.println("function error");
//                break;
//            }
//        }

        analysis.add(new Pair<>(newStudyX, FUNC.setVariable(variable, newStudyX).evaluate()));
    }

    private void resetSets() {
        for (SortedSet<Integer> set : I) {
            set.clear();
        }
    }

    private void initializeI() {
        for(int i = 0; i < 4; i++) {
            I.add(new TreeSet<>());
        }
    }

    private void calculateI() throws Exception{
        for(int i = 0; i < analysis.size(); i++) {
            I.get(calculateV(analysis.get(i).getKey())).add(i);
        }
    }

    private int calculateV(double point) throws Exception{
        if(distanceToObstacles.rodCorrespondLimitations(1, point)){
            if(distanceToObstacles.rodCorrespondLimitations(2, point)) {
                return 3;
            } else{
                return 2;
            }
        } else {
            return 1;
        }
    }

    private List<Double> calculate_u() throws Exception{
        List<Double> u = new ArrayList<Double>(Arrays.asList(new Double[5]));
        for(int i = 1; i < I.size(); i++) {
            ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(i));
            if (Iv.size() >= 2) {
                double max_u = calculateTemp_u(1, 0, i, Iv);
                for (int indexJ = 1; indexJ < Iv.size(); indexJ++) {
                    for (int indexI = indexJ + 1; indexI < Iv.size(); indexI++)
                        max_u = Math.max(calculateTemp_u(indexI, indexJ, i, Iv), max_u);
                }
                if(max_u > 0) {
                    u.set(i, max_u);
                } else if(max_u == 0){
                    u.set(i, 1.0);
                } else {
                    throw new Exception("Error: u < 0");
                }
            } else {
                u.set(i, 1.0);
            }
        }

        return u;
    }

    private double calculateTemp_u(int indexI, int indexJ, int v, List<Integer> Iv) throws Exception{
        if(indexI <= indexJ) {
            throw new Exception("indexI <= indexJ");
        }

        double Xi = analysis.get(Iv.get(indexI)).getKey();
        double Xj = analysis.get(Iv.get(indexJ)).getKey();
        double Zi = calcGv(analysis.get(Iv.get(indexI)).getKey(), v);
        double Zj = calcGv(analysis.get(Iv.get(indexJ)).getKey(), v);

        return Math.abs(Zi - Zj) / (Xi - Xj);
    }

    private List<Double> calculate_z() throws Exception{
        List<Double> z = new ArrayList<>(Arrays.asList(new Double[5]));
        int maxV = 0;
        for (int i = 1; i < I.size(); i++) {
            if (I.get(i).size() != 0) {
                maxV = i;
            }
        }
        for (int i = 1; i < I.size(); i++) {
            if (i == maxV) {
                ArrayList<Integer> Iv = new ArrayList<Integer>(I.get(i));
                double minZ = calcGv(analysis.get(Iv.get(0)).getKey(), maxV);
                for (int j = 1; j < Iv.size(); j++) {
                    minZ = Math.min(calcGv(analysis.get(Iv.get(j)).getKey(), maxV), minZ);
                }
                z.set(i, minZ);
            } else {
                z.set(i, 0.0);
            }
        }
        return z;
    }

    private int calculateMaxIndexR(List<Double> u, List<Double> z) throws Exception{
        ArrayList<Double> R = new ArrayList<>(analysis.size());

        for(int i = 1; i < analysis.size(); i++) {
            R.add(calculateIndexR(i, u, z));
        }
        double maxR = Collections.max(R);
        int t = R.lastIndexOf(maxR);

        return t + 1;
    }

    private double calculateIndexR(int index, List<Double> u, List<Double> z) throws Exception{
        if (calculateVForPoint(index - 1) == calculateVForPoint(index)) {
            return calculateIndexRFormula1(index, u, z);
        } else if (calculateVForPoint(index - 1) > calculateVForPoint(index)) {
            return calculateIndexRFormula2(index, u, z);
        } else {
            return calculateIndexRFormula3(index, u, z);
        }
    }

    private double calculateIndexRFormula1(int index, List<Double> list_u, List<Double> list_z) throws Exception{
        double u = list_u.get(calculateVForPoint(index));
        double z = list_z.get(calculateVForPoint(index));
        double deltaI = analysis.get(index).getKey() - analysis.get(index - 1).getKey();
        double Zi = calcGv(analysis.get(index).getKey(), calculateVForPoint(index));
        double Zj = calcGv(analysis.get(index - 1).getKey(), calculateVForPoint(index - 1));

        return deltaI +
                Math.pow(Zi - Zj, 2) / (Math.pow(R * u, 2) * deltaI) -
                (2 * (Zi + Zj - 2 * z)) / (R * u);
    }

    private double calculateIndexRFormula2(int index, List<Double> list_u, List<Double> list_z) throws Exception{
        double u = list_u.get(calculateVForPoint(index - 1));
        double z = list_z.get(calculateVForPoint(index - 1));
        double deltaI = analysis.get(index).getKey() - analysis.get(index - 1).getKey();
        double Zj = calcGv(analysis.get(index - 1).getKey(), calculateVForPoint(index - 1));

        return 2 * deltaI -
                4 * (Zj - z) / (R * u);
    }

    private double calculateIndexRFormula3(int index, List<Double> list_u, List<Double> list_z) throws Exception{
        double u = list_u.get(calculateVForPoint(index));
        double z = list_z.get(calculateVForPoint(index));
        double deltaI = analysis.get(index).getKey() - analysis.get(index - 1).getKey();
        double Zi = calcGv(analysis.get(index).getKey(), calculateVForPoint(index));

        return 2 * deltaI -
                4 * (Zi - z) / (R * u);
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

    private double calculateNewStudyPoint(int t, List<Double> list_u) throws Exception{
        double Xt = analysis.get(t).getKey();
        double Xj = analysis.get(t - 1).getKey();
        double Zt = calcGv(analysis.get(t).getKey(), calculateVForPoint(t));
        double Zj = calcGv(analysis.get(t - 1).getKey(), calculateVForPoint(t - 1));

        double u = list_u.get(calculateVForPoint(t));
        if (calculateVForPoint(t - 1) != calculateVForPoint(t)) {
            return (Xt + Xj) / 2;
        } else {
            return (Xt + Xj) / 2 -
                    (Zt - Zj) / (2 * R * u);
        }
    }

    public void setDistanceToObstaclesVariables(List<Double> fixedVariables) {
        distanceToObstacles.setFixedVariables(new ArrayList<>(fixedVariables));
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

    private double calcGv(double point, int v) throws Exception{
        switch(v) {
            case 1: {
                return -(distanceToObstacles.firstRodDistToObstacles(point)).stream().mapToDouble(Double::doubleValue).sum();
            }
            case 2: {
                return -(distanceToObstacles.secondRodDistToObstacles(point)).stream().mapToDouble(Double::doubleValue).sum();
            }
            case 3: {
                for(int i = 0; i < analysis.size(); i++) {
                    //Проверить здесь, если не работает
                    if(analysis.get(i).getKey() == point) {
                        return analysis.get(i).getValue();
                    }
                }
            }
            default: {
                return 0.0;
            }
        }
    }

    private Pair<Double, Double> getResult() throws NoSolutionExceptions{
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

        if(result.size() == 0) {
            throw new NoSolutionExceptions("Error: нет точки удовлетворяющей ограничениям");
        }
        return result.get(0);
    }

    public List<Pair<Double, Double>> getAnalysis() {
        return analysis;
    }
}
