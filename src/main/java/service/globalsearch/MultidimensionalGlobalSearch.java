package service.globalsearch;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MultidimensionalGlobalSearch {
    private final Expression FUNC;
    private final String FUNC_STR;
    private final List<Double> A, B;
    private final Double R, E;
    private final Integer variablesCount;
    private final Map<Integer, String> variableNames;
    private final List<List<Double>> analysis;
    List<OneDimensionalGlobalSearch> oneDimensionalGlobalSearches;
    List<Double> variables;

//    private final boolean onlyHingesMoves;

    public MultidimensionalGlobalSearch(Expression func, String funcStr, List<Double> a, List<Double> b,
                                 Double r, Double e) {
        FUNC = func;
        FUNC_STR = funcStr;
        A = a;
        B = b;
        R = r;
        E = e;

        Set<String> funcVariableNames = FUNC.getVariableNames();
        variablesCount = funcVariableNames.size();

        variableNames = new HashMap<>(variablesCount);
        int j = 0;
        for(String variableName: funcVariableNames) {
            variableNames.put(j++, variableName);
        }
        analysis = new ArrayList<>();

        variables = new ArrayList<>(variablesCount);
        for(int i = 0; i < variablesCount; i++) {
            double middleOfInterval = (B.get(i) + A.get(i)) / 2;
            variables.add(middleOfInterval);
        }

        oneDimensionalGlobalSearches = new ArrayList<>(variablesCount);
        for(int i = 0; i < variablesCount; i++) {
            oneDimensionalGlobalSearches.add(new OneDimensionalGlobalSearch(getOneDimensionalFunction(i, variables), A.get(i), B.get(i), R, E));
        }
    }

    public List<Double> findMinimum(){
        while(!oneDimensionalGlobalSearches.get(0).isLastIteration()){
            oneDimensionalGlobalSearches.get(1).setFunction(getOneDimensionalFunction(1, variables));
            Double executionResult = oneDimensionalGlobalSearches.get(1).findMinimum(false).getKey();
            variables.set(1, executionResult);

            oneDimensionalGlobalSearches.get(0).setFunction(getOneDimensionalFunction(0, variables));
            executionResult = oneDimensionalGlobalSearches.get(0).findMinimum(true).getKey();
            variables.set(0, executionResult);

            analysis.add(new ArrayList<>(variables));
        }
        List<Double> result = getResult();
        return result;
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

    private List<Double> getResult() {
        List<Double> result =  analysis.stream().filter(values -> values.get(0).equals(oneDimensionalGlobalSearches.get(0).getAnalysis().get(0).getKey())).findAny().orElse(null);

        Double funcValue = getOneDimensionalFunction(0, result).setVariable(variableNames.get(0), variables.get(0)).evaluate();
        result.add(funcValue);

        return result;
    }
}
