import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by nate on 3/29/17.
 */


public class Genome implements Comparable<Genome> {

    private ArrayList<NodeGene> nodeGenes = new ArrayList<>();
    private ArrayList<ConnectionGene> connectionGenes = new ArrayList<>();
    private int nodeNum = 1;

    private int outputNodeID;
    private double outputThreshold = 0.5;
    private Random rng = new Random();
    private double fitness = 0;
    private double spawnAmount = 0;
    private int depth;

    private NeuralNet phenotype;


    //////////////////
    // CONSTRUCTORS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Constructor, only used once so I can be kinda hacky.
    public Genome( int inputs, int outputs ) {

        // Create input nodes specified by parameters.
        // nodeNum is a global variable which allows assignment of nodeIDs and is incremented after each node creation.
        for( int i = 0; i < inputs; i += 1 ) {
            nodeGenes.add( new NodeGene( nodeNum, 0, 0.0 ) );
            nodeNum += 1;
        }

        // Save a single input node to make a connection later.
        NodeGene ngTempInitial = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );

        // After input nodes are created, the output nodes will be created. One input and output node need to be save,
        // to make a connection between later. We save the nodeNum at which this split occurs so we can select an output
        // node later
        int inputOutputSplit = nodeNum;

        // Creates output nodes as specified by parameters, incrementing nodeNum each time.
        for( int i = 0; i < outputs; i += 1 ) {
            nodeGenes.add( new NodeGene( nodeNum, 2, 1.0 ) );
            outputNodeID = nodeNum;
            nodeNum += 1;
        }

        // Save a single output node to make a connection later.
        NodeGene ngTempTerminus = nodeGenes.get( rng.nextInt( outputs ) + inputOutputSplit - 1 );

