import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by nate on 4/11/17.
 */

// I need an object which will manage both a pool of genomes. This means declaring speciation and removing bad genomes.
// The program that I'm utilizes calls this the

public class GenomeManager {

    private static double totalAverageFitness;
    private static double totalFitness;
    private static double bestFitnessEver;
    private static double bestFitnessThisGeneration;
    // Static HashMap where the integer value is the depth associated with a double key representing the SplitY.
    private static HashMap<Double, Integer> splits = new HashMap<>();

    // This functions ties everything together. Performs one epoch of the genetic algorithm and returns a
    // an ArrayList of new genomes.
    public static ArrayList<Genome> epoch( ArrayList<Genome> genomePool, ArrayList<Species> speciesPool ) {
        // Resets appropriate values and kills off poorly performing species.
        resetAndKill( speciesPool );

        //sortAndRecord();

        // Speciates the genome population and also updates each species spawn amounts.
        speciate( genomePool, speciesPool );

        // ArrayList which will hold the new population of genomes.
        ArrayList<Genome> newPopulation = new ArrayList<>();

        int numSpawnedSoFar = 0;

        Genome temp;

        // Iterate through each species and spawn new individuals for the next generation.
        for( Species species : speciesPool ) {
            // Because spawn amounts are a double cast to an int, there could be overflow, this prevents that.
            if( numSpawnedSoFar < Parameters.populationSize ) {


                boolean bestAdded = false;
                // This is the number of offspring this species is required to spawn.
                int numberToSpawn = (int) Math.round( species.getReproductionRequirements() );
                // While there are still offspring to spawn
                for( int i = 0; i <= numberToSpawn; i++ ) {
                    // First grab the best performing species from each genome and add it to the new population without
                    // mutation.
                    if( !bestAdded ) {
                        temp = new Genome( species.getLeader().getAllGenes() );
                        bestAdded = true;

                    // Else grab other things.
                    } else {
                        // If the species only has one member than it can only be advanced with mutations.
                        if( species.getNumberOfMembers() == 1 ) {
                            temp = new Genome( species.spawn().getAllGenes() );

                        // if greater than one then we can use crossover.
                        } else {
                            Genome g1 = species.spawn();
                            temp = g1;
                            if( Math.random() < Parameters.crossoverRate ) {

                                // Have to make sure that it's not the same
                                Genome g2 = species.spawn();
                                int numOfAttempts = 5;

                                while( ( g1 == g2 ) && ( numOfAttempts > 0) ) {
                                    g2 = species.spawn();
                                    numOfAttempts -= 1;
                                }

                                if( g1 != g2 ) {
                                    temp = crossover( g1, g2 );
                                }
                            } else {
                                temp = new Genome( g1.getAllGenes() );
                            }
                        }

                        // Alright, so here we mutate everything by chance.
                        if( Math.random() < Parameters.chanceMutateNode ) {
                            temp.mutateNode();
                        }
                        if( Math.random() < Parameters.chanceMutateLink ) {
                            temp.mutateLink();
                        }
                        if( Math.random() < Parameters.chanceMutateEnable ) {
                            temp.mutateEnable();
                        }
                        if( Math.random() < Parameters.chanceMutateWeight ) {
                            temp.mutatePoint();
                        }
                        if( Math.random() < Parameters.chanceMutateThreshold ) {
                            temp.mutateThreshold();
                        }
                        if( Math.random() < Parameters.mutationRate ) {
                            temp.mutateActivationResponse();
                        }
                    }

                    newPopulation.add( temp );

                    numSpawnedSoFar += 1;

                    if( numSpawnedSoFar == Parameters.populationSize ) {
                        numberToSpawn = 0;
                    }
                }
            }
        }

        return newPopulation;

    }

    // Searches the lookup table (i.e. HashMap) and sets the maximum depth found in a network
    public static void calculateNetDepth( Genome genome )   {
        int maxSoFar = 0;

        // Determine the maximum depth.
        for( NodeGene ng : genome.getAllNodeGenes() ) {

            try {
                if ((ng.getSplitY() != 0.0) && splits.get(ng.getSplitY()) > maxSoFar) {
                    maxSoFar = splits.get(ng.getSplitY());
                }
            } catch ( NullPointerException n ) {

                // TODO: Sometimes this is thrown because a neural net becomes too deep. So at some point I need to limit nodes.
                // For now, we're just going to ignore those nets and hope they're removed.

                System.out.println( "SplitY: " + ng.getSplitY() );
                System.out.println( "MaxSoFar: " + maxSoFar );
            }
        }

        // Super hacky, might as well put this as maxSoFar * 2.
        // TODO: figure out if this can be improved.
        genome.setDepth( maxSoFar + 2 );

    }

    // Creates a lookup table that is used to calculate the depth of a network.
    public static void split( double low, double high, int depth ) {
        double span = high - low;

        splits.put( low + span / 2.0, depth + 1);

        if( !( depth > 6 ) ) {
            split( low, low + span / 2, depth + 1 );
            split( low + span / 2, high, depth + 1 );
        }
    }

    // Hacky because I don't know how to do this in the actual split() method. Need to add this value.
    public static void splitAddendum() {
        splits.put( 1.0, 1 );
    }

