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
        gen1.reportNodes();
        gen1.reportConnections();
        System.out.println();
        Genome gen2 = new Genome( 5, 1 );
        gen2.reportNodes();
        gen2.reportConnections();

        GenomeManager gm = new GenomeManager();



    }
}
