/**********************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************/

package org.eclipse.tracecompass.tmf.core.trace;

/**
 * An interface that trace classes can implement if they have a known size, so
 * that reading progress can be shown.
 *
 * @author Matthew Khouzam
 */
public interface ITmfTraceKnownSize {

    /**
     * Get the size of the trace. The units of this value are not important, but
     * they should always be the same as {@link #progress()}
     *
     * @return the size of the trace. This can change from one call to the
     *         other, but a later call to this method should not see a decrease
     *         in size.
     */
    int size();

    /**
     * How much of the trace is read. The units of this value are not important,
     * but they should always be the same as {@link #size()}
     *
     * @return how much of the trace is read. This should not exceed
     *         {@link #size()}
     */
    int progress();
}
