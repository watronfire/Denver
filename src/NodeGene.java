/**
 * Created by nate on 3/28/17.
 */



// A Previous version of this class used an integer value for nodeType where 0 is input, 1 is hidden, and 2 is output.
// TODO: nodeType needs to be converted into an enum. Also bias needs to be introduced.
public class NodeGene extends Gene {

    public enum nodeType { INPUT, BIAS, HIDDEN, OUTPUT }

    private int nodeID;
    private nodeType nt;

    private double activationResponse = 0;
    // Useful for determining depth of a neural net...
    private double splitY;

    // For creating a new nodeGene from scratch
    public NodeGene( int nodeID, nodeType nt, double splitY ) {
        this.nodeID = nodeID;
        this.nt = nt;
        innovation = Gene.getGlobalInnovation( this );
        activationResponse = ( Math.random() * 2 ) - 1;
        this.splitY = splitY;
    }

    // For creating a nodeGene from a pre-existing nodeGene
    public NodeGene( int nodeID, int innovation, nodeType nt, double activationResponse, double splitY ) {
        this.nodeID = nodeID;
        this.innovation = innovation;
        this.nt = nt;
        this.activationResponse = activationResponse;
        this.splitY = splitY;
    }

    public int getNodeID() { return nodeID; }
    public nodeType getNodeType() { return nt; }
    public int getNodeTypeInt() {
        int output;
        switch ( nt ) {
            case INPUT: output = 0;
                break;
            case BIAS: output = 1;
                break;
            case HIDDEN: output = 2;
                break;
            case OUTPUT: output = 3;
                break;
            default: output = 0;
        }
        return output;
    }
    public String getNodeTypeString() {
        String output;
        switch( nt ) {

            case INPUT:
                output = "INPUT";
                break;
            case BIAS:
                output = "BIAS";
                break;
            case HIDDEN:
                output = "HIDDEN";
                break;
            case OUTPUT:
                output = "OUTPUT";
                break;
            default:
                output = "??";
        }
        return output;
    }
    public double getActivationResponse() { return activationResponse; }
    public double getSplitY() { return splitY; }
    public static nodeType parseNodeType( String input ) {
        switch( input ) {
            case "INPUT":
                return nodeType.INPUT;
            case "BIAS":
                return nodeType.BIAS;
            case "HIDDEN":
                return nodeType.HIDDEN;
            case "OUTPUT":
                return nodeType.OUTPUT;
            default:
                return nodeType.HIDDEN;
        }
    }
    public void setActivationResponse(double activationResponse) {
        this.activationResponse = activationResponse;
    }
}
