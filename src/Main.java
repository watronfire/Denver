import java.util.ArrayList;

public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {


        Genome gen1 = new Genome( 5, 1 );
        Genome gen2 = new Genome( 5, 1 );

        GenomeManager gm = new GenomeManager();

        Genome child = gm.crossover( gen1, gen2 );

        System.out.println();
        System.out.println( "Child: " );

        child.reportNodes();
        child.reportConnections();


    }
}
