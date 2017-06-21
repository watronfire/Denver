import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nate on 4/11/17.
 */

// I need an object which will manage both a pool of genomes. This means declaring speciation and removing bad genomes.

public class GenomeManager {

    // TODO: Create speciation function
    public void speciation( Genome[] genomes ) {
        for( int i = 0; i < genomes.length - 1; i += 1 ) {

        }
    }

    // Compares two genomes to determine whether their different species or not
    public boolean compareGenomes( Genome genome1, Genome genome2 ) {

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

        // If difference is greater than a threshold, then the two input genomes are different species.
        System.out.println( "Difference: " + difference );
        return difference > threshold;

    }

    // Determines the difference between two genomes based on the number of genes they share/differ
    public double determineDisjointed( Genome genome1, Genome genome2 )     {
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
        System.out.println( "DisjointedGenes: " + disjointedGenes );
        return disjointedGenes / Math.max( innovationG1.size(), innovationG2.size() );
    }
    // Determines the difference between two genomes in terms of the difference between weights of the same genes
    public double determineWeightDelta( Genome genome1, Genome genome2 ) {

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

    // TODO: Finish the crossover function
    // Given two genomes, returns a new genome made up the crossover of the two input genomes.
    public Genome crossover( Genome genome1, Genome genome2 ) {

        Genome childGenome = new Genome();

        // Creates an ArrayList of innovation numbers of both genomes, with no duplicates.
        ArrayList<Integer> innovationTotal = new ArrayList<>();
        // Add all the innovation numbers from genome1
        for( int i = 0; i < genome1.getSize(); i += 1 ) {
            innovationTotal.add( genome1.getConnectionGene( i, true ).getInnovation() );
        }
        // Add innovation numbers from genome2 which are not already in the ArrayList.
        for( int i = 0; i < genome2.getSize(); i += 1 ) {
            if( !innovationTotal.contains( genome2.getConnectionGene( i, true ).getInnovation() ) ) {
                innovationTotal.add( genome2.getConnectionGene( i, true ).getInnovation() );
            }
        }


        boolean genome1contains = false;
        boolean genome2contains = false;

        // Determines which genome has the greater fitness
        // If they have the same fitness than genes will be randomly selected from both.
        if( genome1.getFitness() == genome2.getFitness() ) {

            Random random = new Random();

            // Iterates through the innovationTotal list.
            for( int i : innovationTotal ) {
                genome1contains = false;
                genome2contains = false;

                genome1contains = genome1.containsConnectionGene( i );
                genome2contains = genome2.containsConnectionGene( i );

                // For each innovation number, determine is both or one of the genomes contains the gene.
                if( genome1contains && genome2contains ) {
                    if( random.nextBoolean() ) {
                        childGenome.addConnectionGene( genome1.getConnectionGene( i, false ) );
                    } else {
                        childGenome.addConnectionGene( genome2.getConnectionGene( i, false ) );
                    }

                } else if ( genome1contains ) {
                    childGenome.addConnectionGene( genome1.getConnectionGene( i, false ) );

                } else {
                    childGenome.addConnectionGene( genome2.getConnectionGene( i, false ) );
                }
            }

        // If genome1 fitness is greater, then it's genes will be added to the childGenome instead of genome2
        } else if( genome1.getFitness() > genome2.getFitness() ) {
            for( int i : innovationTotal ) {
                if( genome1.containsConnectionGene( i ) ) {
                    childGenome.addConnectionGene( genome1.getConnectionGene( i, false ) );
                } else {
                    childGenome.addConnectionGene( genome2.getConnectionGene( i, false ) );
                }
            }

        // The opposite of the above situation.
        } else {
            for( int i : innovationTotal ) {
                if( genome2.containsConnectionGene( i ) ) {
                    childGenome.addConnectionGene( genome2.getConnectionGene( i, false ) );
                } else {
                    childGenome.addConnectionGene( genome1.getConnectionGene( i, false ) );
                }
            }
        }

        // Add the necessary nodes. Really all nodes present in the parents.
        for( NodeGene ng: genome1.getNodeGenes() ) {
            childGenome.addNodeGene( ng );
        }
        for( NodeGene ng : genome2.getNodeGenes() ) {
            if( !childGenome.containsNodeGene( ng.getNodeID() ) ) {
                childGenome.addNodeGene( ng );
            }
        }

        return childGenome;
    }

}
