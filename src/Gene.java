/**
 * Created by nate on 6/20/17.
 */

// Both types of genes, both connections and nodes, require identification globally. Mostly for crossover.
// It's possible that there may be other uses to this.
abstract class Gene {

    private static int globalInnovation = 1;
    public static int getGlobalInnovation() {
        return globalInnovation++;
    }

}
