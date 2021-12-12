import VisualApp.GlobalSearch.GlobalSearch;
import jdk.nashorn.internal.objects.annotations.Function;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CalcGvTest {
    @Test
    public void calcGvWithVEqZeroTest(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(0.0, 0.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
        1.0,1.0, false);
        Assert.assertEquals(globalSearch.calcGv(0,
                0,
                points,
                0,
                obstacles
                ),0, 0);
    }
    @Test
    public void calcGvWithVEqOneTest(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(0.0, 0.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        Assert.assertEquals(globalSearch.calcGv(0,
                1,
                points,
                0,
                obstacles
        ),0, 0);
    }
    @Test
    public void calcGvWithVEqTwoTest1(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(138.0, 185.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqTwoTest2(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(135.0, 155.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) < 0);
    }
    @Test
    public void calcGvWithVEqTwoTest3(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(134.0, 126.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) > 0);
    }

    @Test
    public void calcGvWithVEqTwoTest4(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(31.0, 153.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) > 0);
    }

    @Test
    public void calcGvWithVEqTwoTest5(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(250.0, 155.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) > 0);
    }

    @Test
    public void calcGvWithVEqTwoTest6(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(78.0, 156.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) < 0);
    }
    @Test
    public void calcGvWithVEqTwoTest7(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(78.0, 202.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqTwoTest8(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(78.0, 175.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) < 0);
    }
    @Test
    public void calcGvWithVEqTwoTest9(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(113.0, 155.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                2,
                points,
                0,
                obstacles
        ) < 0);
    }
    @Test
    public void calcGvWithVEqTreeTest1(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(177.0, 156.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqTreeTest2(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(354.0, 156.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqTreeTest3(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(265.5, 130.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqTreeTest4(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(265.5, 182.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqTreeTest5(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(265.5, 156.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) < 0);
    }
    @Test
    public void calcGvWithVEqTreeTest6(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(205.0, 154.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) < 0);
    }
    @Test
    public void calcGvWithVEqTreeTest7(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(206.0, 211.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqTreeTest8(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(205.0, 93.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                3,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqFourTest1(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(302.0, 156.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqFourTest2(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(479.0, 156.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqFourTest3(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(390.5, 130.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqFourTest4(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(390.5, 182.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ) > 0);
    }
    @Test
    public void calcGvWithVEqFourTest5(){
        ArrayList<Double> points = new ArrayList<Double>();
        ArrayList<Point2D> obstacles = new ArrayList<Point2D>();
        obstacles.add(new Point2D.Double(390.5, 156.0));
        points.add(0.0);
        points.add(0.0);
        Expression expression = new ExpressionBuilder("x0").variable("x0").build();
        GlobalSearch globalSearch = new GlobalSearch(expression, new ArrayList<Double>(),new ArrayList<Double>(),
                1.0,1.0, false);
        System.out.println("Dist to obstacle: " + globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ));
        Assert.assertTrue(globalSearch.calcGv(0,
                4,
                points,
                0,
                obstacles
        ) < 0);
    }
}
