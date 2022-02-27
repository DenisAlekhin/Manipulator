package service.globalsearch;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MultidimensionalGlobalSearchTest {
    private static final Double R = 1.5;
    private static final Double E = 0.01;
    private static final String FUNCTION_STR = "sqrt((258.0-(75+125*1.0*cos(x0)+125*1.0*cos(x0+x1)))*(258.0-(75+125*1.0*cos(x0)+125*1.0*cos(x0+x1)))+(152.0-(116+125*1.0*sin(x0)+125*1.0*sin(x0+x1)))*(152.0-(116+125*1.0*sin(x0)+125*1.0*sin(x0+x1))))";
    private static final Integer VARIABLE_COUNT = 3;
    private static final String FUNCTION_VARIABLE_0 = "x0";
    private static final String FUNCTION_VARIABLE_1 = "x1";
    private static final String FUNCTION_VARIABLE_2 = "x2";
    private static final List<Double> BOUND_A = new ArrayList<>();
    private static final List<Double> BOUND_B = new ArrayList<>();
    private static Expression FUNCTION;

    @Before
    public void setup() {
        FUNCTION = new ExpressionBuilder(FUNCTION_STR)
                .variables(FUNCTION_VARIABLE_0, FUNCTION_VARIABLE_1, FUNCTION_VARIABLE_2)
                .build();

        for(int i = 0; i < VARIABLE_COUNT; i++) {
            BOUND_A.add(-Math.PI);
            BOUND_B.add(Math.PI);
        }
    }

    @Test
    public void mainTest() {
        MultidimensionalGlobalSearch multidimensionalGlobalSearch =
                new MultidimensionalGlobalSearch(FUNCTION, FUNCTION_STR, BOUND_A, BOUND_B, R, E);
        System.out.println(multidimensionalGlobalSearch.findMinimum());
    }
}