    // TODO: Rewrite this method to accommodate getAllGenes().
    // Compares two genomes to determine whether their different species or not
    private static boolean areSpecies(Genome genome1, Genome genome2 ) {

        // So the documentation talks a about disjointed genes and excess genes, but really they both describe genes
        // which aren't present in the other genome. Both these types of genes should have the same penalty
        double difference = 0;
        double weightDisjoint = 2.0;
        double weightDeltaWeight = 0.4;


        // Find penalty incurred by disjointed genes
        difference += determineDisjointed( genome1, genome2 ) * weightDisjoint;
        // Find the penalty incurred by differences in weights.
        difference += determineWeightDelta( genome1, genome2 ) * weightDeltaWeight;

        // If difference is less than a threshold, then the two input genomes are of the same species.
        return difference <= Parameters.compatibilityThreshold;

    }

    // Determines the difference between two genomes based on the number of genes they share/differ
    private static double determineDisjointed( Genome genome1, Genome genome2 )     {
        // Create array of innovation numbers for genome 1
        ArrayList<Integer> innovationG1 = new ArrayList<>();
        for( Gene gene1 : genome1.getAllGenes() ) {
            innovationG1.add( gene1.getInnovation() );
        }

        // Create array of innovation numbers for genome 2
        ArrayList<Integer> innovationG2 = new ArrayList<>();
        for( Gene gene2 : genome2.getAllGenes() ) {
            innovationG2.add( gene2.getInnovation() );
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
        HashMap<Integer, ConnectionGene> innovationG1 = new HashMap<>();
        for( ConnectionGene cg : genome1.getAllConnectionGenes() ) {
            innovationG1.put( cg.getInnovation(), cg );
        }

        // Coincidence is the number of genes shared by the two genomes, and sum will hold the difference in weights
        // between the two genomes.
        int coincidence = 0;
        double sum = 0;

        // Iterate through the innovation numbers in genome1
        for( ConnectionGene cg: genome2.getAllConnectionGenes() ) {
            // Determine if the innovation number is found in genome2.
            if( innovationG1.containsKey( cg.getInnovation() ) ) {
                // Sum is increased by the absolute difference between the weights of the homologous genes.
                sum += Math.abs( innovationG1.get( cg.getInnovation() ).getWeight() - cg.getWeight() );
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
    // TODO: Figure out why sometimes, additional input nodes are created.
    private static Genome crossover( Genome genome1, Genome genome2 ) {

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

        cleanupGenome( childGeneList );

        return new Genome( childGeneList );
    }

    // Really just removes repetitive innovation numbers.
    private static void cleanupGenome( ArrayList<Gene> geneList ) {
        ArrayList<Integer> innovationNums = new ArrayList<>();
        Iterator<Gene> i = geneList.iterator();

        while( i.hasNext() ) {
            Gene g = i.next();

            if( innovationNums.contains( g.getInnovation() ) ) {
                i.remove();
            } else {
                innovationNums.add( g.getInnovation() );
            }

        }
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
    // fitness divided by the average fitness of the entire genome population. Also updates totalAverageFitness and
    // bestFitness ever.
    private static void assignSpawnRequirements( ArrayList<Genome> genomePool ) {
        totalFitness = 0;
        totalAverageFitness = 0;
        bestFitnessThisGeneration = 0;
        for( Genome genome : genomePool ) {
            totalFitness += genome.getFitness();
            if( genome.getFitness() > bestFitnessEver ) {
                bestFitnessEver = genome.getFitness();
            }
            if( genome.getFitness() > bestFitnessThisGeneration ) {
                bestFitnessThisGeneration = genome.getFitness();
            }
        }
        totalAverageFitness = totalFitness / genomePool.size();
        //System.out.println( "Average Fitness: " + totalAverageFitness + " | Best Fitness: " + bestFitnessThisGeneration );
        System.out.println( totalAverageFitness + "," + bestFitnessThisGeneration );

        for( Genome genome : genomePool ) {
            genome.setSpawnAmount( genome.getFitness() / totalAverageFitness);
        }
    }

    // Speciation function. Separates individual genomes into their respective species by calculating a
    // compatibility score with every other member of the population.
    private static void speciate( ArrayList<Genome> genomePool, ArrayList<Species> speciesPool ) {

        // Iterate through each genome and speciate
        for( Genome genome: genomePool ) {
            // Determine whether a genome is the same species as each species leader. If it finds a
            // compatible species, then add it to the species. If no suitabe species if found, then
            // create a new species.
            boolean added = false;
            for( Species species: speciesPool ) {
                if( areSpecies( genome, species.getLeader() ) ) {
                    species.addMember( genome );
                    added = true;
                    break;
                }
            }

            // If no compatible species is found is really the only
            if( !added ) {
                speciesPool.add(new Species(genome));
            }
        }

        // Probably should update spawn requirements now.
        assignSpawnRequirements( genomePool );
        for( Species species : speciesPool ) {
            species.calculateSpawnAmount();
        }
    }

    // Method resets some values for the next generation and kills off any poorly performing species.
    private static void resetAndKill( ArrayList<Species> speciesPool ) {
        totalFitness = 0;
        totalAverageFitness = 0;

        // I don't understand anything below here. Not sure why I can't remove when I iterate through a list
        // but I can remove an object in an iterator.
        ArrayList<Species> toRemove = new ArrayList<>();
        for( Species species : speciesPool ) {

            // Purges every species
            species.purge();

            // Deletes species if its not improving and if it doesn't have the best fitness.
            if( ( species.getAge() > Parameters.generationsAllowedNoImprovement) && ( species.getBestFitness() < bestFitnessEver ) ) {
                toRemove.add( species );
            }
        }

        speciesPool.removeAll( toRemove );
    }

    public static int determineHiddenNodes( ArrayList<NodeGene> nodeGenes ) {
        int output = 0;
        for( NodeGene ng : nodeGenes ) {
            if( ng.getNodeType() == 1 ) {
                output += 1;
            }
        }
        return output;
    }
}
