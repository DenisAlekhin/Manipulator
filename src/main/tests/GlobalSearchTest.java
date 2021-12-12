import VisualApp.GlobalSearch.GlobalSearch;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GlobalSearchTest {
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
}
