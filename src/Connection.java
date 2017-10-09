/**
 * Created by nate on 7/8/17.
 */
// The actual connect, rather than a description. Contains references to the two nodes it connects and the
// connection weights.
public class Connection {

    // References to the nodes this connection connects
    Node inNode;
    Node outNode;

    // The connection weight
    double weight;

    public Connection( double weight, Node in, Node out ) {
        this.weight = weight;
        inNode = in;
        outNode = out;
    }

    public Node getInNode() { return inNode; }

}
