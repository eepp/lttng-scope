/*******************************************************************************
 * Copyright (c) 2016 EfficiOS Inc., Jonathan Rajotte-Julien
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.lami.ui.viewers;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

/**
 * BigDecimal based range representation
 *
 * @author Jonathan Rajotte-Julien
 */
public class LamiGraphRange {

    private final BigDecimal fMinimum;
    private final BigDecimal fMaximum;
    private final BigDecimal fRange;

    /**
     * Constructor
     *
     * @param minimum
     *            The minimum value of the range
     * @param maximum
     *            The maximum value of the range
     */
    public LamiGraphRange(BigDecimal minimum, BigDecimal maximum) {
        fMinimum = minimum;
        fMaximum = maximum;
        fRange = requireNonNull(maximum.subtract(minimum));
    }

    /**
     * @return the minimum value of the range
     */
    public BigDecimal getMinimum() {
        return fMinimum;
    }

    /**
     * @return the maximum value of the range
     */
    public BigDecimal getMaximum() {
        return fMaximum;
    }

    /**
     * @return the range delta
     */
    public BigDecimal getDelta() {
        return fRange;
    }
}