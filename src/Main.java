import java.util.ArrayList;

public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
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
                genome.calculateFitness( tests );
            }
            genomePool = GenomeManager.epoch( genomePool, speciesPool );

        }
    }
}
