/**
 * Created by nate on 7/7/17.
 */

// Basically just every parameter this program uses.
public class Parameters {
    static int framesPerSecond = 60;
    static double maxTurnRate = 0.2;
    static int sweeperScale = 2;
    static int numSensors = 5;
    static double sensorRange = 15;
    static int popSize = 150;
    static int numTicks = 5000;
    static double cellSize = 5;
    static int numAddLinkAttempts = 5;
    static double survivalRate = 0.2;
    static int generationsAllowedNoImplovement = 15;
    static int maxPermittedNeurons = 100;
    static double chanceAddLink = 0.07;
    static double chanceAddNode = 0.04;
    static double chanceAddRecurrentLink = 0.05;
    static double mutationRate = 0.2;
    static double maxWeightPerturbation = 0.5;
    static double probabilityWeightReplaced = 0.1;
    static double activationMutationRate = 0.1;
    static double maxActivationPerturbation = 0.1;
    static double compatibilityThreshold = 0.5;
    static int oldAgeThreshold = 50;
    static double oldAgePenalty = 0.7;
    static double youngFitnessBonus = 1.3;
    static int youngBonusAgeThreshhold = 10;
    static double crossoverRate = 0.7;
    static int maxNumberOfSpecies = 0;
}
