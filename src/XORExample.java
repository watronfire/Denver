import java.util.Random;

/**
 * Created by nate on 7/7/17.
 */

// I'm going to test my neural net initial with an XOR test before moving on to hard data. This class will generate each
// test case
public class XORExample {
    private boolean[] inputs = new boolean[2];
    private boolean output;

    // Essentially generates two random booleans as the inputs, and then calculates the output using the exclusive OR
    // operator.
    public XORExample() {
        Random random = new Random();
        inputs[0] = random.nextBoolean();
        inputs[1] = random.nextBoolean();
        output = inputs[0] ^ inputs[1];
    }

    public boolean[] getInputs() {
        return inputs;
    }
    public boolean getOutput() {
        return output;
    }
}
