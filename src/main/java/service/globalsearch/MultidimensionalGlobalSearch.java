package service.globalsearch;

import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import service.exceptions.NoSolutionExceptions;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MultidimensionalGlobalSearch {
    private final Expression FUNC;
    private final String FUNC_STR;
    private final List<Double> A, B;
    private final Double R, E;
    private Integer variablesCount;
    private Map<Integer, String> variableNames;
    private final List<List<Double>> analysis = new ArrayList<>();
    private List<OneDimensionalGlobalSearch> oneDimensionalGlobalSearches;
    private List<OneDimensionalGlobalSearchWithLimitations> oneDimensionalGlobalSearchesWithLimitations;
    private List<Double> variables;

    public List<Double> findMinimum(boolean withLimitations, ArrayList<Point2D> obstacles){
        setUp(withLimitations, obstacles);
        if(withLimitations) {
            while(!oneDimensionalGlobalSearchesWithLimitations.get(0).isLastIteration()){
                oneDimensionalGlobalSearchesWithLimitations.get(1).setFunction(getOneDimensionalFunction(1, variables));
                oneDimensionalGlobalSearchesWithLimitations.get(1).setDistanceToObstaclesVariables(variables);
                Pair<Double, Double> executionResult;
                try {
                    executionResult = oneDimensionalGlobalSearchesWithLimitations.get(1).findMinimum(false);
                    variables.set(1, executionResult.getKey());
                } catch (NoSolutionExceptions ignored) {

                } catch (Exception e) {
                    e.printStackTrace();
                }

                oneDimensionalGlobalSearchesWithLimitations.get(0).setFunction(getOneDimensionalFunction(0, variables));
                oneDimensionalGlobalSearchesWithLimitations.get(0).setDistanceToObstaclesVariables(variables);
                try {
                    executionResult = oneDimensionalGlobalSearchesWithLimitations.get(0).findMinimum(true);
                    variables.set(0, executionResult.getKey());
                    analysis.add(new ArrayList<>(variables));
                    analysis.get(analysis.size() - 1).add(executionResult.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return getResultWithLimitations(obstacles);
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
                if(distanceToObstacles.rodCorrespondLimitations(1, e.get(0))) {
                    return true;
                } else {
                    return false;
                }
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
}
