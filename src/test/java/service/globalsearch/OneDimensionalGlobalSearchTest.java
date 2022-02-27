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
    private static Expression LINEAR_FUNCTION;
    private static Expression LINEAR_FUNCTION_SECOND_STATE;

    @Before
    public void setup() {
        LINEAR_FUNCTION = new ExpressionBuilder(LINEAR_FUNCTION_STR)
                .variables(LINEAR_FUNCTION_VARIABLE)
                .build();
        LINEAR_FUNCTION_SECOND_STATE = new ExpressionBuilder(LINEAR_FUNCTION_STR_SECOND_STATE)
                .variables(LINEAR_FUNCTION_VARIABLE)
                .build();
    }

    @Test
    public void multipleIterationsTest() {
        OneDimensionalGlobalSearch oneDimensionalGlobalSearch =
                new OneDimensionalGlobalSearch(LINEAR_FUNCTION, -5.0, 4.0, 1.5, 0.01);
        Assertions.assertEquals(new Pair<>(-5.0, -10.0), oneDimensionalGlobalSearch.findMinimum(false));

    }

    @Test
    public void oneIterationTest() {
        OneDimensionalGlobalSearch oneDimensionalGlobalSearch =
                new OneDimensionalGlobalSearch(LINEAR_FUNCTION, -5.0, 4.0, 1.5, 0.01);
        Assertions.assertEquals(new Pair<>(-3.5, -7.0), oneDimensionalGlobalSearch.findMinimum(true));
        Assertions.assertEquals(false, oneDimensionalGlobalSearch.isLastIteration());
    }

    @Test
    public void mainTest() {
        OneDimensionalGlobalSearch oneDimensionalGlobalSearch =
                new OneDimensionalGlobalSearch(LINEAR_FUNCTION, -5.0, 4.0, 1.5, 0.01);
        Assertions.assertEquals(new Pair<>(-3.5, -7.0), oneDimensionalGlobalSearch.findMinimum(true));
        Assertions.assertEquals(false, oneDimensionalGlobalSearch.isLastIteration());
        oneDimensionalGlobalSearch.setFunction(LINEAR_FUNCTION_SECOND_STATE);
        Assertions.assertEquals(new Pair<>(-4.75, -23.75), oneDimensionalGlobalSearch.findMinimum(true));
        Assertions.assertEquals(false, oneDimensionalGlobalSearch.isLastIteration());
    }
}
