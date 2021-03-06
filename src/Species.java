import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by nate on 6/29/17.
 */
public class Species implements Comparable<Species> {


    private Genome leader;
    private ArrayList<Genome> speciesMembers = new ArrayList<>();
    private static int globalSpeciesID = 1;
    private int speciesID;
    private double bestFitness;
    private double averageFitness;
    private int timeSinceImprovement;
    private int age;
    private double reproductionRequirements;

    public Species( Genome firstGenome ) {
        leader = firstGenome;
        speciesMembers.add( firstGenome );
        speciesID = globalSpeciesID;
        globalSpeciesID += 1;
        bestFitness = leader.getFitness(1);
        averageFitness = leader.getFitness(1);
        timeSinceImprovement = 0;
        age = 0;

    }

    // This method boosts the fitnesses of the younger genomes, penalizes the fitnesses of the older
    // genomes, and then performs fitness sharing over all members of the species. This may be something
    // to implement in the future, but I really don't like it.
    // public void adjustFinesses() { }

    // Adds a new member to the species.
    public void addMember( Genome newGenome ) {
        // Checks to see if the new genome is already part of the species.
        for( Genome genome : speciesMembers ) {
            if( newGenome == genome ) {
                return;
            }
        }

        // If the genome is not already apart of the species, then it is added to the species arrayList, the average
        // fitness of the species is updated and it is determined whether the new genome has a greater fitness than
        // the current best.
        speciesMembers.add( newGenome );
        updateAverageFitness();
        if( newGenome.getFitness(1) > bestFitness ) {
            bestFitness = newGenome.getFitness(1);
            leader = newGenome;
            timeSinceImprovement = 0;
        }
    }

    // Updates the average fitness of the species by iterating through all members and calculating.
    private void updateAverageFitness() {
        averageFitness = 0;
        for( Genome genome : speciesMembers ) {
            averageFitness += genome.getFitness(1);
        }
        averageFitness = averageFitness / speciesMembers.size();
    }

    // Removes all but the leader from the species so that species can be assigned every epoch.
    public void purge() {
        speciesMembers.clear();
        timeSinceImprovement += 1;
        reproductionRequirements = 0;
    }

    // Spawn amount is determine by calculating the sum of how many offspring each member of the species should have.
    // Each member should have their fitness / averageFitness number of children. However, this is complicated because
    // it requires the average fitness of all genomes, not just the ones in a species.
    public void calculateSpawnAmount() {
        reproductionRequirements = 0;
        for( Genome genome : speciesMembers ) {
            reproductionRequirements += genome.getSpawnAmount();
        }
    }

    // Returns a random genome selected from the best individuals.
    public Genome spawn() {
        Genome newGenome;
        // If the species only contains one genome, then that genome is returned.
        if( speciesMembers.size() == 1 ) {
            newGenome = new Genome( speciesMembers.get( 0 ).getAllGenes() );
        } else {
            // Else, selected a genome from the top 20% of the species.
            // Need to sort in descending order though.
            Collections.sort( speciesMembers, Collections.reverseOrder() );
            //Collections.sort( speciesMembers );
            int maxIndexSize = (int)( Parameters.survivalRate * speciesMembers.size() ) + 1;
            Random ran = new Random();
            newGenome = new Genome( speciesMembers.get( ran.nextInt( maxIndexSize ) ).getAllGenes() );
        }
        return newGenome;
    }

    // Boosts the fitnesses of the young, penalizes the fitnesses of the old, and then performs fitness sharing over
    // all members of the species.
    public void adjustFitness() {
        double total = 0;

        for( Genome genome : speciesMembers ) {
            double fitness = genome.getFitness( 1 );

            // We want to boost the fitness scores if the species is young
            if( age < Parameters.youngBonusAgeThreshhold ) {
                fitness *= Parameters.youngFitnessBonus;
            }

            // We also want to punish older species
            if( age > Parameters.oldAgeThreshold ) {
                fitness *= Parameters.oldAgePenalty;
            }

            total += fitness;

            genome.setAdjustedFitness( fitness / speciesMembers.size() );
        }

    }


    @Override
    public int compareTo(Species species) {
        if( this.bestFitness < species.getBestFitness() ) {
            return -1;
        } else if( species.getBestFitness() < this.bestFitness ) {
            return 1; }
        return 0;
    }

    // GETTER METHODS
    public Genome getLeader() {
        return leader;
    }
    public int getNumberOfMembers() {
        return speciesMembers.size();
    }
    public int getSpeciesID() {
        return speciesID;
    }
    public double getBestFitness() {
        bestFitness = 0;
        for( Genome genome : speciesMembers ) {
            if( genome.getFitness(1) > bestFitness ) {
                bestFitness = genome.getFitness(1);
                leader = genome;
            }
        }
        return bestFitness;
    }
    public double getAverageFitness() {
        return averageFitness;
    }
    public int getTimeSinceImprovement() {
        return timeSinceImprovement;
    }
    public int getAge() {
        return age;
    }
    public void incrementAge() {
        age += 1;
    }
    public double getReproductionRequirements() {
        return reproductionRequirements;
    }

}
