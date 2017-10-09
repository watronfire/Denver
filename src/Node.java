import java.util.ArrayList;

/**
 * Created by nate on 7/8/17.
 */
// The actual node that a NodeGene describes
public class Node {

    // All the connections coming in to this neuron
    private ArrayList<Connection> connectionsIn = new ArrayList<>();
    // All the connections going out of this neuron
    private ArrayList<Connection> connectionsOut = new ArrayList<>();

    private static int NodeNum = 1;

    private int nodeNumLocal;

    // The output from this neuron
    private double output;

    // The type of neuron this is. 0 = input, 1 = hidden, 2 = output
    private NodeGene.nodeType nt;

    // The ID of this neuron
    private int nodeID;

    // The curvature of the sigmoid function
    private double activationResponse;

    //Used in determining neural net depth.

    //Constructor
    public Node( NodeGene.nodeType nodeType, int nodeID, double activationResponse ) {
        this.nt = nodeType;
        this.nodeID = nodeID;
        this.activationResponse = activationResponse;
        // output = 0;

        nodeNumLocal = NodeNum;
        NodeNum += 1;
    }

    public void addIncomingConnection( Connection connection ) {
        connectionsIn.add( connection );
    }
    public void addOutgoingConnection( Connection connection ) {
        connectionsOut.add( connection );
    }
    
    public NodeGene.nodeType getNodeType() { return nt; }
    public int getNodeID() { return nodeID; }
    public ArrayList<Connection> getConnectionsIn() { return connectionsIn; }
    public ArrayList<Connection> getConnectionsOut() { return connectionsOut; }
    public double getOutput() {
        //System.out.println( "Output being sent: " + output + " | by Node " + nodeNumLocal );
        return output;
    }
    public double getActivationResponse() {
        return activationResponse;
    }
    
    public void setOutput( double output ) { this.output = output; }
    // Used for xor test.
    public void setOutput( boolean output ) {
        if( output == true ) {
              this.output = 1.0;
        } else {
            this.output = 0.0;
        }

        //System.out.println( "Output actually set to: " + this.output );
    }

}
