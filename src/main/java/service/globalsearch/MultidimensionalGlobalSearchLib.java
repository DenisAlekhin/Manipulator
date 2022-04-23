package service.globalsearch;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.BasicConfigurator;
import smile.math.BFGS;
import smile.math.DifferentiableMultivariateFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static service.utils.Constants.ROG_LENGTH;

@RequiredArgsConstructor
public class MultidimensionalGlobalSearchLib {
    public static List<Double> findMinimum(double target_x, double target_y){
        BasicConfigurator.configure();
        DifferentiableMultivariateFunction func = new DifferentiableMultivariateFunction() {
            @Override
            public double g(double[] doubles, double[] doubles1) {
                return Math.sqrt((target_x-(75+ROG_LENGTH*1.0*Math.cos(doubles[0])+ROG_LENGTH*1.0*Math.cos(doubles[0]+doubles[1])))
                        *(target_x-(75+ROG_LENGTH*1.0*Math.cos(doubles[0])+ROG_LENGTH*1.0*Math.cos(doubles[0]+doubles[1])))
                        +(target_y-(ROG_LENGTH+ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1])))
                        *(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1]))));
            }

            @Override
            public double f(double[] doubles) {
                return Math.sqrt((target_x-(75+ROG_LENGTH *1.0*Math.cos(doubles[0])+ROG_LENGTH *1.0*Math.cos(doubles[0]+doubles[1])))
                        *(target_x-(75+ROG_LENGTH *1.0*Math.cos(doubles[0])+ROG_LENGTH *1.0*Math.cos(doubles[0]+doubles[1])))
                        +(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1])))
                        *(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1]))));
            }
        };
        double[] x = new double[2];

        double res = BFGS.minimize(func, x, 0.0001, 200);
        return new ArrayList<>(Arrays.asList(x[0], x[1], res));
    }
}
