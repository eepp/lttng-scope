/*******************************************************************************
 * Copyright (c) 2015, 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Patrick Tasse - Initial API and implementation
 *******************************************************************************/

package org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Interface to an object which is capable of providing marker events.
 */
public interface IMarkerEventSource {

    /**
     * Gets the list of marker categories that this object provides.
     *
     * @return The list of marker categories
     */
    @NonNull List<@NonNull String> getMarkerCategories();

    /**
     * Gets the list of marker events of a specific category that intersect the
     * given time range (inclusively).
     * <p>
     * The list should also include the nearest previous and next markers that
     * do not intersect the time range.
     *
     * @param category
     *            The marker category
     * @param startTime
     *            Start of the time range
     * @param endTime
     *            End of the time range
     * @param resolution
     *            The resolution
     * @param monitor
     *            The progress monitor object
     * @return The list of marker events
     */
    @NonNull List<@NonNull IMarkerEvent> getMarkerList(@NonNull String category, long startTime, long endTime, long resolution, @NonNull IProgressMonitor monitor);

}
