import java.io.IOException;
import java.util.ArrayList;

public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static void main( String[] args ) {
        // Must initialize the SplitY:Depth lookup table
        GenomeManager.split( 0, 1, 0 );
        // Hacky
        GenomeManager.splitAddendum();

        // Initialize genome and species pool.
        ArrayList<Genome> genomePool = new ArrayList<>();
        ArrayList<Species> speciesPool = new ArrayList<>();

        // Get data from file
        DataParser dp = new DataParser( "res/MLS-Data-current.csv" );

        // Create initial training set. Smaller is apparently better.
        MLSGame[] trainingSet = new MLSGame[ Parameters.trainingSetSize ];

        // Generate training and test set.

        // Generation counter.
        int count = 0;

        // Generate the genomePool
        for ( int i = 0; i < Parameters.populationSize; i++ ) {
            genomePool.add( new Genome( 13, 1 ) );
        }

        // Enter genetic algorithm loop. Number is essentially number of generations to let algortihm run for.
        // For this spine problem. 2000-3000 seems to be about right.
        while ( count < 2500 ) {
            // Create random training set.
            for ( int i = 0; i < trainingSet.length; i += 1 ) {
                trainingSet[i] = dp.getRandomMLSGame();
            }

            // Calculate the finesses.
            for ( Genome genome : genomePool ) {
                // One day maybe...
                // genome.cullConnections();
                GenomeManager.calculateNetDepth( genome );
                genome.createPhenotype();

                genome.calculateFitness( trainingSet );

            }


            genomePool = GenomeManager.epoch( genomePool, speciesPool );

            for ( Species species : speciesPool ) {
                species.incrementAge();
            }

            count += 1;

        }

        //System.out.println();
        //GenomeManager.getBestGenome().reportNodes();
        //GenomeManager.getBestGenome().reportConnections();
        //try {
        //    GenomeIO.writeNETFile( GenomeManager.getBestGenome() );
        //    GenomeIO.writeNNFile( GenomeManager.getBestGenome() );
        //} catch ( IOException e ) {
        //    System.err.println( e.getMessage() );
        //    System.exit( 839214 );
        //}
        //Visualizer vis = new Visualizer();
        //vis.Display( "res/output.net" );
        //vis.setNodeColors( GenomeIO.getTypeArray( GenomeManager.getBestGenome() ) );

        System.out.println();
        count = 0;
        for ( MLSGame game : dp.getData() ) {

            ArrayList<Double> results = GenomeManager.getBestGenome().getPhenotype().update( game.getMetrics(), NeuralNet.runtype.SNAPSHOT );
            String resultString = "";
            String expectedResult = "";

            if( results.get( 0 ) > 1.33 ) {
                resultString = "A";
            } else if( results.get( 0 ) < 0.66 ) {
                resultString = "H";
            } else {
                resultString = "T";
            }

            switch( game.getOutcomes()[0] ) {
                case 0: expectedResult = "H";
                    break;
                case 1: expectedResult = "T";
                    break;
                case 2: expectedResult = "A";
                    break;
            }

            if( resultString == expectedResult ) {
                count += 1;
            }
        }

        System.out.println( count );
    }
}
