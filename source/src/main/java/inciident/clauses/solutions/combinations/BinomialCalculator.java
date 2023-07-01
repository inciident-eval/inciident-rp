
package inciident.clauses.solutions.combinations;


public class BinomialCalculator {

    private final long[][] binomial;
    private final long[] factorial;

    private final int maxK, maxN;

    public BinomialCalculator(int maxK, int maxN) {
        this.maxK = maxK;
        this.maxN = maxN;
        binomial = new long[maxN + 1][maxK + 1];
        factorial = new long[maxK + 1];
    }

    public long factorial(int k) {
        long f = factorial[k];
        if (f == 0) {
            f = 1;
            for (int i = 2; i <= k; i++) {
                f *= i;
            }
            factorial[k] = f;
        }
        return f;
    }

    public long binomial() {
        return binomial(maxN, maxK);
    }

    public long binomial(int n, int k) {
        long b = binomial[n][k];
        if (b == 0) {
            b = computeBinomial(n, k);
            binomial[n][k] = b;
        }
        return b;
    }

    public int[] combination(long index) {
        final int[] combination = new int[maxK];
        for (int i = maxK; i > 0; i--) {
            if (index <= 0) {
                combination[i - 1] = i - 1;
            } else {
                final int p = (int) Math.ceil(Math.pow(index * factorial(i), (1.0 / i)));
                for (int j = p; j <= maxN; j++) {
                    if (binomial(j, i) > index) {
                        combination[i - 1] = j - 1;
                        index -= binomial(j - 1, i);
                        break;
                    }
                }
            }
        }
        return combination;
    }

    public long index(int[] c) {
        long index = 0;
        for (int i = 0; i < maxK; i++) {
            index += binomial(c[i], i + 1);
        }
        return index;
    }

    public static long computeBinomial(int n, int k) {
        if (n < k) {
            return 0;
        }
        if (k > (n - k)) {
            k = n - k;
        }

        long b = k == 0 ? 1 : n;
        for (int i = 1; i < k; i++) {
            b = Math.multiplyExact(b, n - i) / (i + 1);
        }
        return b;
    }
}
