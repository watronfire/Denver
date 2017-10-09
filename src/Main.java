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


    public static void main( String[] args ) {

        if( false ) {
            GenomeManager.split( 0, 1, 0 );
            GenomeManager.splitAddendum();
            Genome genome = new Genome( 2, 1 );
            genome.reportNodes();
            genome.reportConnections();
            GenomeManager.calculateNetDepth( genome );
            System.out.println();
            System.out.println( "Genome Depth: " + genome.getDepth() );
            genome.createPhenotype();
            XORExample xor = new XORExample();
            System.out.println( "Inputs: " + xor.getInputs()[0] + ", " + xor.getInputs()[1] + " | Output: " + xor.getOutput() );
            genome.calculateFitness( xor );
        } else {
            // Must initialize the SplitY:Depth lookup table
            GenomeManager.split( 0, 1, 0 );
            // Hacky
            GenomeManager.splitAddendum();

            ArrayList<Genome> genomePool = new ArrayList<>();
            ArrayList<Species> speciesPool = new ArrayList<>();
            XORExample[] tests = new XORExample[ 100 ];
            for ( int i = 0; i < tests.length; i += 1 ) {
                tests[i] = new XORExample();

            }

            // Generate the genomePool
            for( int i = 0; i < Parameters.populationSize; i++ ) {
                genomePool.add( new Genome( 2, 1 ) );
            }


            while( true ) {


                // Generate the XORTests for this epoch


                // Calculate the finesses.
                for ( Genome genome : genomePool ) {
                    // One day maybe...
                    // genome.cullConnections();
                    GenomeManager.calculateNetDepth( genome );
                    genome.createPhenotype();
                    genome.calculateFitness( tests );
                }
                genomePool = GenomeManager.epoch( genomePool, speciesPool );

                for( Species species : speciesPool ) {
                    species.incrementAge();
                }

            }
        }

    }

}