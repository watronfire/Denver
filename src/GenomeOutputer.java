

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                              //
//  Converts a genome into the specified file format.
class GenomeOutputer {
    public static void writeNETFile( Genome genome) throws IOException {
        FileWriter fileWriter = new FileWriter( "res/output.net" );
        PrintWriter printWriter = new PrintWriter( fileWriter );

        printWriter.printf( "*Vertices %d \n", genome.getAllNodeGenes().size() );

        for( NodeGene ng : genome.getAllNodeGenes() ) {
            printWriter.printf( "%d \"%d\" \n", ng.getNodeID(), ng.getNodeID() );
        }

        printWriter.print( "\n*arcs\n" );

        for( ConnectionGene cg : genome.getAllConnectionGenes() ) {
            if( cg.isEnabled() ) {
                printWriter.printf( "%d %d %f \n", cg.getInNode(), cg.getOutNode(), cg.getWeight() );
            }
        }
        printWriter.close();
    }

    public static HashMap<Integer, String> getTypeArray( Genome genome ) {
        HashMap<Integer, String> typeArray = new HashMap<>();
        for( NodeGene ng : genome.getAllNodeGenes() ) {
            typeArray.put( ng.getNodeID(), ng.getNodeTypeString() );
        }
        return typeArray;
    }
}
