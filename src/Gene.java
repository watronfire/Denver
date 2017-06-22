/**
 * Created by nate on 6/20/17.
 */

// Both types of genes, both connections and nodes, require identification globally. Mostly for crossover.
// It's possible that there may be other uses to this.
public class Gene implements Comparable<Gene> {

    int innovation;

    private static int globalInnovation = 1;
    public static int getGlobalInnovation() {
        return globalInnovation++;
    }

    public int getInnovation() {
        return innovation;
    }


    // Method responsible for comparing two gene objects. Needs to be done so that arrayList of gene objects can be
    // sorted.
    @Override
    public int compareTo( Gene otherGene ) {
        return this.getInnovation() - otherGene.getInnovation();
    }

}
