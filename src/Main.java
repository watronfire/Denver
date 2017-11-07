import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static void main( String[] args ) {
        if( true ) {
            Genome gen = new Genome( 6, 1 );
            //gen.reportNodes();
            gen.reportConnections();

            try {
                GenomeIO.writeNNFile( gen );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            System.out.println( "Genome to open: " );
            Scanner scanner = new Scanner( System.in );
            String genomeName = scanner.nextLine();

            Genome newGen = GenomeIO.readNNfile( genomeName );
            //newGen.reportNodes();
            newGen.reportConnections();


        } else {
            // Must initialize the SplitY:Depth lookup table
            GenomeManager.split( 0, 1, 0 );
            // Hacky
            GenomeManager.splitAddendum();

            // Initialize genome and species pool.
            ArrayList<Genome> genomePool = new ArrayList<>();
            ArrayList<Species> speciesPool = new ArrayList<>();

            // Get data from file
            DataParser dp = new DataParser( "res/spineEdited.csv" );

            // Create initial training set. Smaller is apparently better.
            PatientData[] trainingSet = new PatientData[20];

            // Generate training and test set.

            // Generation counter.
            int count = 0;

            // Generate the genomePool
            for ( int i = 0; i < Parameters.populationSize; i++ ) {
                genomePool.add( new Genome( 12, 1 ) );
            }

            // Enter genetic algorithm loop. Number is essentially number of generations to let algortihm run for.
            // For this spine problem. 2000-3000 seems to be about right.
            while ( count < 2500 ) {
                // Create random training set.
                for ( int i = 0; i < trainingSet.length; i += 1 ) {
                    trainingSet[i] = dp.getRandomPatient();
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

            System.out.println();
            GenomeManager.getBestGenome().reportNodes();
            GenomeManager.getBestGenome().reportConnections();
            try {
                GenomeIO.writeNETFile( GenomeManager.getBestGenome() );
                GenomeIO.writeNNFile( GenomeManager.getBestGenome() );
            } catch ( IOException e ) {
                System.err.println( e.getMessage() );
                System.exit( 839214 );
            }
            Visualizer vis = new Visualizer();
            vis.Display( "res/output.net" );
            vis.setNodeColors( GenomeIO.getTypeArray( GenomeManager.getBestGenome() ) );

            System.out.println();
            for ( PatientData pd : dp.getData() ) {

                boolean result = GenomeManager.getBestGenome().getPhenotype().update( pd.getSymptoms(), NeuralNet.runtype.SNAPSHOT ) > 0.5;
                System.out.println( pd.getOutcome() + "," + ( result ? 1 : 0 ) );
            }
        }
    }
}
