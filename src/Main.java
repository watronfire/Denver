public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {


        Genome gen1 = new Genome( 5, 1 );
        gen1.reportConnections();
        Genome gen2 = new Genome( 5, 1 );
        gen2.reportConnections();

        GenomeManager gm = new GenomeManager();
        //System.out.println( "Different Species? " + gm.compareGenomes( gen1, gen2 ) );

        System.out.println( " " );
        System.out.println( "Child Genome" );
        Genome genChild = gm.crossover( gen1, gen2 );
        genChild.reportNodes();
        genChild.reportConnections();
    }
}
