package org.github.mazurkin.lalg;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * MxN matrix with immutable dimensions but mutable content
 */
public class LalgMatrix {

    public final double[] data;

    public final int rows;

    public final int columns;

    /**
     * Creates a matrix from the 2D array
     *
     * @param data Values
     */
    public LalgMatrix(double[][] data) {
        Preconditions.checkNotNull(data, "Array is null");

        this.rows = data.length;
        Preconditions.checkArgument(this.rows > 0, "Array has no elements");

        this.columns = data[0].length;
        Preconditions.checkArgument(this.columns > 0, "Array has no columns");

        this.data = new double[this.rows * this.columns];

        for (int i = 0, offset = 0; i < this.rows; i++, offset += this.columns) {
            double[] row = data[i];

            Preconditions.checkArgument(row.length == this.columns,
                "Array has different size of rows: %s and %s", row.length, this.columns);

            System.arraycopy(row, 0, this.data, offset, this.columns);
        }
    }

    /**
     * Creates a matrix from the 1D array
     *
     * @param data Values
     * @param rows Number of rows
     * @param columns Number of columns
     */
    public LalgMatrix(double[] data, int rows, int columns) {
        Preconditions.checkNotNull(data, "Array is null");

        Preconditions.checkArgument(data.length > 0, "Array is empty");

        Preconditions.checkArgument(data.length == rows * columns,
            "Array size mismatch: %s <> (%s * %s)", data.length, rows, columns);

        this.rows = rows;
        this.columns = columns;
        this.data = data;
    }

    /**
     * Creates a zero matrix
     *
     * @param rows Number of rows
     * @param columns Number of columns
     */
    public LalgMatrix(int rows, int columns) {
        Preconditions.checkArgument(rows > 0, "Number of rows is invalid: %s", rows);
        this.rows = rows;

        Preconditions.checkArgument(columns > 0, "Number of columns is invalid: %s", columns);
        this.columns = columns;

        this.data = new double[rows * columns];
    }

    /**
     * Makes same copy of this matrix with new data array
     *
     * @return New matrix
     */
    public LalgMatrix copy() {
        double[] copy = Arrays.copyOf(this.data, this.data.length);
        return new LalgMatrix(copy, rows, columns);
    }

    /**
     * Makes transposed copy of this matrix with new data array
     *
     * @param target Target matrix
     */
    public void transpose(LalgMatrix target) {
        if (this.columns != target.rows) {
            throw new IllegalArgumentException(
                String.format(
                    "This matrix's columns and other matrix's rows do not match: %d <> %d",
                    this.columns, target.rows
                )
            );
        }

        if (this.rows != target.columns) {
            throw new IllegalArgumentException(
                String.format(
                    "This matrix's rows and other matrix's columns do not match: %d <> %d",
                    this.rows, target.rows
                )
            );
        }

        for (int i = 0, srcIdx = 0; i < this.rows; i++) {
            for (int j = 0, tgtIdx = i; j < this.columns; j++, srcIdx++, tgtIdx += rows) {
                target.data[tgtIdx] = this.data[srcIdx];
            }
        }
    }

    /**
     * Convert 1xN matrix to 1xN vector
     *
     * @return 1xN vector
     */
    public LalgRowVector toVector1N() {
        if (rows > 1) {
            throw new IllegalStateException(
                String.format("Can't convert to 1N vector the matrix with %d rows", rows)
            );
        }

        return new LalgRowVector(this.data);
    }

    /**
     * Convert Nx1 matrix to Nx1 vector
     *
     * @return Nx1 vector
     */
    public LalgColVector toVectorN1() {
        if (columns > 1) {
            throw new IllegalStateException(
                String.format("Can't convert to N1 vector the matrix with %d columns", columns)
            );
        }

        return new LalgColVector(this.data);
    }

    /**
     * Convert matrix to scalar
     *
     * @return Scalar value
     */
    public double toScalar() {
        if (rows > 1) {
            throw new IllegalStateException(
                String.format("Can't convert to scalar the matrix with %d rows", rows)
            );
        }

        if (columns > 1) {
            throw new IllegalStateException(
                String.format("Can't convert to scalar the matrix with %d columns", columns)
            );
        }

        return this.data[0];
    }

    /**
     * Multiply this MxN matrix by Nx1 multi-row vector computing Mx1 multi-row vector
     *
     * @param vector Nx1 vector to multiply by
     * @param target Result Mx1 vector
     */
    public void multiplyByVector(LalgColVector vector, LalgColVector target) {
        if (this.columns != vector.rows) {
            throw new IllegalArgumentException(
                String.format(
                    "This matrix's columns and other vector's rows do not match: %d <> %d",
                    this.columns, vector.rows
                )
            );
        }

        if (this.rows != target.rows) {
            throw new IllegalArgumentException(
                String.format(
                    "This matrix's rows and target vector's rows do not match: %d <> %d",
                    this.rows, target.rows
                )
            );
        }

        for (int i = 0, srcIdx = 0; i < this.rows; i++) {
            double sum = 0.0;

            for (int j = 0; j < this.columns; j++, srcIdx++) {
                sum += this.data[srcIdx] * vector.data[j];
            }

            target.data[i] = sum;
        }
    }

    /**
     * Multiply this MxN matrix by other <em>transposed</em> KxN matrix and put result into target MxK matrix
     *
     * @param transposedMatrix transposed KxN matrix to multiply by (the original matrix was NxK)
     * @param target Result MxK matrix
     */
    public void multiplyByTransposedMatrix(LalgMatrix transposedMatrix, LalgMatrix target) {
        if (this.columns != transposedMatrix.columns) {
            throw new IllegalArgumentException(
                String.format(
                    "This matrix's columns and other matrix's columns do not match: %d <> %d",
                    this.columns, transposedMatrix.columns
                )
            );
        }

        if (this.rows != target.rows) {
            throw new IllegalArgumentException(
                String.format(
                    "This matrix's rows and target matrix's rows do not match: %d <> %d",
                    this.rows, target.rows
                )
            );
        }

        if (transposedMatrix.rows != target.columns) {
            throw new IllegalArgumentException(
                String.format(
                    "Other matrix's rows and target matrix's columns do not match: %d <> %d",
                    transposedMatrix.columns, target.columns
                )
            );
        }

        for (int thisRow = 0, targetIdx = 0, thisOfst = 0; thisRow < this.rows; thisRow++, thisOfst += this.columns) {
            for (int thatRow = 0, thatIdx = 0; thatRow < transposedMatrix.rows; thatRow++, targetIdx++) {
                double sum = 0.0;

                for (int i = 0, thisIdx = thisOfst; i < this.columns; i++, thisIdx++, thatIdx++) {
                    sum += this.data[thisIdx] * transposedMatrix.data[thatIdx];
                }

                target.data[targetIdx] = sum;
            }
        }
    }

}
