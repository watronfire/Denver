// Object which will be used to stor patient data in the spine neural net.

public class PatientData {
    // Contains the outcome of the patients case. Either abnormal(1), or normal(0)
    int outcome;
    // Contains the 12 symptoms of the patient. Detailed in the kaggle entry.
    double[] symptoms = new double[12];

    public PatientData( double[] symptoms, int outcome ) {
        this.symptoms = symptoms;
        this.outcome = outcome;
    }

    public int getOutcome() { return outcome; }
    public double[] getSymptoms() { return  symptoms; }
}
