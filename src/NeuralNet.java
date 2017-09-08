import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by nate on 7/8/17.
 */
// TODO: determine how to implement depth. I've got no idea.
public class NeuralNet {

    private ArrayList<Node> nodes = new ArrayList<>();
    private int depth;
    public enum runtype { SNAPSHOT, ACTIVE }

    public NeuralNet( Collection<Node> nodes, int depth ) {
        this.nodes = new ArrayList<>( nodes );
        this.depth = depth;
    }

    public double update( boolean[] inputs, runtype type ) {

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
                    node.setOutput( inputs[ inputIndex ] );
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
                    outputs += node.getOutput();
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

    public double sigmoid( double input, double activationResponse ) {
        return ( 1.0 / ( 1.0 + Math.exp( -input / activationResponse ) ) );
    }
}
