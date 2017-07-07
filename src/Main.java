import java.util.ArrayList;

public class Main {

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                         //
    //  Alright, so I what I want to do with this program is to make win predictions of MLS    //
    //  game using a neural network.                                                           //
    //                                                                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        ArrayList<Genome> genomePool = new ArrayList<>();
        ArrayList<Species> speciesPool = new ArrayList<>();
        int population = 50;
        for( int i = 0; i < population; i++ ) {
            genomePool.add( new Genome( 5, 2 ) );
        }

    }
}
