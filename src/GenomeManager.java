import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by nate on 4/11/17.
 */

// I need an object which will manage both a pool of genomes. This means declaring speciation and removing bad genomes.
// The program that I'm utilizes calls this the

public class GenomeManager {

    // TODO: Rewrite this method to accommodate getAllGenes().
    // Compares two genomes to determine whether their different species or not
    public static boolean areSpecies(Genome genome1, Genome genome2 ) {

        // So the documentation talks a about disjointed genes and excess genes, but really they both describe genes
        // which aren't present in the other genome. Both these types of genes should have the same penalty
        double difference = 0;
        double weightDisjoint = 2.0;
        double weightDeltaWeight = 0.4;
        double threshold = 1.0;

        // Find penalty incurred by disjointed genes
        difference += determineDisjointed( genome1, genome2 ) * weightDisjoint;
        // Find the penalty incurred by differences in weights.
        difference += determineWeightDelta( genome1, genome2 ) * weightDeltaWeight;

        // If difference is less than a threshold, then the two input genomes are of the same species.
        return difference < threshold;

    }

    // Determines the difference between two genomes based on the number of genes they share/differ
    private static double determineDisjointed( Genome genome1, Genome genome2 )     {
        // Create array of innovation numbers for genome 1
        ArrayList<Integer> innovationG1 = new ArrayList<>();
        for( int i = 0; i < genome1.getSize(); i += 1 ) {
            innovationG1.add( genome1.getConnectionGene( i, true ).getInnovation() );
        }

        // Create array of innovation numbers for genome 2
        ArrayList<Integer> innovationG2 = new ArrayList<>();
        for( int i = 0; i < genome2.getSize(); i += 1 ) {
            innovationG2.add( genome2.getConnectionGene( i, true ).getInnovation() );
        }

        // Determine innovation numbers which genome1 contains but genome2 doesn't
        int disjointedGenes = 0;
        for( int i : innovationG1 ) {
            if( !innovationG2.contains( i ) ) {
                disjointedGenes += 1;
            }
        }

        // Determines innovation numbers which genome2 contains but genome1 doesn't.
        for ( int i : innovationG2 ) {
            if( !innovationG1.contains( i ) ) {
                disjointedGenes += 1;
            }
        }

        // Returns the number of disjointed genes normalized by the number of genes in each genome.
        return disjointedGenes / Math.max( innovationG1.size(), innovationG2.size() );
    }
    // Determines the difference between two genomes in terms of the difference between weights of the same genes
    private static double determineWeightDelta( Genome genome1, Genome genome2 ) {

        // Create an array of all the innovation numbers of genome1. All genes which are in common between the two
        // will, by definition, be found in genome1.
        ArrayList<Integer> innovationG1 = new ArrayList<>();
        for( int i = 0; i < genome2.getSize(); i += 1 ) {
            innovationG1.add( genome1.getConnectionGene( i, true ).getInnovation() );
        }

        // Coincidence is the number of genes shared by the two genomes, and sum will hold the difference in weights
        // between the two genomes.
        int coincidence = 0;
        double sum = 0;

        // Iterate through the innovation numbers in genome1
        for( int innovation : innovationG1 ) {
            // Determine if the innovation number is found in genome2.
            if( genome2.getConnectionGene( innovation, false ) != null ) {
                // Sum is increased by the absolute difference between the weights of the homologous genes.
                sum += Math.abs( genome1.getConnectionGene(innovation, false).getWeight() - genome2.getConnectionGene(innovation, false).getWeight() );
                coincidence += 1;
            }
        }

        // Return the sum normalized by the number of homologous genes.
        if( coincidence != 0 ) {
            return sum / coincidence;
        } else {
            return 0;
        }
    }

