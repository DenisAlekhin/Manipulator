package service.globalsearch;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static service.utils.Constants.OBSTACLE_RADIUS;
import static service.utils.Constants.ROG_LENGTH;
import static service.utils.Constants.SCR_COORD_MANIP_START_X;
import static service.utils.Constants.SCR_COORD_MANIP_START_Y;

@RequiredArgsConstructor
@Setter
public class DistanceToObstacles {
    private final List<Point2D> obstacles;
    private final Integer indexOfVariable;
    private List<Double> fixedVariables;
    private Integer mockIndexOfVariable;



    public boolean rodCorrespondLimitations(Integer numberOfRod, Double point) throws Exception{
        if(numberOfRod < 1 || numberOfRod > 3) {
            throw new Exception("Error: передан неверный номер штанги");
        }
        if(point < -Math.PI || point > Math.PI) {
            throw new Exception("Error: передана неверая точка");
        }
        if(Objects.isNull(fixedVariables)) {
            throw new Exception("Error: не установленны значения переменных");
        }

        List<Double> distancesToObstacle;
        switch(numberOfRod) {
            case 1 -> distancesToObstacle = firstRodDistToObstacles(point);
            case 2 -> distancesToObstacle = secondRodDistToObstacles(point);
            case 3 -> distancesToObstacle = thirdRodDistToObstacles(point);
            default -> distancesToObstacle = new ArrayList<>();
        }

        for(Double distToObstacle: distancesToObstacle) {
            if(distToObstacle < 0) {
                return false;
            }
        }
        return true;
    }

    public List<Double> firstRodDistToObstacles(Double point) throws Exception{
        if(Objects.isNull(fixedVariables)) {
            throw new Exception("Error: не установленны значения переменных");
        }

        mockRodCompression();
        setPoint(point);
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

    public List<Double> secondRodDistToObstacles(Double point) throws Exception{
        if(Objects.isNull(fixedVariables)) {
            throw new Exception("Error: не установленны значения переменных");
        }

        mockRodCompression();
        setPoint(point);
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

    public ArrayList<Double> thirdRodDistToObstacles(Double point) throws Exception{
        if(Objects.isNull(fixedVariables)) {
            throw new Exception("Error: не установленны значения переменных");
        }

        mockRodCompression();
        setPoint(point);
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
        fixedVariables.set(mockIndexOfVariable, point);
    }

    private void mockRodCompression() {
        switch (indexOfVariable) {
            case 0 -> mockIndexOfVariable = 0;
            case 1 -> mockIndexOfVariable = 2;
        }
        if(fixedVariables.size() == 2) {
            fixedVariables.add(1, 1.0);
            fixedVariables.add(3, 1.0);
            fixedVariables.add(4, 0.0);
            fixedVariables.add(5, 1.0);
        }
    }

    public boolean pointCorrespondLimitations(double point) throws Exception{
        for(int i = 1; i < 4; i++) {
            if(!rodCorrespondLimitations(i, point)) {
                return false;
            }
        }

        return true;
    }
}
