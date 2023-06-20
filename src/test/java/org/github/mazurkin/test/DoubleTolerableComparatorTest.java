package org.github.mazurkin.test;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

class DoubleTolerableComparatorTest {

    @Test
    void test() {
        Comparator<Double> comparator = new DoubleTolerableComparator(0.01);

        assertThat(comparator.compare(+10.1, +10.2))
            .isEqualTo(0);

        assertThat(comparator.compare(-10.1, -10.2))
            .isEqualTo(0);

        assertThat(comparator.compare(-10.1, +10.2))
            .isEqualTo(-1);

        assertThat(comparator.compare(+10.1, -10.2))
            .isEqualTo(+1);

        assertThat(comparator.compare(+9.1, +9.2))
            .isEqualTo(-1);

        assertThat(comparator.compare(-9.1, -9.2))
            .isEqualTo(+1);
    }
}
