package service.globalsearch;

import jsat.linear.ConcatenatedVec;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.math.Function;
import jsat.math.FunctionBase;
import jsat.math.optimization.BFGS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import service.globalsearch.jmetal.CustomProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static service.utils.Constants.ROG_LENGTH;

@RequiredArgsConstructor
@Slf4j
public class MultidimensionalGlobalSearchLib {
//    public static List<Double> findMinimumBFGS(double target_x, double target_y){
//        BasicConfigurator.configure();
//        DifferentiableMultivariateFunction func = new DifferentiableMultivariateFunction() {
//            @Override
//            public double g(double[] doubles, double[] doubles1) {
//                return 0.0;
//            }
//
//            @Override
//            public double f(double[] doubles) {
//                return Math.sqrt((target_x-(75+ROG_LENGTH *1.0*Math.cos(doubles[0])+ROG_LENGTH *1.0*Math.cos(doubles[0]+doubles[1])))
//                        *(target_x-(75+ROG_LENGTH *1.0*Math.cos(doubles[0])+ROG_LENGTH *1.0*Math.cos(doubles[0]+doubles[1])))
//                        +(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1])))
//                        *(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1]))));
//            }
//        };
//        double[] x = new double[2];
//
//        double res = BFGS.minimize(func, x, 0.0001, 200);
//        return new ArrayList<>(Arrays.asList(x[0], x[1], res));
//    }

    public static List<Double> findMinimumJmetal(double target_x, double target_y, Expression func){
        //Define the optimization problem
        Problem<DoubleSolution> problem = new CustomProblem(target_x, target_y);

        //Configure SBX crossover operator
        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
        //Configure mutation operator
        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
        //Configure selection operator
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
                new RankingAndCrowdingDistanceComparator<>());

        //Register the component to the algorithm
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection).setMaxEvaluations(25000).build();
        //Run the algorithm with AlgorithmRunner
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor((Algorithm<?>) algorithm).execute();

        JMetalLogger.logger.info("Total execution time: "+ algorithmRunner.getComputingTime() + "ms");

        return getResult(algorithm.getResult(), func);
    }

    private static List<Double> getResult(List<DoubleSolution> res, Expression func) {
        double x0 = res.get(0).getVariableValue(0);
        double x1 = res.get(0).getVariableValue(1);
        func.setVariable("x0", x0);
        func.setVariable("x1", x1);

        return new ArrayList<>(Arrays.asList(x0, x1, func.evaluate()));
    }

    public static List<Double> findMinimumBFGS(double target_x, double target_y){
//        BasicConfigurator.configure();
//        DifferentiableMultivariateFunction func = new DifferentiableMultivariateFunction() {
//            @Override
//            public double g(double[] doubles, double[] doubles1) {
//                return 0.0;
//            }
//
//            @Override
//            public double f(double[] doubles) {
//                return Math.sqrt((target_x-(75+ROG_LENGTH *1.0*Math.cos(doubles[0])+ROG_LENGTH *1.0*Math.cos(doubles[0]+doubles[1])))
//                        *(target_x-(75+ROG_LENGTH *1.0*Math.cos(doubles[0])+ROG_LENGTH *1.0*Math.cos(doubles[0]+doubles[1])))
//                        +(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1])))
//                        *(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(doubles[0])+ROG_LENGTH *1.0*Math.sin(doubles[0]+doubles[1]))));
//            }
//        };
//        double[] x = new double[2];
//
//        double res = BFGS.minimize(func, x, 0.0001, 200);
//        return new ArrayList<>(Arrays.asList(x[0], x[1], res));

        BFGS bfgs = new BFGS();

        Function func = new FunctionBase() {
            @Override
            public double f(Vec vec) {
                return Math.sqrt((target_x-(75+ROG_LENGTH *1.0*Math.cos(vec.get(0))+ROG_LENGTH *1.0*Math.cos(vec.get(0)+vec.get(1))))
                        *(target_x-(75+ROG_LENGTH *1.0*Math.cos(vec.get(0))+ROG_LENGTH *1.0*Math.cos(vec.get(0)+vec.get(1))))
                        +(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(vec.get(0))+ROG_LENGTH *1.0*Math.sin(vec.get(0)+vec.get(1))))
                        *(target_y-(ROG_LENGTH +ROG_LENGTH *1.0*Math.sin(vec.get(0))+ROG_LENGTH *1.0*Math.sin(vec.get(0)+vec.get(1)))));
            }
        };
        double[] x = new double[2];
        Vec res = new DenseVector(2);
        Vec assumption = new DenseVector(2);

        bfgs.optimize(1e-4, res, assumption, func, null, null);

        log.info(res.toString());
        return new ArrayList<>();
    }
}
