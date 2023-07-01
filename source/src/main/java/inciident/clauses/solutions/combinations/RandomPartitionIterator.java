
package inciident.clauses.solutions.combinations;

import java.util.Random;


public class RandomPartitionIterator extends PartitionIterator {

    public RandomPartitionIterator(int t, int size) {
        this(t, size, new Random(42));
    }

    public RandomPartitionIterator(int t, int size, Random random) {
        super(t, size, 4);

        for (int i = 0; i < dim.length; i++) {
            final int[] dimArray = dim[i];
            for (int j = dimArray.length - 1; j >= 0; j--) {
                final int index = random.nextInt(j + 1);
                final int a = dimArray[index];
                dimArray[index] = dimArray[j];
                dimArray[j] = a;
            }
        }
    }
}
