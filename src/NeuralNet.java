import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by nate on 7/8/17.
 */
public class NeuralNet {

    private ArrayList<Node> nodes = new ArrayList<>();
    private int depth;
    public enum runtype { SNAPSHOT, ACTIVE }

    public NeuralNet( Collection<Node> nodes, int depth ) {
        this.nodes = new ArrayList<>( nodes );
        this.depth = depth;
    }

    // TODO: this method is still shit and needs to be rewritten.
    // Not as shitty anymore, problem probably isn't here anymore.
    public ArrayList<Double> update( double[] inputs, runtype type ) {

        // Create and ArrayList to put the outputs into
        ArrayList<Double> outputs = new ArrayList<>();
        // if the mode is SNAPSHOT then all neurons must be iterated through as many times as the network
        // is deep. If the mode is set to ACTIVE the method can return an output after just one iteration.
        int flushCount;

        if( type == runtype.SNAPSHOT ) {
            flushCount = depth;
        } else {
            flushCount = 1;
        }


        for( int i = 0; i < flushCount; ++i ) {
            // Clear the output vector
            outputs.clear();

            // Iterate through each node of the phenotype.
            for( Node node : nodes ) {

                // If its input then assign the value specified in the input array.
                if( node.getNodeType() == NodeGene.nodeType.INPUT ) {
                    node.setOutput( inputs[ node.getNodeID() - 1 ] );
                } else if( node.getNodeType() == NodeGene.nodeType.BIAS ) {
                    node.setOutput( 1.0 );
                } else {
                    // This will hold the sum of all the inputs * weight.
                    double sum = 0;

                    // Sum the neuron's inputs by iterating through all the links into the neuron
                    for( Connection connection : node.getConnectionsIn() ) {
                        sum += connection.weight * connection.getInNode().getOutput();
                        //System.out.println( "ConnectionWeight: " + connection.weight + " | NodeOutput: " + connection.getInNode().getOutput() + " | Sum: " + sum );
                    }

                    node.setOutput( sigmoid( sum, node.getActivationResponse() ) );

                    if( node.getNodeType() == NodeGene.nodeType.OUTPUT ) {
                        outputs.add( node.getOutput() );
                    }
                }
            }
        }

        if( type == runtype.SNAPSHOT ) {
            for( Node node : nodes ) {
                node.setOutput( 0 );
            }
        }

        return outputs;
    }

/*
    public double update( ArrayList<Double> inputs, runtype type ) {

        // Create and ArrayList to put the outputs into
        double outputs = 0;
        // if the mode is SNAPSHOT then all neurons must be iterated through as many times as the network
        // is deep. If the mode is set to ACTIVE the method can return an output after just one iteration.
        int flushCount;

        if( type == runtype.SNAPSHOT ) {
            flushCount = depth;
        } else {
            flushCount = 1;
        }

        for( int i = 0; i < flushCount; i++ ) {

            // Clear the output vector
            outputs = 0;

            // This is an index into the current neuron.
            int inputIndex = 0;

            // first set the outputs of the input neurons to be equal to the values passed into the function in inputs
            for( Node node : nodes ) {
                if( node.getNodeType() == 0 ) {
                    node.setOutput( inputs.get( inputIndex ) );
                    inputIndex += 1;
                }
            }

            // Set the output of the bias to 1. WTF I don't know what the means.

            // Step through the network a neuron at a time
            for( Node node : nodes ) {

                // This will hold the sum of all the inputs * weight
                double sum = 0;

                // Sum the neuron's inputs by iterating through all the links into the neuron
                for( Connection connection : node.getConnectionsIn() ) {
                    sum += connection.weight * connection.getInNode().getOutput();
                }
                node.setOutput( sigmoid( sum, node.getActivationResponse() ) );

                if( node.getNodeType() == 2 ) {
                    outputs = node.getOutput();
                }
            }
        }

        if( type == runtype.SNAPSHOT ) {
            for( Node node : nodes ) {
                node.setOutput( 0 );
            }
        }

        return outputs;
    }
*/

    // TODO: may play around with multiplying input by a fraction to widen the curve.
    public double sigmoid( double input, double activationResponse ) {
        // Original return
        return ( 2.0 / ( 1.0 + Math.exp( -input ) ) );
    }

    // Potentially use this for soccer results, as
    public double tanh( double input, double activationResponse ) {
        return ( 2.0 / ( 1.0 + Math.exp( -( 2.0 * input / activationResponse ) ) ) ) - 1.0;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }


    public void reportNodes() {
        String type;

        for( Node node : nodes ) {
            switch( node.getNodeType() ) {
                case INPUT: type = "Input";
                    break;
                case HIDDEN: type = "Hidden";
                    break;
                case OUTPUT: type = "Output";
                    break;
                case BIAS: type = "Bias";
                    break;
                default: type = "Invalid Type";
                    break;
            }
            System.out.println( "NodeID: " + node.getNodeID() + " | NodeType: " + type );
        }
    }
}