        // Create a connection with the save input and output node. Increasing the global innovation number by one.
        // The innovation number will be used later for crossover events.
        connectionGenes.add( new ConnectionGene( ngTempInitial.getNodeID(), ngTempTerminus.getNodeID() ) );

    }

    // Constructor to be used during crossover, when you already have a list of genes.
    public Genome( ArrayList<Gene> genes ) {
        for( Gene g : genes ) {
            if( g instanceof ConnectionGene ) {
                addConnectionGene( (ConnectionGene) g );
            } else if ( g instanceof NodeGene ) {
                addNodeGene( (NodeGene) g );
                if( ( (NodeGene) g ).getNodeType() == 2 ) {
                    outputNodeID = ( (NodeGene) g ).getNodeID();
                }
            }
        }
    }


    ///////////////////
    // GENE ADDITION //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Adds a new connectionGene to the genome with defined properties, also can be used to copy a connectionGene
    public void addConnectionGene( int inNode, int outNode, double weight, int innovation, boolean enabled ) {
        connectionGenes.add( new ConnectionGene( inNode, outNode, weight, innovation, enabled ) );
    }
    public void addConnectionGene( ConnectionGene gc ) {
        connectionGenes.add( new ConnectionGene( gc.getInNode(), gc.getOutNode(), gc.getWeight(), gc.getInnovation(), gc.isEnabled() ) );
    }

    // Adds a new nodeGene to the genome with defined properties, or a copy of an already present nodeGene
    public void addNodeGene( int nodeID, int innovation, int nodeType, double activationResponse, double splitY ) {
        nodeGenes.add( new NodeGene( nodeID, innovation, nodeType, activationResponse, splitY ) );
    }
    public void addNodeGene( NodeGene ng ) {
        nodeGenes.add( new NodeGene( ng.getNodeID(), ng.getInnovation(), ng.getNodeType(), ng.getActivationResponse(), ng.getSplitY() ) );
    }


    ////////////////////////
    // MUTATION FUNCTIONS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Randomly update the weight of a randomly selected connected gene
    public void mutatePoint() {

        // Choose a connection to mutate.
        int index = rng.nextInt( connectionGenes.size() );

        // Essentially there are two different mutation choices.
        // The first is to change weight to a completely random weight. The second is to just adjust the weight slightly.
        if( rng.nextBoolean() ) {
            connectionGenes.get( index ).setWeight( rng.nextDouble() * 4 - 2);
        } else {
            double oldWeight = connectionGenes.get( index ).getWeight();
            connectionGenes.get( index ).setWeight( oldWeight + rng.nextDouble() * 0.2 - 0.1);
        }

    }
    // Randomly add a new connection to the network with a random weight. Problematic because nodes can only be
    // connected in a forward manner.
    public void mutateLink() {

        // Pick an initial node. Can only be input or hidden node.
        NodeGene nodeInitial = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        while( nodeInitial.getNodeType() == 2 ) {
            nodeInitial = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        }

        // Pick a terminus. Again, must be connected in a forward manner, so output must be hidden or output.
        // This loop will also make sure that the same node isn't both initial node and terminus.
        NodeGene nodeTerminus = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        while( nodeTerminus.getNodeType() == 0 || nodeTerminus.getNodeID() == nodeInitial.getNodeID() ) {
            nodeTerminus = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        }

        // Need to compare the connection to pre-existing connections to make sure it isn't redundant. Should that fall
        // within this method, or should I create another one.
        if( redundantConnection( nodeInitial.getNodeID(), nodeTerminus.getNodeID() ) ) {
            return;
        }

        // If the program makes it to this point then the connection is made and given an innovation number.
        connectionGenes.add( new ConnectionGene( nodeInitial.getNodeID(), nodeTerminus.getNodeID() ) );

    }
    // Randomly adds a new node to the network by disabling a connection, replacing it with a connection of weight 1, a
    // hidden node, and a connection of the same weight as the disabled connection
    public void mutateNode() {

        // Check to made sure there are connections which can be mutated.
        if( connectionGenes.size() == 0 ) {
            return;
        }

        // Select the connection to be mutated, save its weight, and disable it.
        int index = rng.nextInt( connectionGenes.size() );
        double oldWeight = connectionGenes.get( index ).getWeight();
        connectionGenes.get( index ).disable();

        // Create the new hidden node, saving its ID number for later use.
        double tempSplitY = ( getNodeGene( connectionGenes.get( index ).getInNode() ).getSplitY() + getNodeGene( connectionGenes.get( index ).getOutNode() ).getSplitY() ) / 2;
        nodeGenes.add( new NodeGene( nodeNum, 1, tempSplitY ) );
        int newNodeID = nodeNum;
        nodeNum += 1;

        // Create a connection between the input of the old connection, and the new hidden node, with weight 1.
        connectionGenes.add( new ConnectionGene( connectionGenes.get( index ).getInNode(), newNodeID, 1.0 ) );

        // Create a connection from the new hidden node to the output of the old connection with weight equal to the old
        // connection's weight.
        connectionGenes.add( new ConnectionGene( newNodeID, connectionGenes.get( index ).getOutNode(), oldWeight ) );

    }
    // Chooses a random connection and switched its enabled status
    public void mutateEnable() {
        connectionGenes.get( rng.nextInt( connectionGenes.size() ) ).swapStatus();
    }
    // Mutates the output threshold by either setting to a random double or adjusting slightly.
    public void mutateThreshold() {
        if( rng.nextBoolean() ) {
            outputThreshold = rng.nextDouble() * 2 - 1;
        } else {
            outputThreshold += rng.nextDouble() * 0.2 - 0.1;
        }
    }
    public void mutateActivationResponse() {
        int index = rng.nextInt( nodeGenes.size() );

        nodeGenes.get( index ).setActivationResponse( nodeGenes.get( index ).getActivationResponse() + ( rng.nextDouble() * 2 - 1 ) );
    }

    ////////////////////
    // GENOME CLEANUP //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Determines whether the nodes represented by the initial and terminus values are already connected or not.
    public boolean redundantConnection( int initial, int terminus ) {
        for( int i = 0; i < connectionGenes.size(); i += 1 ) {
            if( connectionGenes.get( i ).getInNode() == initial && connectionGenes.get( i ).getOutNode() == terminus ) {
                return true;
            } else if ( connectionGenes.get( i ).getInNode() == terminus && connectionGenes.get( i ).getOutNode() == initial ) {
                return true;
            }
        }
        return false;
    }
    // Removes connections which are not enabled.
    public void cullConnections() {
        for( int i = 0; i < connectionGenes.size(); i += 1 ) {
            if( !connectionGenes.get( i ).isEnabled() ) {
                connectionGenes.remove( i );
            }
        }
    }


    ////////////////
    // EVALUATION //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Creates a neural net from the genome
    public NeuralNet createPhenotype() {
        // Make sure no phenotype is already present for this genome
        // TODO: implement deletePhenotype().
        phenotype = null;

        // ArrayList to hold all the node required for phenotype
        HashMap<Integer, Node> nodes = new HashMap<>();

        // First we're going to create all the nodes
        for( NodeGene ng : nodeGenes ) {
            nodes.put( ng.getNodeID(), new Node( ng.getNodeType(), ng.getNodeID(), ng.getActivationResponse(), ng.getSplitY() ) );
        }

        // Now create the links
        for( ConnectionGene cg : connectionGenes ) {
            if( cg.isEnabled() ) {
                Node inNode = nodes.get( cg.getInNode() );
                Node outNode = nodes.get( cg.getOutNode() );

                Connection tmpConnection = new Connection( cg.getWeight(), inNode, outNode );

                inNode.addOutgoingConnection( tmpConnection );
                outNode.addIncomingConnection( tmpConnection );
            }
        }

        phenotype = new NeuralNet( nodes.values(), depth );
        return phenotype;
    }
    public void calculateFitness( XORExample[] tests ) {
        fitness = 0;

        // Simple fitness method, if it gets the test correct, fitness is increased. Fitness decreased for incorrect.
        for( XORExample test : tests ) {
            double result = phenotype.update( test.getInputs(), NeuralNet.runtype.SNAPSHOT );
            boolean calculatedOutput = result > outputThreshold;
            if( calculatedOutput == test.getOutput() ) {
                fitness += 1;
            } else {
                fitness -= Math.abs( result - outputThreshold );
            }
        }
    }


    ////////////////////
    // GETTER METHODS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Yields an ArrayList of all genes. Hopefully in order of their innovation number.
    public ArrayList<Gene> getAllGenes() {
        ArrayList<Gene> temp = new ArrayList<>();
        temp.addAll( connectionGenes );
        temp.addAll( nodeGenes );

        // Since all genes are comparable, you can use sort on them!
        Collections.sort( temp );
        return temp;
    }
    // Because connections hold only the ID of their input or output, this function returns the NodeGene with a certain ID
    public NodeGene getNodeGene( int nodeID ) {
        for( int i = 0; i < nodeGenes.size(); i += 1 ) {
            if( nodeID == nodeGenes.get( i ).getNodeID() ) {
                return nodeGenes.get( i );
            }
        }
        return null;
    }
    public ArrayList<NodeGene> getAllNodeGenes() {
        return nodeGenes;
    }
    public ArrayList<ConnectionGene> getAllConnectionGenes() { return connectionGenes; }
    // Returns the number of connection genes.
    public double getFitness() { return fitness; }
    // Returns the number of children this genome should spawn.
    public double getSpawnAmount() { return spawnAmount; }


    ////////////////////
    // SETTER METHODS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setSpawnAmount( double spawnAmount ) {
        this.spawnAmount = spawnAmount;
    }

    //////////////////////
    // REPORTER METHODS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Basically just a test that whatever I'm doing is working. Should have probably made this in libGDX so that
    // I could make this visual.
    public void reportNodes() {
        String type;

        for( int i = 0; i < nodeGenes.size(); i += 1 ) {
            switch( nodeGenes.get( i ).getNodeType() ) {
                case 0: type = "Input";
                    break;
                case 1: type = "Hidden";
                    break;
                case 2: type = "Output";
                    break;
                default: type = "Invalid Type";
                    break;
            }
            System.out.println( "NodeID: " + nodeGenes.get( i ).getNodeID() + " | NodeType: " + type + " | Innovation: " + nodeGenes.get( i ).getInnovation() );
        }
    }
    public void reportConnections() {
        for( int i = 0; i < connectionGenes.size(); i += 1 ) {
            System.out.println( " " );
            System.out.println( "InNode: " + connectionGenes.get( i ).getInNode() );
            System.out.println( "OutNode: " + connectionGenes.get( i ).getOutNode() );
            System.out.println( "Weight: " + connectionGenes.get( i ).getWeight() + " | Enabled: " + connectionGenes.get( i ).isEnabled() + " | Innovation: " + connectionGenes.get( i ).getInnovation() );

        }
    }

    @Override
    // Because the values being compared are doubles, we need to lay out every case.
    public int compareTo(Genome genome) {
        if( this.getFitness() < genome.getFitness() ) {
            return -1;
        } else if( genome.getFitness() < this.getFitness() ) {
            return 1;
        }
        return 0;

    }

    public void setDepth( int depth ) {
        this.depth = depth;
    }
}
