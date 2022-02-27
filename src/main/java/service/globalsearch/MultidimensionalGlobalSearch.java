package service.globalsearch;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultidimensionalGlobalSearch {
    private final Expression FUNC;
    private final String FUNC_STR;
    private final List<Double> A, B;
    private final Double R, E;
//    private final List<List<Pair<Double, Double>>> analysis;
    private final Integer variablesCount;
    private final Map<Integer, String> variableNames;

//    private final boolean onlyHingesMoves;

    public MultidimensionalGlobalSearch(Expression func, String funcStr, List<Double> a, List<Double> b,
                                 Double r, Double e) {
        FUNC = func;
        FUNC_STR = funcStr;
        A = a;
        B = b;
        R = r;
        E = e;
        variablesCount = FUNC.getVariableNames().size();
//        analysis = new ArrayList<>();
//        for(int i = 0; i < variablesCount; i++) {
//            analysis.add(new ArrayList<>());
//        }

        Set<String> variables = FUNC.getVariableNames();
        variableNames = new HashMap<>(variablesCount);
        int i = 0;
        for(String variableName: variables) {
            variableNames.put(i++, variableName);
        }
    }

    public List<Double> findMinimum(){
        List<Double> variables = new ArrayList<>(variablesCount);
        initVariables(variables);
        List<OneDimensionalGlobalSearch> oneDimensionalGlobalSearches = new ArrayList<>(variablesCount);
        initOneDimensionalGlobalSearches(oneDimensionalGlobalSearches, variables);

        while(!oneDimensionalGlobalSearches.get(0).isLastIteration()){
            oneDimensionalGlobalSearches.get(1).setFunction(getOneDimensionalFunction(1, variables));
            Double executionResult = oneDimensionalGlobalSearches.get(1).findMinimum(false).getKey();
            variables.set(1, executionResult);
            oneDimensionalGlobalSearches.get(0).setFunction(getOneDimensionalFunction(0, variables));
            executionResult = oneDimensionalGlobalSearches.get(0).findMinimum(true).getKey();
            variables.set(0, executionResult);
        }

        Double result = getOneDimensionalFunction(0, variables).setVariable(variableNames.get(0), variables.get(0)).evaluate();
        variables.add(result);
        return variables;
    }

    private void initVariables(List<Double> variables) {
        for(int i = 0; i < variablesCount; i++) {
            double middleOfInterval = (B.get(i) + A.get(i)) / 2;
            variables.add(middleOfInterval);
        }
    }

    private void initOneDimensionalGlobalSearches(List<OneDimensionalGlobalSearch> oneDimensionalGlobalSearches, List<Double> variables) {
        for(int i = 0; i < variablesCount; i++) {
            oneDimensionalGlobalSearches.add(new OneDimensionalGlobalSearch(getOneDimensionalFunction(i, variables), A.get(i), B.get(i), R, E));
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
}
