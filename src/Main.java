import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    // TODO: Bias node needs to be incorporated

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: The best performing genomes are those without hidden nodes. Because hidden nodes are required to
    // successfully evaluate XOR, the best anything can do is a fitness of 3.0 because that would be a linear division.
    // Something is happening so that increasing nodes is not helping. Also, connections shouldn't be allow from input
    // to bias. Need to rewrite the mutateConnetion() function so that connections cannot terminate in the bias.

    public static void main ( String[]args ){

        if( false ) {

            new Visualizer();

        } else {


            // Must initialize the SplitY:Depth lookup table
            GenomeManager.split( 0, 1, 0 );
            // Hacky
            GenomeManager.splitAddendum();

            ArrayList<Genome> genomePool = new ArrayList<>();
            ArrayList<Species> speciesPool = new ArrayList<>();
            XORExample[] tests = { new XORExample( false, false ),
                    new XORExample( false, true ),
                    new XORExample( true, false ),
                    new XORExample( true, true ) };

            int[] correctRatios = { 0, 0, 0, 0 };

            int count = 0;

            // Generate the genomePool
            for ( int i = 0; i < Parameters.populationSize; i++ ) {
                genomePool.add( new Genome( 2, 1 ) );
            }


            while ( count < 1000 ) {


                // Generate the XORTests for this epoch


                // Calculate the finesses.
                for ( Genome genome : genomePool ) {
                    // One day maybe...
                    // genome.cullConnections();
                    GenomeManager.calculateNetDepth( genome );
                    genome.createPhenotype();
                    int[] tmpAnswers = genome.calculateFitness( tests );

                    for ( int i = 0; i < tmpAnswers.length; i += 1 ) {
                        correctRatios[i] += tmpAnswers[i];
                    }
                }
                genomePool = GenomeManager.epoch( genomePool, speciesPool );

                for ( Species species : speciesPool ) {
                    species.incrementAge();
                }

                count += 1;

            }

            for ( int i : correctRatios ) {
                System.out.println( i );
            }

            System.out.println();
            GenomeManager.getBestGenome().reportNodes();
            GenomeManager.getBestGenome().reportConnections();

        }
    }
}