    // Given two genomes, returns a new genome made up the crossover of the two input genomes.
    public static Genome crossover( Genome genome1, Genome genome2 ) {

        ArrayList<Gene> childGeneList = new ArrayList<>();

        // Create a HashMap of all genes from genome1 and their innovation numbers.
        HashMap<Integer, Gene> parent1 = new HashMap<>();
        for( Gene g : genome1.getAllGenes() ) {
            parent1.put( g.getInnovation(), g );
        }
        HashMap<Integer, Gene> parent2 = new HashMap<>();
        for( Gene g : genome2.getAllGenes() ) {
            parent2.put( g.getInnovation(), g );
        }

        // Creates an ArrayList of innovation numbers of both genomes, with no duplicates.
        ArrayList<Integer> innovationTotal = GenomeManager.getSimilarInnovation( genome1, genome2 );

        // Determines which genome has the greater fitness
        // If they have the same fitness than genes will be randomly selected from both.
        if( genome1.getFitness() == genome2.getFitness() ) {

            Random random = new Random();

            // Iterates through the innovationTotal list.
            for( int i : innovationTotal ) {

                // For each innovation number, determine is both or one of the genomes contains the gene.
                if( parent1.containsKey( i ) && parent2.containsKey( i ) ) {
                    if( random.nextBoolean() ) {
                        childGeneList.add( parent1.get( i ) );
                    } else {
                        childGeneList.add( parent2.get( i ) );
                    }

                } else if ( parent1.containsKey( i ) ) {
                    childGeneList.add( parent1.get( i ) );

                } else {
                    childGeneList.add( parent2.get( i ) );
                }
            }

        // If genome1 fitness is greater, then it's genes will be added to the childGenome instead of genome2
        } else if( genome1.getFitness() > genome2.getFitness() ) {
            for( int i : innovationTotal ) {
                if( parent1.containsKey( i ) ) {
                    childGeneList.add( parent1.get( i ) );
                } else {
                    childGeneList.add( parent2.get( i ) );
                }
            }

        // The opposite of the above situation.
        } else {
            for( int i : innovationTotal ) {
                if( parent2.containsKey( i ) ) {
                    childGeneList.add( parent2.get( i ) );
                } else {
                    childGeneList.add( parent1.get( i ) );
                }
            }
        }


        return new Genome( childGeneList );
    }

    private static ArrayList<Integer> getSimilarInnovation( Genome genome1, Genome genome2 ) {
        ArrayList<Integer> solution = new ArrayList<>();

        for( Gene g : genome1.getAllGenes() ) {
            solution.add( g.getInnovation() );
        }
        // Add innovation numbers from genome2 which are not already in the ArrayList.
        for( Gene g : genome2.getAllGenes() ) {
            if( !solution.contains( g.getInnovation() ) ) {
                solution.add( g.getInnovation() );
            }
        }

        return solution;
    }

    // Calculates how many offspring each member of the population should spawn. Equal to the genomes
    // fitness divided by the average fitness of the entire genome population.
    public static void assignSpawnRequirements( ArrayList<Genome> genomePool ) {
        double totalFitness = 0;
        double totalAverageFitness = 0;
        for( Genome genome : genomePool ) {
            totalFitness += genome.getFitness();
        }
        totalAverageFitness = totalFitness / genomePool.size();

        for( Genome genome : genomePool ) {
            genome.setSpawnAmount( genome.getFitness() / totalAverageFitness );
        }
    }

    // Speciation function. Separates individual genomes into their respective species by calculating a
    // compatibility score with every other member of the population.
    public static void speciate( ArrayList<Genome> genomePool, ArrayList<Species> speciesPool ) {
        boolean added = false;

        // Iterate through each genome and speciate
        for( Genome genome: genomePool ) {
            // Determine whether a genome is the same species as each species leader. If it finds a
            // compatible species, then add it to the species. If no suitabe species if found, then
            // create a new species.
            for( Species species: speciesPool ) {
                if( areSpecies( genome, species.getLeader() ) ) {
                    species.addMember( genome );
                    added = true;
                    return;
                }
            }

            // If no compatible species is found is really the only
            speciesPool.add( new Species( genome ) );
        }
    }

}
