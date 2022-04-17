//package service.globalsearch;
//
//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import java.awt.geom.Point2D;
//import java.util.ArrayList;
//import java.util.List;
//
//public class OneDimensionalGlobalSearchWithLimitationsTest {
//    private static final String FUNCTION_STR = "sqrt((258.0-(75+125*1.0*cos(0)+125*1.0*cos(0+x1)))*(258.0-(75+125*1.0*cos(0)+125*1.0*cos(0+x1)))+(152.0-(116+125*1.0*sin(0)+125*1.0*sin(0+x1)))*(152.0-(116+125*1.0*sin(0)+125*1.0*sin(0+x1))))";
//    private static final String FUNCTION_STR_VARIABLE = "x1";
//    private static final Double A = -5.0;
//    private static final Double B = 4.0;
//    private static final Double R = 1.5;
//    private static final Double E = 0.01;
//
//    private static final Expression FUNCTION = new ExpressionBuilder(FUNCTION_STR)
//            .variables(FUNCTION_STR_VARIABLE)
//            .build();
//
//
//    @Test
//    public void test() {
//        List<Point2D> obstacles = new ArrayList<>();
//        obstacles.add(new Point2D.Double())
//
//        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(new ArrayList<>(), new ArrayList<>(), null);
//
//        OneDimensionalGlobalSearchWithLimitations globalSearch = new OneDimensionalGlobalSearchWithLimitations(FUNCTION, A, B, R, E, distanceToObstacles);
//        globalSearch.findMinimum();
//
//    }
//}
