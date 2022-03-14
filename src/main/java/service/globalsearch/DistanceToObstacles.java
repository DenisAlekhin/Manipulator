package service.globalsearch;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static service.utils.StringConstants.OBSTACLE_RADIUS;
import static service.utils.StringConstants.ROG_LENGTH;
import static service.utils.StringConstants.SCR_COORD_MANIP_START_X;
import static service.utils.StringConstants.SCR_COORD_MANIP_START_Y;

@RequiredArgsConstructor
@Setter
public class DistanceToObstacles {
    private final List<Point2D> obstacles;
    private final Integer indexOfVariable;
    private List<Double> fixedVariables;



    public boolean rodCorrespondLimitations(Integer numberOfRod, Double point) throws Exception{
        if(numberOfRod < 1 || numberOfRod > 3) {
            throw new Exception("Error: передан неверный номер штанги");
        }
        if(point < -Math.PI || point > Math.PI) {
            throw new Exception("Error: передана неверая точка");
        }
        setPoint(point);
        ArrayList<Double> distancesToObstacle;
        mockRodCompression();
        switch(numberOfRod) {
            case 1 -> distancesToObstacle = firstRodDistToObstacles();
            case 2 -> distancesToObstacle = secondRodDistToObstacles();
            case 3 -> distancesToObstacle = thirdRodDistToObstacles();
            default -> distancesToObstacle = new ArrayList<>();
        }

        for(Double distToObstacle: distancesToObstacle) {
            if(distToObstacle < 0) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Double> firstRodDistToObstacles() {
        ArrayList<Double> distToObstacles = new ArrayList<>();
        Line2D firstRod = new Line2D.Double(
                new Point2D.Double(SCR_COORD_MANIP_START_X, SCR_COORD_MANIP_START_Y),
                new Point2D.Double(SCR_COORD_MANIP_START_X+ROG_LENGTH*fixedVariables.get(1)*Math.cos(fixedVariables.get(0)),
                        SCR_COORD_MANIP_START_Y+ROG_LENGTH*fixedVariables.get(1)*Math.sin(fixedVariables.get(0))));

        for (Point2D obstacle : obstacles) {
            distToObstacles.add(firstRod.ptSegDist(obstacle) - OBSTACLE_RADIUS);
        }
        return distToObstacles;
    }

    private ArrayList<Double> secondRodDistToObstacles() {
        ArrayList<Double> distToObstacles = new ArrayList<>();
        Line2D secondRod = new Line2D.Double(
                new Point2D.Double(SCR_COORD_MANIP_START_X+ROG_LENGTH*fixedVariables.get(1)*Math.cos(fixedVariables.get(0)),
                        SCR_COORD_MANIP_START_Y+ROG_LENGTH*fixedVariables.get(1)*Math.sin(fixedVariables.get(0))),
                new Point2D.Double(SCR_COORD_MANIP_START_X+ROG_LENGTH*fixedVariables.get(1)*Math.cos(fixedVariables.get(0))+
                        ROG_LENGTH*fixedVariables.get(3)*Math.cos(fixedVariables.get(0)+fixedVariables.get(2)),
                        SCR_COORD_MANIP_START_Y+ROG_LENGTH*fixedVariables.get(1)*Math.sin(fixedVariables.get(0))+
                                ROG_LENGTH*fixedVariables.get(3)*Math.sin(fixedVariables.get(0)+fixedVariables.get(2))));

        for (Point2D obstacle : obstacles) {
            distToObstacles.add(secondRod.ptSegDist(obstacle) - OBSTACLE_RADIUS);
        }
        return distToObstacles;
    }

    private ArrayList<Double> thirdRodDistToObstacles() {
        ArrayList<Double> distToObstacles = new ArrayList<>();
        Line2D thirdRod = new Line2D.Double(
                new Point2D.Double(SCR_COORD_MANIP_START_X+ROG_LENGTH*fixedVariables.get(1)*Math.cos(fixedVariables.get(0))+
                        ROG_LENGTH*fixedVariables.get(3)*Math.cos(fixedVariables.get(0)+fixedVariables.get(2)),
                        SCR_COORD_MANIP_START_Y+ROG_LENGTH*fixedVariables.get(1)*Math.sin(fixedVariables.get(0))+
                                ROG_LENGTH*fixedVariables.get(3)*Math.sin(fixedVariables.get(0)+fixedVariables.get(2))),
                new Point2D.Double(SCR_COORD_MANIP_START_X+ROG_LENGTH*fixedVariables.get(1)*Math.cos(fixedVariables.get(0))+
                        ROG_LENGTH*fixedVariables.get(3)*Math.cos(fixedVariables.get(0)+fixedVariables.get(2))+
                        ROG_LENGTH*fixedVariables.get(5)*Math.cos(fixedVariables.get(0)+fixedVariables.get(2)+fixedVariables.get(4)),
                        SCR_COORD_MANIP_START_Y+ROG_LENGTH*fixedVariables.get(1)*Math.sin(fixedVariables.get(0))+
                                ROG_LENGTH*fixedVariables.get(3)*Math.sin(fixedVariables.get(0)+fixedVariables.get(2))+
                                ROG_LENGTH*fixedVariables.get(5)*Math.sin(fixedVariables.get(0)+fixedVariables.get(2)+fixedVariables.get(4))));
        for (Point2D obstacle : obstacles) {
            distToObstacles.add(thirdRod.ptSegDist(obstacle) - OBSTACLE_RADIUS);
        }
        return distToObstacles;
    }

    private void setPoint(double point) {
        fixedVariables.set(indexOfVariable, point);
    }

    private void mockRodCompression() {
        fixedVariables.add(1, 1.0);
        fixedVariables.add(3, 1.0);
        fixedVariables.add(4, 0.0);
        fixedVariables.add(5, 1.0);
    }
}
