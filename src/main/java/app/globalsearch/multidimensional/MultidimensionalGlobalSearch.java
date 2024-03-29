package app.globalsearch.multidimensional;

import javafx.util.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import app.service.Iterations;
import app.exceptions.NoSolutionExceptions;
import app.service.DistanceToObstacles;
import app.globalsearch.singledimensional.OneDimensionalGlobalSearch;
import app.globalsearch.singledimensional.OneDimensionalGlobalSearchWithLimitations;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class MultidimensionalGlobalSearch {
    private final Expression FUNC;
    private final String FUNC_STR;
    private final List<Double> A, B;
    private final Double R;
    @NonNull
    private Double E;
    private Integer variablesCount;
    private Map<Integer, String> variableNames;
    private final List<List<Double>> analysis = new ArrayList<>();
    private List<OneDimensionalGlobalSearch> oneDimensionalGlobalSearches;
    private List<OneDimensionalGlobalSearchWithLimitations> oneDimensionalGlobalSearchesWithLimitations;
    private List<Double> variables;

    public List<Double> findMinimum(boolean withLimitations, ArrayList<Point2D> obstacles, Double precision, int maxIter){
        E = precision;
        Iterations.reset();
        setUp(withLimitations, obstacles);
        if(withLimitations) {
            while(!oneDimensionalGlobalSearchesWithLimitations.get(1).isLastIteration()){
                oneDimensionalGlobalSearchesWithLimitations.get(0).setFunction(getOneDimensionalFunction(0, variables));
                oneDimensionalGlobalSearchesWithLimitations.get(0).setDistanceToObstaclesVariables(variables);
                Pair<Double, Double> executionResult;
                try {
                    executionResult = oneDimensionalGlobalSearchesWithLimitations.get(0).findMinimum(false, maxIter);
                    variables.set(0, executionResult.getKey());
                } catch (NoSolutionExceptions ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }

                oneDimensionalGlobalSearchesWithLimitations.get(1).setFunction(getOneDimensionalFunction(1, variables));
                oneDimensionalGlobalSearchesWithLimitations.get(1).setDistanceToObstaclesVariables(variables);
                try {
                    executionResult = oneDimensionalGlobalSearchesWithLimitations.get(1).findMinimum(true, maxIter);
                    variables.set(1, executionResult.getKey());
                    analysis.add(new ArrayList<>(variables));
                    analysis.get(analysis.size() - 1).add(executionResult.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            log.info("Execution of the algorithm took {} iterations", Iterations.get());
            List<Double> res = getResultWithLimitations(obstacles);
            res.add((double)Iterations.get());
            return res;
        } else {
            while(!oneDimensionalGlobalSearches.get(0).isLastIteration()){
                oneDimensionalGlobalSearches.get(1).setFunction(getOneDimensionalFunction(1, variables));
                Pair<Double, Double> executionResult = oneDimensionalGlobalSearches.get(1).findMinimum(false);
                variables.set(1, executionResult.getKey());

                oneDimensionalGlobalSearches.get(0).setFunction(getOneDimensionalFunction(0, variables));
                executionResult = oneDimensionalGlobalSearches.get(0).findMinimum(true);
                variables.set(0, executionResult.getKey());

                analysis.add(new ArrayList<>(variables));
                analysis.get(analysis.size() - 1).add(executionResult.getValue());
            }
            return getResult();
        }
    }

    private Expression getOneDimensionalFunction(final int numberOfVariable, List<Double> variables) {
        String newFuncStr = FUNC_STR;
        for(int i = 0; i < variablesCount; i++) {
            if(i != numberOfVariable) {
                newFuncStr = newFuncStr.replaceAll(variableNames.get(i), variables.get(i).toString());
            }
        }

        return new ExpressionBuilder(newFuncStr)
                .variables(variableNames.get(numberOfVariable))
                .build();
    }

    private List<Double> getResultWithLimitations(List<Point2D> obstacles) {
        return analysis.stream().sorted(Comparator.comparingDouble(v -> v.get(v.size() - 1))).filter(e -> {
            DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 0);
            distanceToObstacles.setFixedVariables(new ArrayList<>(Arrays.asList(e.get(0), e.get(1))));
            try {
                return distanceToObstacles.rodCorrespondLimitations(1, e.get(0));
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }).findFirst().orElseThrow(() -> new NoSolutionExceptions("Error: нет решения"));
    }

    private void setUp(boolean withLimitations, ArrayList<Point2D> obstacles) {
        variablesCount = FUNC.getVariableNames().size();

        variableNames = new HashMap<>(variablesCount);
        int j = 0;
        for(String variableName: FUNC.getVariableNames()) {
            variableNames.put(j++, variableName);
        }

        variables = new ArrayList<>(variablesCount);
        for(int i = 0; i < variablesCount; i++) {
            double middleOfInterval = (B.get(i) + A.get(i)) / 2;
            variables.add(middleOfInterval);
        }

        if(withLimitations) {
            oneDimensionalGlobalSearchesWithLimitations = new ArrayList<>(variablesCount);
            for(int i = 0; i < variablesCount; i++) {
                oneDimensionalGlobalSearchesWithLimitations.add(new OneDimensionalGlobalSearchWithLimitations(
                        getOneDimensionalFunction(i, variables), A.get(i), B.get(i), R, E,
                        new DistanceToObstacles(obstacles, i)));
            }
        } else {
            oneDimensionalGlobalSearches = new ArrayList<>(variablesCount);
            for(int i = 0; i < variablesCount; i++) {
                oneDimensionalGlobalSearches.add(new OneDimensionalGlobalSearch(getOneDimensionalFunction(i, variables), A.get(i), B.get(i), R, E));
            }
        }
    }

    private List<Double> getResult() {
        return analysis.stream().min(Comparator.comparingDouble(v -> v.get(v.size() - 1))).orElseThrow(() -> new NoSolutionExceptions("Error: нет решения"));
    }

    public List<Double> findMinimumLocal(double target_x, double target_y, Expression func, Double precision, Double localPrecision, int maxIter) {
        Iterations.reset();
        E = precision;
        List<Double> assumption = findMinimum(true, new ArrayList<>(), precision, maxIter);
        int globalIter = Iterations.get();
//        Iterations.reset();
        List<Double> result =  MultidimensionalGlobalSearchLib.findMinimumNelderMead(target_x, target_y, func, assumption, localPrecision);
        func.setVariable("x0", assumption.get(0));
        func.setVariable("x1", assumption.get(1));
        log.debug("Для точности АГП E = {} и количестве итераций = {} при точности локального алгоритма: {}", E, maxIter, localPrecision);
        log.debug("Результат: {}", assumption);
        log.debug("Итераций в глобальном алгоритме: {}, итераций в локальном алгоритме: {}",globalIter, Iterations.get());
        log.debug("Расстояние до целевой точки до применения локального алгоритма: {}, после применения: {}",func.evaluate(), result.get(2));
        log.debug("");
        return result;
    }
}
