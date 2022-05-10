package app.globalsearch.jmetal;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import app.service.Iterations;

import java.util.ArrayList;
import java.util.List;

import static app.utils.Constants.*;

public class CustomProblem extends AbstractDoubleProblem {
    private static final long serialVersionUID = 1L;
    private final double target_x;
    private final double target_y;

    public CustomProblem(double target_x, double target_y) {
        this.target_x = target_x;
        this.target_y = target_y;
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
        Iterations.add(1);
        double x0 = solution.getVariableValue(0);
        double x1 = solution.getVariableValue(1);
        double y = Math.sqrt(Math.pow(target_x-(MANIP_START_X+ROG_LENGTH*1.0*Math.cos(x0)+ROG_LENGTH*1.0*Math.cos(x0+x1)), 2)
                +Math.pow(target_y-(MANIP_START_Y+ROG_LENGTH *1.0*Math.sin(x0)+ROG_LENGTH *1.0*Math.sin(x0+x1)), 2));
        solution.setObjective(0, y);
    }
}
