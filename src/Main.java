import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    // Static hashmap where the value is the depth associated with a double value representing the SplitY

    public static void main(String[] args) {

        // Must initialize the SplitY:Depth loopup table
        GenomeManager.split( 0, 1, 0 );
        // Hacky
        GenomeManager.splitAddendum();


        ArrayList<Genome> genomePool = new ArrayList<>();
        ArrayList<Species> speciesPool = new ArrayList<>();
        XORExample[] tests = new XORExample[ 50 ];

        // Generate the genomePool
        for( int i = 0; i < Parameters.populationSize; i++ ) {
            genomePool.add( new Genome( 2, 1 ) );
        }

        // Generate the initial XORTests.
        for (int i = 0; i < tests.length; i += 1) {
            tests[i] = new XORExample();
        }

        while( true ) {


            // Calculate the finesses.
            for ( Genome genome : genomePool ) {
                GenomeManager.calculateNetDepth( genome );
                genome.createPhenotype();
                genome.calculateFitness( tests );
            }
            genomePool = GenomeManager.epoch( genomePool, speciesPool );

        }
    }

}
