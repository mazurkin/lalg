package org.github.mazurkin.lalg;

/**
 * Collection of operations on each element of a data array
 */
public final class LalgOperations {

    private LalgOperations() {
        // utility class
    }

    /**
     * Calculate max(abs(data))
     * @param data Data array to scan
     * @return Result value
     */
    public static double computeNormMax(double[] data) {
        double max = 0.0;

        for (double item : data) {
            double v = Math.abs(item);

            if (v > max) {
                max = v;
            }
        }

        return max;
    }

    /**
     * Calculate max(data)
     * @param data Data array to scan
     * @return Result value
     */
    public static double computeMax(double[] data) {
        double max = Double.MIN_NORMAL;

        for (double item : data) {
            if (item > max) {
                max = item;
            }
        }

        return max;
    }

    /**
     * Calculate min(data)
     * @param data Data array to scan
     * @return Result value
     */
    public static double computeMin(double[] data) {
        double min = Double.MAX_VALUE;

        for (double item : data) {
            if (item < min) {
                min = item;
            }
        }

        return min;
    }

    /**
     * Calculate sum(data)
     * @param data Data array to scan
     * @return Result value
     */
    public static double computeSum(double[] data) {
        double sum = 0.0;

        for (double item : data) {
            sum += item;
        }

        return sum;
    }

    /**
     * Calculate dot product of two arrays
     * @param data1 Data array 1
     * @param data2 Data array 2
     * @return Result value
     * @see <a href="https://en.wikipedia.org/wiki/Dot_product">Dot product</a>
     */
    public static double computeDotProduct(double[] data1, double[] data2) {
        if (data1.length != data2.length) {
            throw new IllegalArgumentException(
                String.format("Array length mismatch: %d <> %d", data1.length, data2.length)
            );
        }

        double sum = 0.0;

        for (int i = 0, limit = data1.length; i < limit; i++) {
            sum += data1[i] * data2[i];
        }

        return sum;
    }

    /**
     * Calculate element-wise (hadamard) product of two arrays (data1[i] * data2[i]) -> target[i]
     * @param data1 Data array 1
     * @param data2 Data array 2
     * @param target Target array (could be either data1 or data2 or even other array)
     * @see <a href="https://en.wikipedia.org/wiki/Hadamard_product_(matrices)">Hadamard product (matrices)</a>
     */
    public static void inplaceMul(double[] data1, double[] data2, double[] target) {
        if (data1.length != data2.length) {
            throw new IllegalArgumentException(
                String.format("Array length mismatch (data1, data2): %d <> %d", data1.length, data2.length)
            );
        }

        if (data1.length != target.length) {
            throw new IllegalArgumentException(
                String.format("Array length mismatch (data1, target): %d <> %d", data1.length, target.length)
            );
        }

        for (int i = 0, limit = data1.length; i < limit; i++) {
            target[i] = data1[i] * data2[i];
        }
    }

    /**
     * Calculate element-wise sum of two arrays (data1[i] + data2[i]) -> target[i]
     * @param data1 Data array 1
     * @param data2 Data array 2
     * @param target Target array (could be either data1 or data2 or even other array)
     */
    public static void inplaceAdd(double[] data1, double[] data2, double[] target) {
        if (data1.length != data2.length) {
            throw new IllegalArgumentException(
                String.format("Array length mismatch (data1, data2): %d <> %d", data1.length, data2.length)
            );
        }

        if (data1.length != target.length) {
            throw new IllegalArgumentException(
                String.format("Array length mismatch (data1, target): %d <> %d", data1.length, target.length)
            );
        }

        for (int i = 0, limit = data1.length; i < limit; i++) {
            target[i] = data1[i] + data2[i];
        }
    }

    /**
     * Calculate element-wise substraction of two arrays (data1[i] - data2[i]) -> target[i]
     * @param data1 Data array 1
     * @param data2 Data array 2
     * @param target Target array (could be either data1 or data2 or even other array)
     */
    public static void inplaceSub(double[] data1, double[] data2, double[] target) {
        if (data1.length != data2.length) {
            throw new IllegalArgumentException(
                String.format("Array length mismatch (data1, data2): %d <> %d", data1.length, data2.length)
            );
        }

        if (data1.length != target.length) {
            throw new IllegalArgumentException(
                String.format("Array length mismatch (data1, target): %d <> %d", data1.length, target.length)
            );
        }

        for (int i = 0, limit = data1.length; i < limit; i++) {
            target[i] = data1[i] - data2[i];
        }
    }

    /**
     * Rectification (inplace)
     * @param data Data array to modify
     */
    public static void inplaceRelu(double[] data) {
        for (int i = 0, limit = data.length; i < limit; i++) {
            double v = data[i];

            if (v < 0.0) {
                data[i] = 0.0;
            }
        }
    }

    /**
     * Soft max (inplace)
     * @param data Data array to modify
     */
    public static void inplaceSoftMax(double[] data) {
        double offset = computeNormMax(data);

        double sum = 0.0;

        for (int i = 0, limit = data.length; i < limit; i++) {
            double v = Math.exp(data[i] - offset);
            data[i] = v;
            sum += v;
        }

        for (int i = 0, limit = data.length; i < limit; i++) {
            data[i] = data[i] / sum;
        }
    }

    /**
     * Cumulative sum (inplace)
     * @param data Data array to modify
     */
    public static void inplaceCumSum(double[] data) {
        double sum = 0.0;

        for (int i = 0, limit = data.length; i < limit; i++) {
            sum += data[i];
            data[i] = sum;
        }
    }
}
