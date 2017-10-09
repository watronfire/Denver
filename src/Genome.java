import java.util.*;

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
    private double adjustedFitness = 0;
    private double spawnAmount = 0;
    private int depth;
    private double odds;

    private NeuralNet phenotype;


    //////////////////
    // CONSTRUCTORS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Constructor when creating a genome from scratch.
    public Genome( int inputs, int outputs ) {
        NodeGene[] inputNodes = new NodeGene[ inputs + 1 ];
        NodeGene[] outputNodes = new NodeGene[ outputs ];

        // Create input nodes as specified by parameters
        for( int i = 0; i < inputs; i += 1 ) {
            inputNodes[ i ] = new NodeGene( nodeNum, NodeGene.nodeType.INPUT, 0.0 );
            nodeNum += 1;
        }

        // Create a bias node
        inputNodes[ inputs ] = new NodeGene( nodeNum, NodeGene.nodeType.BIAS, 0.0 );
        nodeNum += 1;

        // Create output nodes as specified by parameters
        for( int i = 0; i < outputs; i += 1 ) {
            outputNodes[ i ] = new NodeGene( nodeNum, NodeGene.nodeType.OUTPUT, 1.0 );
            nodeNum += 1;
        }

        // Connect all input nodes and bias nodes to output nodes.
        for( NodeGene in : inputNodes ) {
            for( NodeGene on : outputNodes ) {
                connectionGenes.add( new ConnectionGene( in.getNodeID(), on.getNodeID() ) );
            }
        }

        // Add the nodes to the nodeGenes ArrayList
        nodeGenes.addAll( Arrays.asList( inputNodes ) );
        nodeGenes.addAll( Arrays.asList( outputNodes ) );


    }

    // Constructor to be used during crossover, when you already have a list of genes.
    public Genome( ArrayList<Gene> genes ) {
        for( Gene g : genes ) {
            if( g instanceof ConnectionGene ) {
                addConnectionGene( (ConnectionGene) g );
            } else if ( g instanceof NodeGene ) {
                addNodeGene( (NodeGene) g );
                if( ( (NodeGene) g ).getNodeType() == NodeGene.nodeType.OUTPUT ) {
                    outputNodeID = ( (NodeGene) g ).getNodeID();
                }
            }
        }
    }


    ///////////////////
    // GENE ADDITION //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Adds a new connectionGene to the genome with defined properties, also can be used to copy a connectionGene
    public void addConnectionGene( ConnectionGene gc ) {
        connectionGenes.add( new ConnectionGene( gc.getInNode(), gc.getOutNode(), gc.getWeight(), gc.getInnovation(), gc.isEnabled() ) );
    }

    // Adds a new nodeGene to the genome with defined properties, or a copy of an already present nodeGene
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

        //System.out.println( "Point Mutated" );

    }
    // Randomly add a new connection to the network with a random weight. Problematic because nodes can only be
    // connected in a forward manner.
    public void mutateLink() {

        // Pick an initial node. Can only be input, hidden, or bias node.
        NodeGene nodeInitial = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        while( nodeInitial.getNodeType() == NodeGene.nodeType.OUTPUT ) {
            nodeInitial = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        }

        // Pick a terminus. Again, must be connected in a forward manner, so output must be hidden or output.
        // This loop will also make sure that the same node isn't both initial node and terminus.
        NodeGene nodeTerminus = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        while( nodeTerminus.getNodeType() == NodeGene.nodeType.INPUT || nodeTerminus.getNodeID() == nodeInitial.getNodeID() ) {
            nodeTerminus = nodeGenes.get( rng.nextInt( nodeGenes.size() ) );
        }

        // Need to compare the connection to pre-existing connections to make sure it isn't redundant. Should that fall
        // within this method, or should I create another one.
        if( redundantConnection( nodeInitial.getNodeID(), nodeTerminus.getNodeID() ) ) {
            return;
        }

        // If the program makes it to this point then the connection is made and given an innovation number.
        connectionGenes.add( new ConnectionGene( nodeInitial.getNodeID(), nodeTerminus.getNodeID() ) );
        //System.out.println( "Link Mutated" );

    }
    // Randomly adds a new node to the network by disabling a connection, replacing it with a connection of weight 1, a
    // hidden node, and a connection of the same weight as the disabled connection
    // TODO: The genetic algorithm seems to favor create lots and lots of nodes/connections with this method.
    // so a limiter needs to be implemented if the number of connections is greater than some limit.
    public void mutateNode() {

        int attempts = 5;

        ConnectionGene chosenGene = null;

        boolean isDone = false;
        int chosenLink;
        Random rdm = new Random();

        //We need to choose a connectionGene to mutate. If the genome is small then one of the older links
        // should be split so as to avoid a chaining effect. A genome is considered small if it has less
        // than 5 hidden neurons.
        if( GenomeManager.determineHiddenNodes( nodeGenes ) < Parameters.sizeThreshold ) {
            while( attempts >= 0 ) {

                if ( connectionGenes.size() == 0 ) {
                    return;
                } else if ( connectionGenes.size() == 1 ) {
                    chosenGene = connectionGenes.get( 0 );
                    isDone = true;
                    attempts = -1;
                } else {
                    chosenLink = rdm.nextInt( connectionGenes.size() - (int) Math.sqrt( connectionGenes.size() ) );
                    chosenGene = connectionGenes.get( chosenLink );
                    if ( chosenGene.isEnabled() ) {
                        isDone = true;
                        attempts = -1;
                    }
                }
                attempts -= 1;
            }
            if( !isDone ) {
                // Essentially failed to find a gene
                return;
            }
        } else {

            // Everything in this block is for genomes which have reached the 5 hidden node size threshold

            // Check to made sure there are connections which can be mutated.
            if (connectionGenes.size() == 0) {
                return;
            }

            while( !isDone ) {
                if( attempts < 0 ) {
                    return;
                }

                chosenLink = rdm.nextInt(connectionGenes.size());
                chosenGene = connectionGenes.get(chosenLink);

                if (chosenGene.isEnabled()) {
                    isDone = true;
                }

                attempts -= 1;
            }

        }


        // Select the connection to be mutated, save its weight, and disable it.
        double oldWeight = chosenGene.getWeight();
        chosenGene.disable();

        // Create the new hidden node, saving its ID number for later use.
        double tempSplitY = ( getNodeGene( chosenGene.getInNode() ).getSplitY() + getNodeGene( chosenGene.getOutNode() ).getSplitY() ) / 2;
        nodeGenes.add( new NodeGene( nodeNum, NodeGene.nodeType.HIDDEN, tempSplitY ) );
        int newNodeID = nodeNum;
        nodeNum += 1;

        // Create a connection between the input of the old connection, and the new hidden node, with weight 1.
        connectionGenes.add( new ConnectionGene( chosenGene.getInNode(), newNodeID, 1.0 ) );

        // Create a connection from the new hidden node to the output of the old connection with weight equal to the old
        // connection's weight.
        connectionGenes.add( new ConnectionGene(newNodeID, chosenGene.getOutNode(), oldWeight ) );
        //System.out.println( "Node Mutated" );

    }
    // Chooses a random connection and switched its enabled status
    public void mutateEnable() {
        connectionGenes.get( rng.nextInt( connectionGenes.size() ) ).swapStatus();
        //System.out.println( "Enable Mutated" );
    }
    // Mutates the output threshold by either setting to a random double or adjusting slightly.
    public void mutateThreshold() {
        if( rng.nextBoolean() ) {
            outputThreshold = rng.nextDouble() * 2 - 1;
        } else {
            outputThreshold += rng.nextDouble() * 0.2 - 0.1;
        }

        //System.out.println( "Threshold Mutated" );
    }
    public void mutateActivationResponse() {
        int index = rng.nextInt( nodeGenes.size() );

        nodeGenes.get( index ).setActivationResponse( nodeGenes.get( index ).getActivationResponse() + ( rng.nextDouble() * 2 - 1 ) );

        //System.out.println( "Activation Response Mutated" );
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
    public void createPhenotype() {
        // Make sure no phenotype is already present for this genome
        // TODO: implement deletePhenotype().
        phenotype = null;

        // ArrayList to hold all the node required for phenotype
        HashMap<Integer, Node> nodes = new HashMap<>();

        // First we're going to create all the nodes
        for( NodeGene ng : nodeGenes ) {
            nodes.put( ng.getNodeID(), new Node( ng.getNodeType(), ng.getNodeID(), ng.getActivationResponse() ) );
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
    }
    public void calculateFitness( XORExample[] tests ) {
        fitness = 0;
        int falseAnswers = 1;
        int trueAnswers = 1;
        // Simple fitness method, if it gets the test correct, fitness is increased. Fitness decreased for incorrect.
        for( XORExample test : tests ) {
            double result = phenotype.update( test.getInputs(), NeuralNet.runtype.SNAPSHOT );
            boolean calculatedOutput = result < outputThreshold;
            if( calculatedOutput == test.getOutput() ) {
                fitness += 1;
            } //else {
                //fitness -= Math.abs( result - outputThreshold );
            //}

        }
    }
    // Useless method for determining where the problem is
    public void calculateFitness( XORExample test ) {
        double result = phenotype.update( test.getInputs(), NeuralNet.runtype.SNAPSHOT );

        boolean calculatedOutput = result > outputThreshold;
        System.out.print( "Result: " + result + " | Output Threshold: " + outputThreshold + " | Calculated Output: " + calculatedOutput );

    }


    ////////////////////
    // GETTER METHODS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Yields an ArrayList of all genes. Hopefully in order of their innovation number.
    public ArrayList<Gene> getAllGenes() {
        ArrayList<Gene> temp = new ArrayList<>();
        temp.addAll( connectionGenes );
        temp.addAll( nodeGenes );

        // Since all genes are comparable, you can use sort on them! Sort in ascending order, iIthink.
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
    public double getFitness( int i ) { return fitness; }
    // Returns the number of children this genome should spawn.
    public double getSpawnAmount() { return spawnAmount; }
    public double getAdjustedFitness() { return adjustedFitness; }
    public double getDepth() { return depth; }

    public NeuralNet getPhenotype() {
        return phenotype;
    }

    ////////////////////
    // SETTER METHODS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setSpawnAmount( double spawnAmount ) {
        this.spawnAmount = spawnAmount;
    }
    public void setAdjustedFitness( double adjustedFitness ) { this.adjustedFitness = adjustedFitness; }
    public void setFitness( double fitness ) { this.fitness = fitness; }

    //////////////////////
    // REPORTER METHODS //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Basically just a test that whatever I'm doing is working. Should have probably made this in libGDX so that
    // I could make this visual.
    public void reportNodes() {
        String type;

        for( NodeGene ng : nodeGenes ) {
            switch( ng.getNodeType() ) {
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
            System.out.println( "NodeID: " + ng.getNodeID() + " | NodeType: " + type + " | Innovation: " + ng.getInnovation() );
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
    public void reportPhenotypeNodes() {
        phenotype.reportNodes();
    }

    @Override
    // Because the values being compared are doubles, we need to lay out every case.
    public int compareTo( Genome genome ) {
        // I don't understand which direction these should be.
        if( this.getFitness(1) < genome.getFitness(1) ) {
            return -1;
        } else if( this.getFitness(1) > genome.getFitness(1) ) {
            return 1;
        }
        return 0;

    }

    public void setDepth( int depth ) {
        this.depth = depth;
    }
}