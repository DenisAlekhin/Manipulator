package service.globalsearch;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class OneDimensionalGlobalSearchTest {
    private static final String LINEAR_FUNCTION_STR = "2*x";
    private static final String LINEAR_FUNCTION_VARIABLE = "x";
    private static final String LINEAR_FUNCTION_STR_SECOND_STATE = "5*x";
    private static final Double A = -5.0;
    private static final Double B = 4.0;
    private static final Double R = 1.5;
    private static final Double E = 0.01;

    private static final Expression LINEAR_FUNCTION = new ExpressionBuilder(LINEAR_FUNCTION_STR)
            .variables(LINEAR_FUNCTION_VARIABLE)
            .build();
    private static final Expression LINEAR_FUNCTION_SECOND_STATE = new ExpressionBuilder(LINEAR_FUNCTION_STR_SECOND_STATE)
            .variables(LINEAR_FUNCTION_VARIABLE)
            .build();

    @Test
    public void multipleIterationsTest() {
        OneDimensionalGlobalSearch oneDimensionalGlobalSearch =
                new OneDimensionalGlobalSearch(LINEAR_FUNCTION, A, B, R, E);
        Assertions.assertEquals(new Pair<>(-5.0, -10.0), oneDimensionalGlobalSearch.findMinimum(false));

    }

    @Test
    public void oneIterationTest() {
        OneDimensionalGlobalSearch oneDimensionalGlobalSearch =
                new OneDimensionalGlobalSearch(LINEAR_FUNCTION, A, B, R, E);
        Assertions.assertEquals(new Pair<>(-3.5, -7.0), oneDimensionalGlobalSearch.findMinimum(true));
        Assertions.assertEquals(false, oneDimensionalGlobalSearch.isLastIteration());
    }

    @Test
    public void mainTest() {
        OneDimensionalGlobalSearch oneDimensionalGlobalSearch =
                new OneDimensionalGlobalSearch(LINEAR_FUNCTION, A, B, R, E);
        Assertions.assertEquals(new Pair<>(-3.5, -7.0), oneDimensionalGlobalSearch.findMinimum(true));
        Assertions.assertEquals(false, oneDimensionalGlobalSearch.isLastIteration());
        oneDimensionalGlobalSearch.setFunction(LINEAR_FUNCTION_SECOND_STATE);
        Assertions.assertEquals(new Pair<>(-4.75, -23.75), oneDimensionalGlobalSearch.findMinimum(true));
        Assertions.assertEquals(false, oneDimensionalGlobalSearch.isLastIteration());
    }
}
