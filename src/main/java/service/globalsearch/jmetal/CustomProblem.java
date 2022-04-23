package service.globalsearch.jmetal;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

import static service.utils.Constants.ROG_LENGTH;

public class CustomProblem extends AbstractDoubleProblem {
    private static final long serialVersionUID = 1L;
    private final double target_x;
    private final double target_y;

    public CustomProblem(double target_x, double target_y) {
        this.target_x = target_x;
        this.target_y = target_y - 25;
        setNumberOfVariables(2);
        setNumberOfObjectives(1);
        setName("test");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());
        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-Math.PI);
            upperLimit.add(Math.PI);
        }
        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        //Get the value of the decision variable
        double x0 = solution.getVariableValue(0);
        double x1 = solution.getVariableValue(1);
        //Calculate fitness
        double y = Math.sqrt((target_x-(75+ROG_LENGTH*1.0*Math.cos(x0)+ROG_LENGTH*1.0*Math.cos(x0+x1)))
                *(target_x-(75+ROG_LENGTH*1.0*Math.cos(x0)+ROG_LENGTH*1.0*Math.cos(x0+x1)))
                +(target_y-(ROG_LENGTH+ROG_LENGTH *1.0*Math.sin(x0)+ROG_LENGTH *1.0*Math.sin(x0+x1)))
                *(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(x0)+ROG_LENGTH *1.0*Math.sin(x0+x1))));
        //Set the fitness value of the solution
        solution.setObjective(0, y);
    }
}
