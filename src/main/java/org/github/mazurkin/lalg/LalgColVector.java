package org.github.mazurkin.lalg;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * Column (Mx1, multi-row) vector with immutable dimensions but mutable content
 */
public class LalgColVector {

    public final double[] data;

    public final int rows;

    /**
     * Creates Mx1 multi-row vector with data
     * @param data Values
     */
    public LalgColVector(double[] data) {
        Preconditions.checkNotNull(data, "Array is null");
        this.data = data;

        this.rows = data.length;
        Preconditions.checkArgument(this.rows > 0, "Array has no elements");
    }

    /**
     * Creates zero Mx1 vector
     * @param rows Number of rows
     */
    public LalgColVector(int rows) {
        Preconditions.checkArgument(rows > 0, "Number of rows is invalid: %s", rows);
        this.rows = rows;

        this.data = new double[rows];
    }

    /**
     * Creates same copy of this vector with new data array
     * @return New vector
     */
    public LalgColVector copy() {
        double[] copy = Arrays.copyOf(this.data, this.data.length);
        return new LalgColVector(copy);
    }

    /**
     * Transpose to 1xN vector
     * @return 1xN vector
     */
    public LalgRowVector transpose() {
        return new LalgRowVector(this.data);
    }

    /**
     * Multiply by 1xN vector
     * @param vector 1xN vector
     * @param target MxN matrix
     */
    public void multiplyByVector(LalgRowVector vector, LalgMatrix target) {
        if (this.rows != target.rows) {
            throw new IllegalArgumentException(
                String.format(
                    "This vector's rows and target matrix's rows do not match: %d <> %d",
                    this.rows, target.rows
                )
            );
        }

        if (vector.columns != target.columns) {
            throw new IllegalArgumentException(
                String.format(
                    "Other vector's columns and target matrix's columns do not match: %d <> %d",
                    vector.columns, target.columns
                )
            );
        }

        for (int i = 0, tgtIdx = 0; i < this.rows; i++) {
            double srcVal = this.data[i];

            for (int j = 0; j < vector.columns; j++, tgtIdx++) {
                target.data[tgtIdx] = srcVal * vector.data[j];
            }
        }
    }

}
