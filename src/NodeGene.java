/**
 * Created by nate on 3/28/17.
 */
public class NodeGene extends Gene {

    private int nodeID;
    private int nodeType;              // So 0 is input, 1 is hidden, and 2 is output. Probably a better way to do this.
    private double value = 0.0;     // If the node is an input node then it should have a value of some kind. I think.
                                           // While I'm making this a double, I'm not sure if that correct.

    private double activationResponse = 0;
    // Useful for determining depth of a neural net...
    private double splitY;

    // For creating a new nodeGene from scratch
    public NodeGene( int nodeID, int nodeType, double splitY ) {
        this.nodeID = nodeID;
        innovation = Gene.getGlobalInnovation( this );
        this.nodeType = nodeType;
        activationResponse = ( Math.random() * 2 ) - 1;
        this.splitY = splitY;
    }

    // For creating a nodeGene from a pre-existing nodeGene
    public NodeGene( int nodeID, int innovation, int nodeType, double activationResponse, double splitY ) {
        this.nodeID = nodeID;
        this.innovation = innovation;
        this.nodeType = nodeType;
        this.activationResponse = activationResponse;
        this.splitY = splitY;
    }


    public int getNodeID() { return nodeID; }
    public int getNodeType() { return nodeType; }
    public double getValue() { return value; }
    public double getActivationResponse() { return activationResponse; }
    public double getSplitY() { return splitY; }

    public void setActivationResponse(double activationResponse) {
        this.activationResponse = activationResponse;
    }
    public void setValue( double value ) { this.value = value; }
}
