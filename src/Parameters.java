/**
 * Created by nate on 7/7/17.
 */

// Basically just every parameter this program uses.
public class Parameters {
    static int populationSize = 20;
    static double survivalRate = 0.2;
    static int generationsAllowedNoImprovement = 15;
    static int maxPermittedNeurons = 100;
    static double chanceMutateLink = 0.07;       // 0.07
    static double chanceMutateNode = 0.01;       // 0.03
    static double chanceMutateWeight = 0.3;     // 0.2
    static double chanceMutateEnable = 0.3;     // 0.1
    static double chanceMutateThreshold = 0.1;  // 0.1
    static double mutationRate = 0.1;           // 0.1
    static double maxWeightPerturbation = 0.5;
    static double probabilityWeightReplaced = 0.1;
    static double maxActivationPerturbation = 0.1;
    static double compatibilityThreshold = 0.5;
    static int oldAgeThreshold = 50;
    static double oldAgePenalty = 0.7;
    static double youngFitnessBonus = 1.3;
    static int youngBonusAgeThreshhold = 10;
    static double crossoverRate = 0.7;
    static int maxNumberOfSpecies = 0;
    static int sizeThreshold = 5;
    static double successfulFitness = 100.0;
    static int stressEventInterval = 5;
}
