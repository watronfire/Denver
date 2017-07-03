import java.util.ArrayList;

/**
 * Created by nate on 6/29/17.
 */
public class Species implements Comparable<Species> {


    private Genome leader;
    private ArrayList<Genome> speciesMembers = new ArrayList<>();
    private int speciesID;
    private double bestFitness;
    private double averageFitness;
    private int timeSinceImprovement;
    private int age;
    private double reproductionRequirements;

    // TODO: complete all this shit below.

    public Species( Genome firstGenome, int speciesID ) {
        leader = firstGenome;
        speciesMembers.add( firstGenome );
        this.speciesID = speciesID;
        bestFitness = leader.getFitness();
        averageFitness = leader.getFitness();
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
        for( Genome genome: speciesMembers ) {
            if( newGenome == genome ) {
                return;
            }
        }

        // If the genome is not already apart of the species, then it is added to the species arrayList, the average
        // fitness of the species is updated and it is determined whether the new genome has a greater fitness than
        // the current best.
        speciesMembers.add( newGenome );
        updateAverageFitness();
        if( newGenome.getFitness() > bestFitness ) {
            bestFitness = newGenome.getFitness();
            leader = newGenome;
        }
    }

    // Updates the average fitness of the species by iterating through all members and calculating.
    private void updateAverageFitness() {
        averageFitness = 0;
        for( Genome genome : speciesMembers ) {
            averageFitness += genome.getFitness();
        }
        averageFitness = averageFitness / speciesMembers.size();
    }

    // Removes all but the leader from the species so that species can be assigned every epoch.
    public void purge() {
        speciesMembers.clear();
        speciesMembers.add( leader );
    }

    // Spawn amount is determine by calculating the sum of how many offspring each member of the species should have.
    // Each member should have their fitness / averageFitness number of children.
    public void calculateSpawnAmount() {
        reproductionRequirements = 0;
        updateAverageFitness();
        for( Genome genome : speciesMembers ) {
            reproductionRequirements += genome.getFitness() / averageFitness;
        }
    }

    // Still need to understand what this exactly is supposed to do.
    public Genome spawn() {
        return new Genome( 5, 1 );
    }


    @Override
    public int compareTo(Species species) {
        // Hacky as fuck. This probably shouldn't work.
        return (int) (this.bestFitness - species.bestFitness );
    }


    // GETTER METHODS
    public Genome getLeader() {
        return leader;
    }
    public ArrayList<Genome> getSpeciesMembers() {
        return speciesMembers;
    }
    public int getSpeciesID() {
        return speciesID;
    }
    public double getBestFitness() {
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
