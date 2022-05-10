package app.globalsearch.multidimensional;

import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.math.Function;
import jsat.math.FunctionBase;
import jsat.math.optimization.NelderMead;
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
import app.service.Iterations;
import app.globalsearch.jmetal.CustomProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static app.utils.Constants.*;

@RequiredArgsConstructor
@Slf4j
public class MultidimensionalGlobalSearchLib {

    public static List<Double> findMinimumJmetal(double target_x, double target_y, Expression func){
        Iterations.reset();
        Problem<DoubleSolution> problem = new CustomProblem(target_x, target_y);

        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
                new RankingAndCrowdingDistanceComparator<>());

        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection).setMaxEvaluations(100000).build();
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

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

    public static List<Double> findMinimumNelderMead(double target_x, double target_y, Expression func, List<Double> assumption, Double localPrecision){
        Function funcToCalc = new FunctionBase() {
            @Override
            public double f(Vec vec) {
                Iterations.add(1);
                return Math.sqrt(Math.pow(target_x-(MANIP_START_X+ROG_LENGTH*1.0*Math.cos(vec.get(0))+ROG_LENGTH*1.0*Math.cos(vec.get(0)+vec.get(1))), 2)
                        +Math.pow(target_y-(MANIP_START_Y+ROG_LENGTH *1.0*Math.sin(vec.get(0))+ROG_LENGTH *1.0*Math.sin(vec.get(0)+vec.get(1))), 2));
            }
        };

        NelderMead optimizer = new NelderMead();
        assumption.remove(2);
        Vec guess = new DenseVector(assumption);
        List<Vec> guesses = new ArrayList<>();
        guesses.add(guess);
        Vec result = optimizer.optimize(localPrecision, 50000, funcToCalc, guesses);
        log.debug(result.toString());
        return getResult(result, func);
    }

    private static List<Double> getResult(Vec res, Expression func) {
        double x0 = res.get(0);
        double x1 = res.get(1);
        func.setVariable("x0", x0);
        func.setVariable("x1", x1);

        return new ArrayList<>(Arrays.asList(x0, x1, func.evaluate()));
    }
}
