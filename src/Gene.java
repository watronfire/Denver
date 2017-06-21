/**
 * Created by nate on 6/20/17.
 */
abstract class Gene {

    private static int globalInnovation = 1;

    public static int getGlobalInnovation() {
        return globalInnovation++;
    }
}
