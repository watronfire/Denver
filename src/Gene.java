import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nate on 6/20/17.
 */

// Both types of genes, both connections and nodes, require identification globally. Mostly for crossover.
// It's possible that there may be other uses to this.
abstract class Gene implements Comparable<Gene> {

    int innovation;

    // List of all innovation numbers association with a particular topological feature. The key is a list containing
    // the inNode and the outNode, and the value is the innovation number.
    private static HashMap<List<Integer>, Integer> connectionInnovations = new HashMap<>();

    // List of all innovation numbers associated with a particular node feature. The key is a list containing the NodeID
    // and the NodeType, and the value is the innovation number.
    private static HashMap<List<Integer>, Integer> nodeInnovations = new HashMap<>();

    // TODO: Create innovation array, to keep track of all structural changes
    private static int globalInnovation = 1;

    // To get an innovation number, a gene must be analyzed. If the gene is a topological feature which already exists/been
    // evolved, then it is given the innovation number associated with that given topological feature.
    public static int getGlobalInnovation( Gene gene ) {

        // If the gene is a connection, then the inNode and outNode are description enough of a topological feature.
        // These are collected from the input gene and checked against the ConnectionInnovations already present. If it
        // is present, then it is given the innovation number present in the table, and if not then it is given an
        // entirely unique innovation number.
        if( gene instanceof ConnectionGene ) {
            List<Integer> temp = new ArrayList<>();
            temp.add( ( (ConnectionGene) gene).getInNode() );
            temp.add( ( (ConnectionGene) gene).getOutNode() );
            if( connectionInnovations.containsKey( temp ) ) {
                return connectionInnovations.get( temp );
            } else {
                connectionInnovations.put( temp, globalInnovation );
                return globalInnovation++;
            }

        // If the gene is a node than the NodeID and NodeType describe a topological feature, maybe...
        // Again, these values are checked against an innovation table, if it is found then it is given the innovation
        // number in the table, and if not found then it is given a unique number.
        } else if( gene instanceof NodeGene ) {
            List<Integer> temp = new ArrayList<>();
            temp.add( ((NodeGene) gene).getNodeID() );
            temp.add( ((NodeGene) gene).getNodeType() );
            if( nodeInnovations.containsKey( temp ) ) {
                return nodeInnovations.get( temp );
            } else {
                nodeInnovations.put( temp, globalInnovation );
                return globalInnovation++;
            }
        }
        return globalInnovation++;
    }

    public int getInnovation() {
        return innovation;
    }


    // Method responsible for comparing two gene objects. Needs to be done so that arrayList of gene objects can be
    // sorted.
    @Override
    public int compareTo( Gene otherGene ) {
        return this.getInnovation() - otherGene.getInnovation();
    }

}
