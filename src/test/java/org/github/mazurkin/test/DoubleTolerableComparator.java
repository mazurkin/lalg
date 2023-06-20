package org.github.mazurkin.test;

import com.google.common.base.Preconditions;

import java.util.Comparator;

public class DoubleTolerableComparator implements Comparator<Double> {

    private final double tolerance;

    public DoubleTolerableComparator(double tolerance) {
        Preconditions.checkArgument(tolerance >= 0.0, "Tolerance must be positive: %s", tolerance);
        this.tolerance = tolerance;
    }

    @Override
    public int compare(Double o1, Double o2) {
        double v1 = o1;
        double v2 = o2;

        double absMean = Math.abs((v1 + v2) / 2);
        double absDiff = Math.abs(v1 - v2);

        if ((absDiff / absMean) <= this.tolerance) {
            return 0;
        } else {
            return Double.compare(v1, v2);
        }
    }
}
