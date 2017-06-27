import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nate on 3/28/17.
 */
public class ConnectionGene extends Gene {

    // Instance variables
    private int inNode;
    private int outNode;
    private double weight;
    private boolean enabled;

    // Constructor when you need a random weight
    public ConnectionGene( int inNode, int outNode ) {
        this.inNode = inNode;
        this.outNode = outNode;
        innovation = Gene.getGlobalInnovation( this );

        // I'm generating a random weight between -2 and 2, because that's what the tutorial seems to use.
        weight = Math.random() * 2 - 1;
        enabled = true;
    }

    // Constructor when you don't want a random weight
    public ConnectionGene( int inNode, int outNode, double weight ) {
        this.inNode = inNode;
        this.outNode = outNode;
        innovation = Gene.getGlobalInnovation( this );
        this.weight = weight;
        enabled = true;
    }

    // Constructor when you're copying a connectionGene that already exists.
    public ConnectionGene( int inNode, int outNode, double weight, int innovation, boolean enabled ) {
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.innovation = innovation;
        this.enabled = enabled;
    }

    public int getInNode() { return inNode; }
    public int getOutNode() { return outNode; }
    public double getWeight() { return weight; }
    public boolean isEnabled() { return enabled; }

    public void setWeight( double weight ) { this.weight = weight; }
    public void disable() { enabled = false; }
    public void swapStatus() { enabled = !enabled; }

}
