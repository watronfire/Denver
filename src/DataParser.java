import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nate on 4/8/17.
 */

// So I think I need to create my own database, but until I do that I should probably just use a database that's already
// been created. Maybe basic record, that kinda thing.

public class DataParser {

    private ArrayList<MLSGame> mlsGames = new ArrayList<>();

    public DataParser( String csvFile ) {
        String line;
        String cvsSplitBy = ",";

        try ( BufferedReader br = new BufferedReader( new FileReader( csvFile ) ) ) {
            while ( ( line = br.readLine() ) != null ) {

                // use comma as separator 0-11 are symptoms, 12 is outcome.
                String[] game = line.split( cvsSplitBy );
                double[] metrics = new double[13];
                int[] outcomes = new int[3];

                metrics[0] = Double.parseDouble( game[4] );
                metrics[1] = Double.parseDouble( game[5] );
                metrics[2] = Double.parseDouble( game[7] );
                metrics[3] = Double.parseDouble( game[8] );
                metrics[4] = Double.parseDouble( game[15] );
                metrics[5] = Double.parseDouble( game[16] );
                metrics[6] = Double.parseDouble( game[17] );
                metrics[7] = Double.parseDouble( game[18] );
                metrics[8] = Double.parseDouble( game[19] );
                metrics[9] = Double.parseDouble( game[20] );
                metrics[10] = Double.parseDouble( game[21] );
                metrics[11] = Double.parseDouble( game[21] );
                metrics[12] = Double.parseDouble( game[23] );

                String result = game[14];
                switch( result ) {
                    case "H": outcomes[0] = 0;
                        break;
                    case "D": outcomes[0] = 1;
                        break;
                    case "A": outcomes[0] = 2;
                        break;
                }
                outcomes[1] = Integer.parseInt( game[12] );
                outcomes[2] = Integer.parseInt( game[13] );


                mlsGames.add( new MLSGame( metrics, outcomes ) );
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public MLSGame getRandomMLSGame() {
        Random ran = new Random();
        return mlsGames.get( ran.nextInt( mlsGames.size() ) );
    }

    public void reportOutcomes() {
        for( MLSGame mlsg : mlsGames ) {
            for( int i : mlsg.getOutcomes() ) {
                System.out.print( i + "," );
            }
            System.out.println();
        }
    }
    public void reportSymptoms() {
        for( MLSGame mlsg : mlsGames ) {
            for( double f : mlsg.getMetrics() ) {
                System.out.print( f + "," );
            }
            System.out.println();
        }
    }

    public ArrayList<MLSGame> getData() { return mlsGames; }
}
