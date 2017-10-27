import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static void main ( String[]args ){


        if( false ) {
            DataParser dp = new DataParser( "res/spineEdited.csv" );
            dp.getRandomPatient();
            dp.reportSymptoms();

        } else {

            Genome successfulGenome = null;

            // Must initialize the SplitY:Depth lookup table
            GenomeManager.split( 0, 1, 0 );
            // Hacky
            GenomeManager.splitAddendum();

            // Initialize genome and species pool.
            ArrayList<Genome> genomePool = new ArrayList<>();
            ArrayList<Species> speciesPool = new ArrayList<>();

            // Get data from file
            DataParser dp = new DataParser( "res/spineEdited.csv" );

            // Create initial training set.
            PatientData[] trainingSet = new PatientData[155];
            PatientData[] testSet = new PatientData[155];

            // Generate training and test set.
            for( int i = 0; i < trainingSet.length; i += 1 ) {
                trainingSet[i] = dp.getRandomPatient();
                testSet[i] = dp.getRandomPatient();
            }

            // Generation counter.
            int count = 0;

            // Generate the genomePool
            for ( int i = 0; i < Parameters.populationSize; i++ ) {
                genomePool.add( new Genome( 12, 1 ) );
            }

            // Enter genetic algorithm loop
            while ( count < 100 ) {

                // Calculate the finesses.
                for ( Genome genome : genomePool ) {
                    // One day maybe...
                    // genome.cullConnections();
                    GenomeManager.calculateNetDepth( genome );
                    genome.createPhenotype();
                    if( genome.calculateFitness( trainingSet, true ) ) {
                        successfulGenome = genome;

                        if( successfulGenome.calculateFitness( testSet, false ) ) {
                            count = 99999999;
                            break;
                        }

                    }

                }
                genomePool = GenomeManager.epoch( genomePool, speciesPool );

                for ( Species species : speciesPool ) {
                    species.incrementAge();
                }

                count += 1;

            }

            System.out.println();

            if( successfulGenome == null ) {
                GenomeManager.getBestGenome().reportNodes();
                GenomeManager.getBestGenome().reportConnections();
            } else {
                successfulGenome.reportNodes();
                successfulGenome.reportConnections();

                try {
                    GenomeOutputer.writeNETFile( successfulGenome );
                } catch ( IOException e ) {
                    System.err.println( e.getMessage() );
                    System.exit( 839214 );
                }

                Visualizer vis = new Visualizer();
                vis.Display( "res/output.net" );
                vis.setNodeColors( GenomeOutputer.getTypeArray( successfulGenome ) );

            }



        }
    }




}