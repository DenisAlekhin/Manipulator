package app.globalsearch;

import app.service.DistanceToObstacles;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static app.utils.Constants.OBSTACLE_RADIUS;
import static app.utils.Constants.ROG_LENGTH;
import static app.utils.Constants.SCR_COORD_MANIP_START_X;
import static app.utils.Constants.SCR_COORD_MANIP_START_Y;

public class DistanceToObstaclesTest {

    @Test
    public void obstacleAtFirstRodTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X + ROG_LENGTH - OBSTACLE_RADIUS, SCR_COORD_MANIP_START_Y));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertFalse(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }

    @Test
    public void obstacleAtSecondRodTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X + ROG_LENGTH + OBSTACLE_RADIUS, SCR_COORD_MANIP_START_Y));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertFalse(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }

    @Test
    public void obstacleAtThirdRodTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X + ROG_LENGTH * 2 + OBSTACLE_RADIUS, SCR_COORD_MANIP_START_Y));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertFalse(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }

    @Test
    public void obstacleAtFirstAndSecondRodsTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X + ROG_LENGTH, SCR_COORD_MANIP_START_Y));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertFalse(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertFalse(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }

    @Test
    public void obstacleAtSecondAndThirdRodsTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X + ROG_LENGTH * 2, SCR_COORD_MANIP_START_Y));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertFalse(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertFalse(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }

    @Test
    public void obstacleNearFirstRodTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X - OBSTACLE_RADIUS, SCR_COORD_MANIP_START_Y));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }

    @Test
    public void obstacleNearSecondRodTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X + ROG_LENGTH, SCR_COORD_MANIP_START_Y + OBSTACLE_RADIUS));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }

    @Test
    public void obstacleNearThirdRodTest() throws Exception{
        List<Point2D> obstacles = new ArrayList<>();
        obstacles.add(new Point2D.Double(SCR_COORD_MANIP_START_X + ROG_LENGTH * 3 + OBSTACLE_RADIUS, SCR_COORD_MANIP_START_Y));

        List<Double> variables = new ArrayList<>(Arrays.asList(0.0, 1.0));

        DistanceToObstacles distanceToObstacles = new DistanceToObstacles(obstacles, 1);
        distanceToObstacles.setFixedVariables(variables);
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(1, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(2, 0.0));
        Assert.assertTrue(distanceToObstacles.rodCorrespondLimitations(3, 0.0));
    }
}
