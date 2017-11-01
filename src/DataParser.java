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

    ArrayList<PatientData> patientData = new ArrayList<>();

    public DataParser( String csvFile ) {
        String line;
        String cvsSplitBy = ",";

        try ( BufferedReader br = new BufferedReader( new FileReader( csvFile ) ) ) {
            int count = 0;
            while ( ( line = br.readLine() ) != null ) {

                // use comma as separator 0-11 are symptoms, 12 is outcome.
                String[] patient = line.split( cvsSplitBy );
                double[] symptoms = new double[12];
                for( int i = 0; i < 12; i += 1 ) {
                    symptoms[i] = Double.parseDouble( patient[i] );
                }

                patientData.add( new PatientData( symptoms, Integer.parseInt( patient[12] ) ) );
                count += 1;
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public PatientData getRandomPatient() {
        Random ran = new Random();
        return patientData.get( ran.nextInt( patientData.size() ) );
    }

    public void reportOutcomes() {
        for( PatientData pd : patientData ) {
            System.out.println( pd.outcome );
        }
    }
    public void reportSymptoms() {
        for( PatientData pd : patientData ) {
            for( double d : pd.symptoms ) {
                System.out.print( d + "," );
            }
            System.out.println();
        }
    }

    public ArrayList<PatientData> getData() { return patientData; }
}
