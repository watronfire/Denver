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
        this.speciesID = speciesID;
        bestFitness = leader.getFitness();
        averageFitness = leader.getFitness();
        timeSinceImprovement = 0;
        age = 0;

    }

    // This method boosts the fitnesses of the younger genomes, penalizes the fitnesses of the older
    // genomes, and then performs fitness sharing over all members of the species.
    public void adjustFitnesses() {
        double total = 0;
        for( Genome genome: speciesMembers ) {

        }
    }

    public void addMember( Genome newGenome ) {
        speciesMembers.add( newGenome );
        updateAverageFitness();
        if( newGenome.getFitness() > bestFitness ) {
            bestFitness = newGenome.getFitness();
        }
    }

    private void updateAverageFitness() {
        averageFitness = 0;
        for( Genome genome : speciesMembers ) {
            averageFitness += genome.getFitness();
        }
        averageFitness = averageFitness / speciesMembers.size();
    }

    public void purge() {}

    public void calculateSpawnAmount() {}

    public Genome spawn() {
        return new Genome( 5, 1 );
    }



    @Override
    public int compareTo(Species species) {
        // Hacky as fuck. This probably shouldn't work.
        return (int) (this.bestFitness - species.bestFitness );
    }
}
