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

    // Sum of weights x inputs
    private double sumActivation;

    // The output from this neuron
    private double output;

    // The type of neuron this is. 0 = input, 1 = hidden, 2 = output
    private int nodeType;

    // The ID of this neuron
    private int nodeID;

    // The curvature of the sigmoid function
    private double activationResponse;

    //Used in determining neural net depth.
    private double splitY;

    //Constructor
    public Node( int nodeType, int nodeID, double activationResponse, double splitY ) {
        this.nodeType = nodeType;
        this.nodeID = nodeID;
        this.activationResponse = activationResponse;
        this.splitY = splitY;
        sumActivation = 0;
        output = 0;
    }

    public void addIncomingConnection( Connection connection ) {
        connectionsIn.add( connection );
    }
    public void addOutgoingConnection( Connection connection ) {
        connectionsOut.add( connection );
    }
    
    public int getNodeType() { return nodeType; }
    public ArrayList<Connection> getConnectionsIn() { return connectionsIn; }
    public ArrayList<Connection> getConnectionsOut() { return connectionsOut; }
    public double getOutput() { return output; }
    public double getActivationResponse() {
        return activationResponse;
    }
    
    public void setOutput( double output ) { this.output = output; }

}
