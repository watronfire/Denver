import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                              //
//  Converts a genome into the specified file format.                                                           //
//                                                                                                              //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class GenomeIO {
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

    public static void writeNNFile( Genome genome ) throws IOException {
        // Ask for a name using a basic scanner. Could sanitize the input if I wanted to, but I don't.
        System.out.println( "Give the genome a name: " );
        Scanner scanner = new Scanner( System.in );
        String fileName = scanner.nextLine();

        // Initiates the ability to write to a file.
        FileWriter fileWriter = new FileWriter( "res/" + fileName );
        PrintWriter printWriter = new PrintWriter( fileWriter );

        // First line will be an asterisk, indicating nodes, and a number which is the number of nodes in the genomes
        printWriter.printf( "*%s\n", genome.getAllNodeGenes().size() );

        // Iterate through each node and print to file. Format is as follows:
        // NodeID, NodeInnovation, NodeType, Activation Response, SplitY
        for( NodeGene ng : genome.getAllNodeGenes() ) {
            printWriter.printf( "%d,%d,%s,%f,%f\n", ng.getNodeID(), ng.getInnovation(), ng.getNodeTypeString(), ng.getActivationResponse(), ng.getSplitY() );
        }

        printWriter.printf( "^%s\n", genome.getAllConnectionGenes().size() );

        // Iterate through each connection and print to file. Format is as follows:
        // InNode, OutNode, Weight, Innovation, Enabled
        for( ConnectionGene cg : genome.getAllConnectionGenes() ) {
            int enabled = cg.isEnabled() ? 1:0;
            printWriter.printf( "%d,%d,%f,%d,%d\n", cg.getInNode(), cg.getOutNode(), cg.getWeight(), cg.getInnovation(), enabled );
        }

        // Close writer and thus save to file.
        printWriter.close();

    }

    public static Genome readNNfile( String fileName ) {

        // ArrayList of genes which will initialize the return genome.
        ArrayList<Gene> genes = new ArrayList<>();
        // Initialize bufferedReader
        BufferedReader br = null;

        try {

            // Open the file specified by parameter.
            br = new BufferedReader( new FileReader( "res/" + fileName ) );

            // Read the first line
            String currentLine = br.readLine();
            String[] cna;

            // First line should contain an * followed by the number of nodes
            if( currentLine.substring( 0, 1 ).equals( "*" ) ) {
                // Parse the number of nodes from the lines
                int numOfNodes = Integer.parseInt( currentLine.substring( 1 ) );
                // For each of the following lines which should contain a node description.
                for( int i = 0; i < numOfNodes; i += 1 ) {

                    // read the next line
                    currentLine = br.readLine();

                    // Split the line based on the comma character
                    cna = currentLine.split("," );

                    // Add a new NodeGene to the genes ArrayList with the nodeGene description specified by the line.
                    genes.add( new NodeGene( Integer.parseInt( cna[0] ), Integer.parseInt( cna[1] ), NodeGene.parseNodeType( cna[2] ), Double.parseDouble( cna[3] ), Double.parseDouble( cna[4] ) ) );

                }
            }

            // Read the line after the nodeGene descriptions.
            currentLine = br.readLine();
            // First line should contain a ^ followed by the number of connections
            if( currentLine.substring( 0,1 ).equals( "^" ) ) {

                // Parse the number of connections from the line.
                int numOfConnections = Integer.parseInt( currentLine.substring( 1 ) );

                // For each connection line
                for( int i = 0; i < numOfConnections; i += 1 ) {

                    // Read the next line
                    currentLine = br.readLine();

                    // Split the current line by the comma character.
                    cna = currentLine.split( "," );

                    // Add a new connectionGene the the genes ArrayList with the connection description on the line.
                    genes.add( new ConnectionGene( Integer.parseInt( cna[0] ), Integer.parseInt( cna[1] ), Double.parseDouble( cna[2] ), Integer.parseInt( cna[3] ), cna[4].equals( "1" ) ) );
                }
            }


        } catch ( IOException e ) {
            e.printStackTrace();
        }


        try {
            if ( br != null ) {
                br.close();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return new Genome( genes );
    }

    public static HashMap<Integer, String> getTypeArray( Genome genome ) {
        HashMap<Integer, String> typeArray = new HashMap<>();
        for( NodeGene ng : genome.getAllNodeGenes() ) {
            typeArray.put( ng.getNodeID(), ng.getNodeTypeString() );
        }
        return typeArray;
    }
}
