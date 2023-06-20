package org.github.mazurkin.lalg;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * Row (1xN, multi-column) vector with immutable dimensions but mutable content
 */
public class LalgRowVector {

    public final double[] data;

    public final int columns;

    /**
     * Creates 1xN multi-column vector with data
     * @param data Values
     */
    public LalgRowVector(double[] data) {
        Preconditions.checkNotNull(data, "Array is null");
        this.data = data;

        this.columns = data.length;
        Preconditions.checkArgument(this.columns > 0, "Array has no elements");
    }

    /**
     * Creates zero 1xN vector
     * @param columns Number of columns
     */
    public LalgRowVector(int columns) {
        Preconditions.checkArgument(columns > 0, "Number of columns is invalid: %s", columns);
        this.columns = columns;

        this.data = new double[columns];
    }

    /**
     * Creates same copy of this vector with new data array
     * @return New vector
     */
    public LalgRowVector copy() {
        double[] copy = Arrays.copyOf(this.data, this.data.length);
        return new LalgRowVector(copy);
    }

    /**
     * Transpose to Nx1 vector
     * @return Nx1 vector
     */
    public LalgColVector transpose() {
        return new LalgColVector(this.data);
    }

    /**
     * Multiply this 1xN vector by the <em>transposed</em> matrix MxN and put result to the target 1xM vector
     *
     * @param transposedMatrix Transposed MxN matrix (original matrix was NxM)
     * @param target Target 1xM vector
     */
    public void multiplyByTransposedMatrix(LalgMatrix transposedMatrix, LalgRowVector target) {
        if (this.columns != transposedMatrix.columns) {
            throw new IllegalArgumentException(
                String.format(
                    "Source vector columns and transposed matrix columns do not match: %d <> %d",
                    this.columns, transposedMatrix.columns
                )
            );
        }

        if (target.columns != transposedMatrix.rows) {
            throw new IllegalArgumentException(
                String.format(
                    "Target vector columns and transposed matrix rows do not match: %s <> %s",
                    target.columns, transposedMatrix.rows
                )
            );
        }

        for (int i = 0, mtxOffset = 0; i < transposedMatrix.rows; i++) {
            double sum = 0.0;

            for(int j = 0; j < this.columns; j++, mtxOffset++) {
                sum += this.data[j] * transposedMatrix.data[mtxOffset];
            }

            target.data[i] = sum;
        }
    }

    /**
     * Multiply by Nx1 vector
     * @param vector Nx1 vector
     * @return scalar result
     */
    public double multiplyByVector(LalgColVector vector) {
        if (this.columns != vector.rows) {
            throw new IllegalArgumentException(
                String.format(
                    "This vector's columns and other vector's rows do not match: %d <> %d",
                    this.columns, vector.rows
                )
            );
        }

        double sum = 0.0;

        for (int i = 0; i < columns; i++) {
            sum += this.data[i] * vector.data[i];
        }

        return sum;
    }

}
